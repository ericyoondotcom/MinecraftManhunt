package com.yoonicode.minecraftmanhunt;

import org.bukkit.configuration.file.FileConfiguration;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.*;
import java.util.UUID;

public class AnalyticsManager {
    public static final String MEASUREMENT_ID = "G-S6DY8TGBPJ";
    public static final String API_SECRET = "46lPT72nQ2aOErUOqDBwNA";
    public static final boolean DEBUG_MODE = false;
    public static final boolean VALIDATION_MODE = false;

    public boolean enabled;
    PluginMain main;
    String serverUUID;

    public AnalyticsManager(PluginMain main){
        this.main = main;
        FileConfiguration config = main.getConfig();
        enabled = config.getBoolean("sendUsageData", false);
        main.logger.info("Send usage data: " + enabled);
        if(enabled){
            serverUUID = main.getConfig().getString("uuid", "");
            if(serverUUID.length() == 0){
                serverUUID = UUID.randomUUID().toString();
                main.logger.info("Server uuid is empty. Setting to " + serverUUID);
                config.set("uuid", serverUUID);
                main.saveConfig();
            }else{
                main.logger.info("Found server uuid: " + serverUUID);
            }
        }
    }

    public void sendEvent(String eventName, JsonObject params){
        if(!enabled) return;
        HttpURLConnection connection = null;
        try {
            URL url = new URL("https://www.google-analytics.com/" + (VALIDATION_MODE ? "debug/" : "") + "mp/collect?measurement_id=" + MEASUREMENT_ID + "&api_secret=" + API_SECRET + (DEBUG_MODE ? "&_dbg=1" : ""));
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            OutputStream stream = connection.getOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(stream, "UTF-8");
            JsonObject eventObj = Json.createObjectBuilder()
                .add("name", eventName)
                .add("params", params)
                .build();
            JsonArray eventsArray = Json.createArrayBuilder()
                .add(eventObj)
                .build();
            JsonObject json = Json.createObjectBuilder()
                .add("client_id", serverUUID)
                .add("events", eventsArray)
                .build();
            if(DEBUG_MODE) main.logger.info(json.toString());
            writer.write(json.toString());
            writer.close();

            if(DEBUG_MODE){
                BufferedInputStream inputStream = new BufferedInputStream(connection.getInputStream());
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                int resInt = inputStream.read();
                while(resInt != -1){
                    byteStream.write((byte)resInt);
                    resInt = inputStream.read();
                }
                String result = byteStream.toString();
                main.logger.info(result);
            }

        } catch(Exception e){
            main.logger.info("Exception sending analytics event: " + e.toString());
        } finally {
            if(connection != null) connection.disconnect();
        }
    }
}
