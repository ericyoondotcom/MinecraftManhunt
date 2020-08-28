package com.yoonicode.minecraftmanhunt;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

public class TrackManager extends AudioEventAdapter implements Listener {
    MusicManager musicManager;
    PluginMain main;
    boolean autoEnabled = false;

    public enum DangerLevel {
        Fighting,
        Chasing,
        InSight,
        Stealth,
        Approaching,
        FarAway
    }

    public HashMap<String, String> trackURLs = new HashMap<String, String>() {{
        put("intro", "https://www.youtube.com/watch?v=beCC9xJjLZQ");
        put("headstart", "https://www.youtube.com/watch?v=tJFqdLg58i4");

        put("pirates", "https://www.youtube.com/watch?v=27mB8verLK8");

        put("gatheringresources", "https://www.youtube.com/watch?v=tKfpwSVpXxU");
        put("fun", "https://www.youtube.com/watch?v=O9cWfV_3J7o");
        put("montage", "https://www.youtube.com/watch?v=BezpUnoZObw");
        put("relaxing", "https://www.youtube.com/watch?v=ipCeVg-SA-Y");
        put("xmas", "https://www.youtube.com/watch?v=R5swgaETU1g");

        put("preparing-safe", "https://www.youtube.com/watch?v=gehk17hjEdU");
        put("preparing-danger", "https://www.youtube.com/watch?v=CYcTNDXUOoY");
        put("lowdanger", "https://www.youtube.com/watch?v=3OSs540jvF4");

        put("risingaction", "https://www.youtube.com/watch?v=ghAeHHTID2k");

        put("nether", "https://www.youtube.com/watch?v=tQdAiG29HiM"); // When both teams are in the nether

        put("spooked", "https://www.youtube.com/watch?v=3kclVzQ3S4M");
        put("tense", "https://www.youtube.com/watch?v=Vv9-cxbacOg");
        put("plotting", "https://www.youtube.com/watch?v=RXyYt8kx740");
        put("approaching", "https://www.youtube.com/watch?v=wD3Mf4asnFk");

        put("danger", "https://www.youtube.com/watch?v=kVJQ74RoHCM");
        put("danger2", "https://www.youtube.com/watch?v=5y4b7jDXf_E");

        put("discovered", "https://www.youtube.com/watch?v=BMUPNgU3lII");
        put("found", "https://www.youtube.com/watch?v=YMwQQJ0ChCU");

        put("chase", "https://www.youtube.com/watch?v=CX9wFdExF_k");
        put("chase2", "https://www.youtube.com/watch?v=6S3f_AVEnSM");
        put("epicwar", "https://www.youtube.com/watch?v=_UBZmrQwD9o");
        put("endwar", "https://www.youtube.com/watch?v=0EsBItv1Pns");

        put("winning", "https://open.spotify.com/track/7tT4uf3CpCVaXbvH2w450d?si=ATLTa-eET2qRodPjsUYIMg");
        put("beastmode", "https://www.youtube.com/watch?v=4rGl1KXV4eA");

        put("resolution", "https://www.youtube.com/watch?v=B4K7Hqv4vts");
        put("resolution2", "https://www.youtube.com/watch?v=eM6WxujEGy8");

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
    }
}
