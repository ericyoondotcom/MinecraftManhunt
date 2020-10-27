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
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TrackManager extends AudioEventAdapter implements Listener {
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

    public HashMap<String, String> trackURLs = new HashMap<String, String>() {{
        put("intro", "https://www.youtube.com/watch?v=beCC9xJjLZQ"); // Epic Dawn - Bobby Cole
        put("headstart", "https://www.youtube.com/watch?v=tJFqdLg58i4"); // Trance Music for Racing Game - Bobby Cole
        put("pirates", "https://www.youtube.com/watch?v=27mB8verLK8"); // Pirates of the Caribbean theme song
        put("gatheringresources", "https://www.youtube.com/watch?v=tKfpwSVpXxU"); // Do the Funky Strut - Bobby Cole
        put("montage", "https://www.youtube.com/watch?v=BezpUnoZObw"); // The Elevator Bossa Nova - Bensound
        put("xmas", "https://www.youtube.com/watch?v=R5swgaETU1g"); // Jingle Bells - Bill Robuck & Jerrold W Lambert

        put("relaxing", "https://www.youtube.com/watch?v=ipCeVg-SA-Y"); // Inhale and Exhale - For Peace of Mind and Serenity
        put("fun", "https://www.youtube.com/watch?v=O9cWfV_3J7o"); // Playtime - Peter Godfrey
        put("chill", "https://www.youtube.com/watch?v=eSNXvU-kowc"); // Crime and Robbery Music

        put("preparing-safe", "https://www.youtube.com/watch?v=gehk17hjEdU"); // Race Against Time - Ceiri Torjussen
        put("preparing-danger", "https://www.youtube.com/watch?v=CYcTNDXUOoY"); // Action Preparation - Jason Donnelly
        put("lowdanger", "https://www.youtube.com/watch?v=3OSs540jvF4"); // White Lines - Zac Nelson

        put("risingaction", "https://www.youtube.com/watch?v=ghAeHHTID2k"); // Strange Things - Zac Nelson

        put("nether", "https://www.youtube.com/watch?v=tQdAiG29HiM"); // Music For a Killer - Bobby Cole

        put("spooked", "https://www.youtube.com/watch?v=3kclVzQ3S4M"); // Re-Animation - Peter Godfrey
        put("tense", "https://www.youtube.com/watch?v=Vv9-cxbacOg"); // Tension in the Air - Bobby Cole
        put("plotting", "https://www.youtube.com/watch?v=RXyYt8kx740"); // Thinking and Tension - Bobby Cole
        put("approaching", "https://www.youtube.com/watch?v=wD3Mf4asnFk"); // Thinking About Murder - Boby Cole

        put("danger", "https://www.youtube.com/watch?v=kVJQ74RoHCM"); // Dramatic Anticipation (Alternative version) - PremiumTrax
        put("danger2", "https://www.youtube.com/watch?v=5y4b7jDXf_E"); // Dark Time Ticking - Robert Valenti

        put("discovered", "https://www.youtube.com/watch?v=BMUPNgU3lII"); // Evidence - RTaphael Costa
        put("found", "https://www.youtube.com/watch?v=YMwQQJ0ChCU"); // On The Killer's Trail - William Pearson & Robert Watson

        // on runner hit, less than two pieces of armor
        put("chase", "https://www.youtube.com/watch?v=CX9wFdExF_k"); // Navajo Race - Bryan Steele
        put("chase2", "https://www.youtube.com/watch?v=0TAFhSZXOjI"); // The Tribal Chase Cue - Bobby Cole

        // on runner hit, more than two pieces of armor
        put("epicwar", "https://www.youtube.com/watch?v=_UBZmrQwD9o"); // There Is No Escape - Hollywood Film Music
        put("endwar", "https://www.youtube.com/watch?v=0EsBItv1Pns"); // Heist Gone Wrong - Clark Aboud
        put("fighting", "https://www.youtube.com/watch?v=S4MC7QdayXc"); // Halo - Michael Vignola

        // On hunter kill by runner
        put("beastmode", "https://www.youtube.com/watch?v=4rGl1KXV4eA"); // Dominating - Ray Aley

        put("resolution", "https://www.youtube.com/watch?v=B4K7Hqv4vts"); // Out of the Skies, Under the Earth - Chris Zabriskie
        put("resolution2", "https://www.youtube.com/watch?v=eM6WxujEGy8"); // Divider - Chris Zabriskie

        // On runner death
        put("loss", "https://www.youtube.com/watch?v=9Fx314TyuWI"); // Mystery Collection - Lonesome Piano - D. Silverstone
        put("sad", "https://www.youtube.com/watch?v=OK7a4EQVRc0"); // Sad Goodbyes - Mark Kueffner

        put("timesup", "https://soundcloud.com/user-547037461/clock-is-ticking-benny-hawes"); // Clock Is Ticking - Benny Hawes
    }};
    public HashMap<String, AudioTrack> tracks = new HashMap<String, AudioTrack>();

    public TrackManager(MusicManager musicManager, PluginMain main){
        this.musicManager = musicManager;
        this.main = main;
        musicManager.player.addListener(this);
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
            ret += "playing special: " + specialPlaying + ", ";
            ret += ", auto enabled: " + autoEnabled;
            return ret;
        }
        if(!trackURLs.containsKey(argument.toLowerCase())){
            return "Track " + argument + " was not found. Use /music list to see a list of valid tracks.";
        }

        autoEnabled = false;
        playTrack(argument.toLowerCase());
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
        dangerLevel = DangerLevel.FarAway;
    }

    public void playDangerLevelTrack(){
        if(!autoEnabled) return;

        ArrayList<String> candidates = new ArrayList<String>();
        if(dangerLevel == null) return;
        switch(dangerLevel){
            case Chasing:
                candidates.add("danger");
                candidates.add("risingaction");
                break;
            case InSight:
                candidates.add("preparing-danger");
                candidates.add("danger2");
                candidates.add("timesup");
            case Stealth:
                candidates.add("approaching");
                candidates.add("found");
                candidates.add("plotting");
            case Approaching:
                candidates.add("relaxing");
                candidates.add("discovered");
                candidates.add("tense");
                candidates.add("resolution");
                candidates.add("resolution2");
            case FarAway:
                candidates.add("fun");
                candidates.add("preparing-safe");
                candidates.add("chill");
                candidates.add("gatheringresources");
                candidates.add("lowdanger");
            case RunnerInNether:
                candidates.add("nether");
                candidates.add("spooked");
        }
        int random = (int)(Math.random() * candidates.size());
        playTrack(candidates.get(random));
    }

    public void updateDangerLevel(){
        if(!autoEnabled) return;
        double distance = 9999999;
        boolean hunterInNetherDimension = false;
        boolean runnerInNetherDimension = false;
        for(String huntername : main.hunters){
            Player hunter = Bukkit.getPlayer(huntername);
            if(hunter == null) continue;
            if(hunter.getWorld().getEnvironment() == World.Environment.NETHER) hunterInNetherDimension = true;
            for(String runnername : main.runners){
                Player runner = Bukkit.getPlayer(runnername);
                if(runner == null) continue;
                if(runner.getWorld().getEnvironment() == World.Environment.NETHER) runnerInNetherDimension = true;
                if(hunter.getWorld().getEnvironment() != runner.getWorld().getEnvironment()) continue;
                double newDistance = hunter.getLocation().distance(runner.getLocation());
                if(newDistance < distance) distance = newDistance;
            }
        }
        DangerLevel oldLevel = dangerLevel;
        if(distance < 25){
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

        specialPlaying = true;
        playTrack(trackName);
    }

    public void playSpecialTrack(String trackName){
        playSpecialTrack(trackName, false);
    }

    @EventHandler
    public void playerEnterPortalEvent(PlayerPortalEvent event){
        playDangerLevelTrack();
    }

    @EventHandler
    public void onPlayerVehicleEnter(VehicleEnterEvent event){
        if(event.getVehicle().getType() != EntityType.BOAT) return;
        if(event.getEntered().getType() != EntityType.PLAYER) return;

        boolean found = false;
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

        playSpecialTrack("pirates");
    }

    @EventHandler
    public void onBlockMine(BlockBreakEvent event){
        Material type = event.getBlock().getType();
        if(type == Material.IRON_ORE){
            if(!main.runners.contains(event.getPlayer().getName())) return;

            for(String i : main.runners){
                Player p = Bukkit.getPlayer(i);
                if(p == null) return;
                if(p.getInventory().contains(Material.IRON_ORE) || p.getInventory().contains(Material.IRON_INGOT)) return;
            }

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
        int random = (int)(Math.random() * 3);
        long armorCount = Arrays.stream(damager.getInventory().getArmorContents()).filter(stack -> stack != null).count() +
                Arrays.stream(damagee.getInventory().getArmorContents()).filter(stack -> stack != null).count();
        if(armorCount > 2){
            if(random == 0) playSpecialTrack("epicwar");
            else if(random == 1) playSpecialTrack("endwar");
            else playSpecialTrack("fighting");
        }else{
            if(random == 0) playSpecialTrack("chase");
            else playSpecialTrack("chase2");
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        if(main.hunters.contains(event.getEntity().getName())){
            playSpecialTrack("beastmode", true);
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
}
