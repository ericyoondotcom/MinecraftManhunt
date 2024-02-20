package com.yoonicode.minecraftmanhunt;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.ItemStack;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class TrackManager extends AudioEventAdapter implements Listener {
    long piratesCooldownMillis = 0;

    public HashMap<String, String> trackURLs;

    MusicManager musicManager;
    PluginMain main;
    boolean autoEnabled = false;
    boolean specialPlaying = false;

    public enum DangerLevel {
        Chasing,
        InSight,
        RunnerInNether,
        Stealth,
        Approaching,
        FarAway
    }

    public DangerLevel dangerLevel;


    public HashMap<String, AudioTrack> tracks = new HashMap<String, AudioTrack>();

    boolean armorObtainedByHunters = false;
    long piratesLastPlaytime = 0;
    boolean runnersHaveTradedWithPiglins = false;
    boolean ironObtainedByRunners = false;

    public TrackManager(MusicManager musicManager, PluginMain main){
        this.musicManager = musicManager;
        this.main = main;
        musicManager.player.addListener(this);

        // Undocumented config option
        piratesCooldownMillis = main.getConfig().getLong("piratesCooldown", 300000);

//        for(Map.Entry<String, String> i : trackURLs.entrySet()){
//            loadTrack(i.getKey(), new TrackLoadHandler() {
//                @Override
//                public void onTrackLoaded() {}
//            });
//        }

        trackURLs = new HashMap<String, String>() {{
            put("intro", main.getConfig().getString("intro", "https://www.youtube.com/watch?v=lDjfRqkUlN4")); // Epic Dawn - Bobby Cole
            put("intro2", main.getConfig().getString("intro2", "https://www.youtube.com/watch?v=Ht7cNljCYM4")); // Forgotten Ones - Will Van De Crommert via. Cinematic Sky
            put("headstart", main.getConfig().getString("headstart", "https://www.youtube.com/watch?v=ILtYQftxJ3k")); // Trance Music for Racing Game - Bobby Cole
            put("pirates", main.getConfig().getString("pirates", "https://www.youtube.com/watch?v=27mB8verLK8")); // Pirates of the Caribbean theme song
            put("gatheringresources", main.getConfig().getString("gatheringresources", "https://www.youtube.com/watch?v=wlRkXTqy5Ng")); // Do the Funky Strut - Bobby Cole
            put("montage", main.getConfig().getString("montage", "https://www.youtube.com/watch?v=BezpUnoZObw")); // The Elevator Bossa Nova - Bensound
            put("xmas", main.getConfig().getString("xmas", "https://www.youtube.com/watch?v=R5swgaETU1g")); // Jingle Bells - Bill Robuck & Jerrold W Lambert

            put("relaxing", main.getConfig().getString("relaxing", "https://www.youtube.com/watch?v=ipCeVg-SA-Y")); // Inhale and Exhale - For Peace of Mind and Serenity
            put("fun", main.getConfig().getString("fun", "https://www.youtube.com/watch?v=O9cWfV_3J7o")); // Playtime - Peter Godfrey
            // put("chill", "https://www.youtube.com/watch?v=eSNXvU-kowc"); // Crime and Robbery Music
            put("upbeat", main.getConfig().getString("upbeat", "https://www.youtube.com/watch?v=rxfqhAOfIqo")); // Ready and Go - Diego Martinez
            put("swing", main.getConfig().getString("swing", "https://www.youtube.com/watch?v=wJ5Ip9qs9iw")); // Snap Swing - Diego Martinez
            put("jazz", main.getConfig().getString("jazz", "https://www.youtube.com/watch?v=R_-44VsmTmI")); // Heist Prop Montage - Rob McAllister
            put("upbeat-bite", main.getConfig().getString("upbeat-bite", "https://www.youtube.com/watch?v=hn7k7z_heOw")); // Brain Short Circuit - Neil Cross
            put("preparing-safe2", main.getConfig().getString("preparing-safe2", "https://www.youtube.com/watch?v=A6L-LNf5mhE")); // On Queue 2 - Bruce Zimmerman

            put("preparing-safe", main.getConfig().getString("preparing-safe", "https://www.youtube.com/watch?v=gehk17hjEdU")); // Race Against Time - Ceiri Torjussen
            put("preparing-danger", main.getConfig().getString("preparing-danger", "https://www.youtube.com/watch?v=ORHevqbtJFE")); // Action Preparation - Jason Donnelly
            put("lowdanger", main.getConfig().getString("lowdanger", "https://www.youtube.com/watch?v=yWw0_rUlfIA")); // White Lines - Zac Nelson
            put("journey", main.getConfig().getString("journey", "https://www.youtube.com/watch?v=7we8rs_YF5w")); // Crucial Conflict - Westar Music
            put("cinematic", main.getConfig().getString("cinematic", "https://www.youtube.com/watch?v=K1p5TCW2tp0")); // Hidden in the Corner - Marc Robillard
            put("premonition", main.getConfig().getString("premonition", "https://www.youtube.com/watch?v=pNxcUi50Ge0")); // Evolution of Man - Linus Lau
            // put("orchestral", "https://dm0qx8t0i9gc9.cloudfront.net/previews/audio/HNxwBHlArk43bm5tw/audioblocks-spearfisher-ft-cicely-parnas-_blood-and-strings_hope-and-heisenberg_inst_rAkI7RJ4v_NWM.mp3"); // Hope and Heisenberg - Lance Conrad

            put("risingaction", main.getConfig().getString("risingaction", "https://www.youtube.com/watch?v=ghAeHHTID2k")); // Strange Things - Zac Nelson
            put("rhythmic", main.getConfig().getString("rhythmic", "https://www.youtube.com/watch?v=vWghhruheNk")); // Escape Theme - Udeze Ukwuoma via. Von Neumann Effect

            put("nether", main.getConfig().getString("nether", "https://www.youtube.com/watch?v=tQdAiG29HiM")); // Music For a Killer - Bobby Cole

            put("spooked", main.getConfig().getString("spooked", "https://www.youtube.com/watch?v=3kclVzQ3S4M")); // Re-Animation - Peter Godfrey / When Bats Fly - Neil Cross
            put("spooked2", main.getConfig().getString("spooked2", "https://www.youtube.com/watch?v=UZ7OcaFTiA4")); // The Final Decision - Clark Aboud
            // put("tense", "https://www.youtube.com/watch?v=Vv9-cxbacOg"); // Tension in the Air - Bobby Cole
            // put("tense2", "https://dm0qx8t0i9gc9.cloudfront.net/previews/audio/BsTwCwBHBjzwub4i4/bbc-051117-Feel-the-Tension_NWM.mp3"); // Feel the Tension - Bobby Cole
            put("approaching", main.getConfig().getString("approaching", "https://www.youtube.com/watch?v=e3J-A7ze038")); // Thinking About Murder - Boby Cole
            put("dramatic", main.getConfig().getString("dramatic", "https://www.youtube.com/watch?v=-u7MW_KkTWU")); // Dramatic Movie Opening - Bobby Cole

            put("plotting", main.getConfig().getString("plotting", "https://www.youtube.com/watch?v=RXyYt8kx740")); // Thinking and Tension - Bobby Cole
            put("danger", main.getConfig().getString("danger", "https://www.youtube.com/watch?v=V-5MQywZlaw")); // Dramatic Anticipation (Alternative version) - PremiumTrax
            put("danger2", main.getConfig().getString("danger2", "https://www.youtube.com/watch?v=5y4b7jDXf_E")); // Dark Time Ticking - Robert Valenti

            put("discovered", main.getConfig().getString("discovered", "https://www.youtube.com/watch?v=FDTY6EILSLc")); // Evidence - Raphael Costa
            put("found", main.getConfig().getString("found", "https://www.youtube.com/watch?v=YMwQQJ0ChCU")); // On The Killer's Trail - William Pearson & Robert Watson

            put("intense", main.getConfig().getString("intense", "https://www.youtube.com/watch?v=VNTLefY9rT0")); // Promo Upbeat Intense Racer 1 - Jermaine Stegall
            put("intense2", main.getConfig().getString("intense2", "https://www.youtube.com/watch?v=KwxIN6-BNWs")); // Fight to the Death - Bobby Cole
            put("conflict", main.getConfig().getString("conflict", "https://www.youtube.com/watch?v=GaWcNDK9N10")); // Dangerous Action - Mikael Manvelyan
            // on runner hit, less than two pieces of armor
            put("chase", main.getConfig().getString("chase", "https://www.youtube.com/watch?v=CX9wFdExF_k")); // Navajo Race - Bryan Steele
            put("chase2", main.getConfig().getString("chase2", "https://www.youtube.com/watch?v=0TAFhSZXOjI")); // The Tribal Chase Cue - Bobby Cole

            // on runner hit, more than two pieces of armor
            put("epicwar", main.getConfig().getString("epicwar", "https://www.youtube.com/watch?v=_UBZmrQwD9o")); // There Is No Escape - Hollywood Film Music
            put("endwar", main.getConfig().getString("endwar", "https://www.youtube.com/watch?v=0EsBItv1Pns")); // Heist Gone Wrong - Clark Aboud
            put("fighting", main.getConfig().getString("fighting", "https://www.youtube.com/watch?v=S4MC7QdayXc")); // Halo - Michael Vignola
            put("fighting-upbeat", main.getConfig().getString("fighting-upbeat", "https://www.youtube.com/watch?v=0HD3rQ64YJw")); // Space Expansion Modern Retrowave - Oleksandr Koltsov
            put("heroic", main.getConfig().getString("heroic", "https://www.youtube.com/watch?v=eGojBSVYHZA")); // Heroic Fireworks - Paul Werner

            // On hunter kill by runner
            put("beastmode", main.getConfig().getString("beastmode", "https://www.youtube.com/watch?v=4rGl1KXV4eA")); // Dominating - Ray Aley

            put("resolution", main.getConfig().getString("resolution", "https://www.youtube.com/watch?v=B4K7Hqv4vts")); // Out of the Skies, Under the Earth - Chris Zabriskie
            put("resolution2", main.getConfig().getString("resolution2", "https://www.youtube.com/watch?v=eM6WxujEGy8")); // Divider - Chris Zabriskie

            // On runner death
            put("loss", main.getConfig().getString("loss", "https://www.youtube.com/watch?v=9Fx314TyuWI")); // Mystery Collection - Lonesome Piano - D. Silverstone
            put("sad", main.getConfig().getString("sad", "https://www.youtube.com/watch?v=OK7a4EQVRc0")); // Sad Goodbyes - Mark Kueffner

            // put("timesup", "https://media.proudmusiclibrary.com/en/file/stream/c6a2a45eae97c0de11a11cad286a152f/0/220405.mp3"); // Clock Is Ticking - Benny Hawes
            put("finale", main.getConfig().getString("finale", "https://www.youtube.com/watch?v=rQUXUdD3B-4")); // Reach Beyond - Paul Werner
        }};
    }

    public void loadTrack(String trackName, String url, TrackLoadHandler callback){
        musicManager.playerManager.loadItemOrdered(musicManager, url, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                tracks.put(trackName, track);
                main.logger.info("Loaded track " + trackName);
                callback.onTrackLoaded();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                main.logger.warning("Playlists not supported");
            }

            @Override
            public void noMatches() {
                main.logger.warning("Track " + trackName + " not found: " + url);
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                main.logger.warning("Load track " + trackName + " failed: " + exception.getMessage());
                exception.printStackTrace();
            }
        });
    }

    public void loadTrack(String trackName, TrackLoadHandler callback){
        loadTrack(trackName, trackURLs.get(trackName), callback);
    }

    public String parseCommand(String argument){
        if(argument.length() == 0){
            return "Specify a track to play!";
        }
        if(argument.equalsIgnoreCase("forceupdate")){
            specialPlaying = false;
            if(!autoEnabled) return "Automatic music is disabled. Use /music auto first.";
            if(!main.commands.gameIsRunning) return "Cannot use this command when no game is running. Use /start first.";
            playDangerLevelTrack();
            return "Forcing music update to match danger level.";
        }
        if(argument.equalsIgnoreCase("auto")){
            autoEnabled = true;
            return "Automatic music enabled";
        }
        if(argument.equalsIgnoreCase("list")){
            String ret = "Music choices: ";
            for(String i : trackURLs.keySet()){
                ret += i + ", ";
            }
            return ret;
        }
        if(argument.equalsIgnoreCase("stop")){
            musicManager.stopTrack();
            autoEnabled = false;
            return "Stopped music";
        }
        if(argument.equalsIgnoreCase("debug")){
            String ret = "";
            if(dangerLevel != null){
                ret += "Level: " + dangerLevel.toString() + ", ";
            }
            ret += "playing special: " + specialPlaying;
            ret += ", auto enabled: " + autoEnabled;
            return ret;
        }
        if(!trackURLs.containsKey(argument.toLowerCase())){
            return "Track " + argument + " was not found. Use /music list to see a list of valid tracks.";
        }

        autoEnabled = false;
        playTrack(argument.toLowerCase());

        JsonObjectBuilder eventParams = Json.createObjectBuilder()
                .add("track", argument.toLowerCase());
        main.analytics.sendEvent("manual_track_played", eventParams);
        return "Playing track " + argument;
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track){
        main.logger.info("Audio track start callback called");
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
//        super.onTrackEnd(player, track, endReason);
        main.logger.info("Track ended for reason: " + endReason.toString());
        if(!autoEnabled) return;
        if(endReason == AudioTrackEndReason.FINISHED){
            specialPlaying = false;
            if(endReason.mayStartNext){
                playDangerLevelTrack();
            }
        }
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        main.logger.warning("Audio track exception: " + exception.getMessage());
        main.logger.warning(exception.getCause().getMessage());
        exception.printStackTrace();
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        main.logger.warning("Audio track is stuck.");
    }

    public void reset(){
        armorObtainedByHunters = false;
        piratesLastPlaytime = 0;
        runnersHaveTradedWithPiglins = false;
        ironObtainedByRunners = false;
        dangerLevel = DangerLevel.FarAway;
    }

    public void playDangerLevelTrack(){
        if(!autoEnabled) return;
        if(!main.commands.gameIsRunning) return;
        ArrayList<String> candidates = new ArrayList<String>();
        if(dangerLevel == null) return;
        switch(dangerLevel){
            case Chasing:
                candidates.add("danger2");
                candidates.add("risingaction");
                candidates.add("endwar");
                break;
            case InSight:
                candidates.add("plotting");
                candidates.add("danger");
                candidates.add("preparing-danger");
//                candidates.add("timesup");
                break;
            case Stealth:
                candidates.add("approaching");
                candidates.add("found");
                candidates.add("discovered");
                break;
            case Approaching:
                candidates.add("relaxing");
//                candidates.add("tense");
                candidates.add("resolution");
                candidates.add("resolution2");
                candidates.add("rhythmic");
                candidates.add("lowdanger");
                candidates.add("premonition");
                break;
            case FarAway:
                candidates.add("fun");
                candidates.add("preparing-safe");
                candidates.add("preparing-safe2");
//                candidates.add("chill");
                candidates.add("gatheringresources");
                candidates.add("journey");
                candidates.add("swing");
                break;
            case RunnerInNether:
                candidates.add("nether");
                candidates.add("spooked");
                candidates.add("cinematic");
                candidates.add("jazz");
                break;
        }
        int random = (int)(Math.random() * candidates.size());
        String chosen = candidates.get(random);
        playTrack(chosen);

        JsonObjectBuilder eventParams = Json.createObjectBuilder()
                .add("danger_level", dangerLevel.toString())
                .add("track", chosen);
        main.analytics.sendEvent("danger_level_track_played", eventParams);
    }

    public void updateDangerLevel(){
        if(!autoEnabled) return;
        double distance = 9999999;
        boolean hunterInNetherDimension = false;
        boolean runnerInNetherDimension = false;
        for(String huntername : main.hunters){
            Player hunter = Bukkit.getPlayer(huntername);
            if(hunter == null) continue;
            if(hunter.getGameMode() != GameMode.SURVIVAL) continue;
            if(hunter.getWorld().getEnvironment() == World.Environment.NETHER) hunterInNetherDimension = true;
            for(String runnername : main.runners){
                Player runner = Bukkit.getPlayer(runnername);
                if(runner == null) continue;
                if(runner.getGameMode() != GameMode.SURVIVAL) continue;
                if(runner.getWorld().getEnvironment() == World.Environment.NETHER) runnerInNetherDimension = true;
                if(hunter.getWorld().getEnvironment() != runner.getWorld().getEnvironment()) continue;
                double newDistance = hunter.getLocation().distance(runner.getLocation());
                if(newDistance < distance) distance = newDistance;
            }
        }
        DangerLevel oldLevel = dangerLevel;
        if(distance < 50){
            dangerLevel = DangerLevel.Chasing;
        }else if(distance < 150){
            dangerLevel = DangerLevel.InSight;
        }else if(distance < 300){
            dangerLevel = DangerLevel.Stealth;
        }else if(distance < 450){
            dangerLevel = DangerLevel.Approaching;
        }else{
            dangerLevel = DangerLevel.FarAway;
        }
        if(runnerInNetherDimension && !hunterInNetherDimension){
            dangerLevel = DangerLevel.RunnerInNether;
        }
        if(oldLevel.compareTo(dangerLevel) > 0 || musicManager.player.getPlayingTrack() == null){
            if(!specialPlaying) playDangerLevelTrack();
        }
    }

    public void playTrack(String trackName){
        if(tracks.containsKey(trackName)){
            musicManager.playTrack(tracks.get(trackName));
            return;
        }
        loadTrack(trackName, new TrackLoadHandler() {
            @Override
            public void onTrackLoaded() {
                musicManager.playTrack(tracks.get(trackName));
            }
        });
    }

    public void playSpecialTrack(String trackName, boolean override){
        if(!autoEnabled) return;
        if(specialPlaying && !override) return;
        if(!main.commands.gameIsRunning) return;

        specialPlaying = true;
        playTrack(trackName);

        JsonObjectBuilder eventParams = Json.createObjectBuilder()
                .add("track", trackName);
        main.analytics.sendEvent("special_track_played", eventParams);
    }

    public void playSpecialTrack(String trackName){
        playSpecialTrack(trackName, false);
    }

    @EventHandler
    public void onPlayerVehicleEnter(VehicleEnterEvent event){
        if(event.getVehicle().getType() != EntityType.BOAT) return;
        if(event.getEntered().getType() != EntityType.PLAYER) return;
        long serverTimestamp = System.currentTimeMillis();
        if(serverTimestamp - piratesLastPlaytime < piratesCooldownMillis) return;

        boolean found = main.hunters.size() == 0; // For debugging: if there are no hunters play anyways
        for(String i : main.hunters){
            Player player = Bukkit.getPlayer(i);
            if(player == null) continue;
            if(player.isInsideVehicle() && player.getVehicle().getType() == EntityType.BOAT){
                found = true;
                break;
            }
        }
        if(!found) return;

        found = false;
        for(String i : main.runners) {
            Player player = Bukkit.getPlayer(i);
            if (player == null) continue;
            if (player.isInsideVehicle() && player.getVehicle().getType() == EntityType.BOAT) {
                found = true;
                break;
            }
        }
        if(!found) return;
        piratesLastPlaytime = serverTimestamp;
        playSpecialTrack("pirates");
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if(main.runners.contains(event.getPlayer().getName())) {
            World.Environment startingWorld = event.getFrom().getWorld().getEnvironment();
            World.Environment endingWorld = event.getTo().getWorld().getEnvironment();
            if(startingWorld == World.Environment.NORMAL && endingWorld == World.Environment.NETHER) {
                playDangerLevelTrack();
            } else if(startingWorld == World.Environment.NORMAL && endingWorld == World.Environment.THE_END) {
                playSpecialTrack("finale");
            }
        }
    }

    @EventHandler
    public void onBlockMine(BlockBreakEvent event){
        Material type = event.getBlock().getType();
        if(type == Material.IRON_ORE && !ironObtainedByRunners){
            if(!main.runners.contains(event.getPlayer().getName())) return;
            ironObtainedByRunners = true;
            playSpecialTrack("gatheringresources", true);
        }else if(type == Material.DIAMOND_ORE){
            if(event.getPlayer().getInventory().contains(Material.DIAMOND)) return;
            playSpecialTrack("xmas", true);
        }
    }

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event){
        if(event.getDamager().getType() != EntityType.PLAYER || event.getEntity().getType() != EntityType.PLAYER) return;
        Player damager = (Player)event.getDamager();
        Player damagee = (Player)event.getEntity();
        if(main.hunters.contains(damager.getName())){
            if(!main.runners.contains(damagee.getName())){
                return;
            }
        }else if(main.runners.contains(damager.getName())){
            if(!main.hunters.contains(damagee.getName())){
                return;
            }
        }else{
            return;
        }

        long armorCount = Arrays.stream(damager.getInventory().getArmorContents()).filter(stack -> stack != null).count() +
                Arrays.stream(damagee.getInventory().getArmorContents()).filter(stack -> stack != null).count();
        if(armorCount > 2){
            int random = (int)(Math.random() * 5);
            // intense, intense2,
            if(random == 0) playSpecialTrack("intense");
            else if(random == 1) playSpecialTrack("dramatic");
            else if(random == 2) playSpecialTrack("heroic");
            else if(random == 3) playSpecialTrack("intense2");
            else if(random == 4) playSpecialTrack("fighting-upbeat");
        }else{
            int random = (int)(Math.random() * 2);
            if(random == 0) playSpecialTrack("chase");
            else playSpecialTrack("chase2");
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        if(main.hunters.contains(event.getEntity().getName())){
            int random = (int)(Math.random() * 2);
            if(random == 0) playSpecialTrack("beastmode", true);
            else playSpecialTrack("upbeat", true);
        }else if(main.runners.contains(event.getEntity().getName())){
            for(String i : main.runners){
                Player p = Bukkit.getPlayer(i);
                if(p == null) return;
                if(p.getGameMode() == GameMode.SURVIVAL && !p.getName().equalsIgnoreCase(event.getEntity().getName())){
                    playSpecialTrack("loss", true);
                    return;
                }
            }
            playSpecialTrack("sad", true);
        }
    }

    @EventHandler
    public void onCraftCompleted(CraftItemEvent event) {
        if(!(event.getWhoClicked() instanceof Player)){
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if(main.hunters.contains(player.getName())){
            Material type = event.getRecipe().getResult().getType();
            boolean isIronArmor = type == Material.IRON_BOOTS || type == Material.IRON_LEGGINGS || type == Material.IRON_CHESTPLATE || type == Material.IRON_HELMET;
            if(!armorObtainedByHunters && isIronArmor){
                armorObtainedByHunters = true;
                playSpecialTrack("upbeat-bite", true);
            }
        }
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event){
        Player player = event.getPlayer();
        if(main.runners.contains(player.getName())){
            if(event.getRightClicked().getType() == EntityType.PIGLIN && !runnersHaveTradedWithPiglins){
                ItemStack mainHand = player.getInventory().getItemInMainHand();
                if(mainHand != null && mainHand.getType() == Material.GOLD_INGOT){
                    runnersHaveTradedWithPiglins = true;
                    playSpecialTrack("montage", true);
                }
            }
        }
    }
}
