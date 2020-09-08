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
        put("intro", "https://www.youtube.com/watch?v=beCC9xJjLZQ");
        put("headstart", "https://www.youtube.com/watch?v=tJFqdLg58i4");
        put("pirates", "https://www.youtube.com/watch?v=27mB8verLK8");
        put("gatheringresources", "https://www.youtube.com/watch?v=tKfpwSVpXxU");
        put("montage", "https://www.youtube.com/watch?v=BezpUnoZObw");
        put("xmas", "https://www.youtube.com/watch?v=R5swgaETU1g");

        put("relaxing", "https://www.youtube.com/watch?v=ipCeVg-SA-Y");
        put("fun", "https://www.youtube.com/watch?v=O9cWfV_3J7o");

        put("preparing-safe", "https://www.youtube.com/watch?v=gehk17hjEdU");
        put("preparing-danger", "https://www.youtube.com/watch?v=CYcTNDXUOoY");
        put("lowdanger", "https://www.youtube.com/watch?v=3OSs540jvF4");

        put("risingaction", "https://www.youtube.com/watch?v=ghAeHHTID2k");

        put("nether", "https://www.youtube.com/watch?v=tQdAiG29HiM");

        put("spooked", "https://www.youtube.com/watch?v=3kclVzQ3S4M");
        put("tense", "https://www.youtube.com/watch?v=Vv9-cxbacOg");
        put("plotting", "https://www.youtube.com/watch?v=RXyYt8kx740");
        put("approaching", "https://www.youtube.com/watch?v=wD3Mf4asnFk");

        put("danger", "https://www.youtube.com/watch?v=kVJQ74RoHCM");
        put("danger2", "https://www.youtube.com/watch?v=5y4b7jDXf_E");

        put("discovered", "https://www.youtube.com/watch?v=BMUPNgU3lII");
        put("found", "https://www.youtube.com/watch?v=YMwQQJ0ChCU");

        // on runner hit, less than two pieces of armor
        put("chase", "https://www.youtube.com/watch?v=CX9wFdExF_k");
        put("chase2", "https://www.youtube.com/watch?v=6S3f_AVEnSM");

        // on runner hit, more than two pieces of armor
        put("epicwar", "https://www.youtube.com/watch?v=_UBZmrQwD9o");
        put("endwar", "https://www.youtube.com/watch?v=0EsBItv1Pns");

        // On hunter kill by runner
        put("beastmode", "https://www.youtube.com/watch?v=4rGl1KXV4eA");

        put("resolution", "https://www.youtube.com/watch?v=B4K7Hqv4vts");
        put("resolution2", "https://www.youtube.com/watch?v=eM6WxujEGy8");

        // On runner death
        put("loss", "https://www.youtube.com/watch?v=9Fx314TyuWI");
        put("sad", "https://www.youtube.com/watch?v=OK7a4EQVRc0");

        put("timesup", "https://www.youtube.com/watch?v=_KaspaKTBw8");
    }};
    public HashMap<String, AudioTrack> tracks = new HashMap<String, AudioTrack>();

    public TrackManager(MusicManager musicManager, PluginMain main){
        this.musicManager = musicManager;
        this.main = main;
        for(Map.Entry<String, String> i : trackURLs.entrySet()){
            musicManager.playerManager.loadItemOrdered(musicManager, i.getValue(), new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack track) {
                    tracks.put(i.getKey(), track);
                    main.logger.info("Loaded track " + i.getKey());
                }

                @Override
                public void playlistLoaded(AudioPlaylist playlist) {
                    main.logger.warning("Playlists not supported");
                }

                @Override
                public void noMatches() {
                    main.logger.warning("Track not found: " + i.getValue());
                }

                @Override
                public void loadFailed(FriendlyException exception) {
                    main.logger.warning("Load track failed: " + exception.getMessage());
                }
            });
        }
    }

    public String parseCommand(String argument){
        if(argument.length() == 0){
            return "Specify a track to play!";
        }
        if(argument.equalsIgnoreCase("forceupdate")){
            specialPlaying = false;
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
        autoEnabled = false;
        if(argument.equalsIgnoreCase("stop")){
            musicManager.stopTrack();
            return "Stopped music";
        }
        AudioTrack found = tracks.get(argument.toLowerCase());
        if(found == null){
            return "Track " + argument + " was not found. Use /music list to see a list of valid tracks.";
        }
        musicManager.playTrack(found);
        return "Playing track " + argument;
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        super.onTrackEnd(player, track, endReason);

        if(!autoEnabled) return;
        specialPlaying = false;
        playDangerLevelTrack();
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
            case FarAway:
                candidates.add("fun");
                candidates.add("preparing-safe");
                candidates.add("lowdanger");
                candidates.add("resolution");
                candidates.add("resolution2");
            case RunnerInNether:
                candidates.add("nether");
                candidates.add("spooked");
        }
        int random = (int)(Math.random() * candidates.size());
        musicManager.playTrack(tracks.get(candidates.get(random)));
    }

    public void updateDangerLevel(){
        if(!autoEnabled) return;
        double distance = 9999999;
        boolean hunterInNetherDimension = false;
        boolean runnerInNetherDimension = false;
        for(String huntername : main.hunters){
            Player hunter = Bukkit.getPlayer(huntername);
            if(hunter.getWorld().getEnvironment() == World.Environment.NETHER) hunterInNetherDimension = true;
            if(hunter == null) continue;
            for(String runnername : main.runners){
                Player runner = Bukkit.getPlayer(runnername);
                if(runner.getWorld().getEnvironment() == World.Environment.NETHER) runnerInNetherDimension = true;
                if(runner == null) continue;
                double newDistance = hunter.getLocation().distance(runner.getLocation());
                if(newDistance < distance) distance = newDistance;
            }
        }
        DangerLevel oldLevel = dangerLevel;
        if(distance < 15){
            dangerLevel = DangerLevel.Chasing;
        }else if(distance < 40){
            dangerLevel = DangerLevel.InSight;
        }else if(distance < 100){
            dangerLevel = DangerLevel.Stealth;
        }else if(distance < 200){
            dangerLevel = DangerLevel.Approaching;
        }else{
            dangerLevel = DangerLevel.FarAway;
        }
        if(runnerInNetherDimension && !hunterInNetherDimension){
            dangerLevel = DangerLevel.RunnerInNether;
        }
        if(oldLevel.compareTo(dangerLevel) > 0 || musicManager.player.getPlayingTrack() == null){
            if(!specialPlaying) playDangerLevelTrack();;
        }
    }

    public void playSpecialTrack(String trackName, boolean override){
        if(!autoEnabled) return;
        if(specialPlaying && !override) return;
        specialPlaying = true;
        musicManager.playTrack(tracks.get(trackName));
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
        int random = (int)(Math.random() * 2);
        long armorCount = Arrays.stream(damager.getInventory().getArmorContents()).filter(stack -> stack != null).count() +
                Arrays.stream(damagee.getInventory().getArmorContents()).filter(stack -> stack != null).count();
        if(armorCount > 2){
            if(random == 0) playSpecialTrack("epicwar");
            else playSpecialTrack("endwar");
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
