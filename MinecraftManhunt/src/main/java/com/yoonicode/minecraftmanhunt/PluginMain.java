package com.yoonicode.minecraftmanhunt;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class PluginMain extends JavaPlugin {

    Logger logger;

    @Override
    public void onEnable() {
        logger = getLogger();
        logger.info("Minecraft Manhunt plugin enabled!");
        getServer().getPluginManager().registerEvents(new PluginListener(this), this);

        PluginCommands commands = new PluginCommands(this);
        for(String command : PluginCommands.registeredCommands){
            this.getCommand(command).setExecutor(commands);
        }


    }

    @Override
    public void onDisable() {
        logger.info("Minecraft Manhunt plugin disabled!");
    }

}
