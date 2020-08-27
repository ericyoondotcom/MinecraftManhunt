package com.yoonicode.minecraftmanhunt;

import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

public class TrackManager extends AudioEventAdapter implements Listener {
    MusicManager musicManager;
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
        put("epicwar", "https://www.youtube.com/watch?v=xd0uMCUMMwo");
        put("endwar", "https://www.youtube.com/watch?v=xd0uMCUMMwo");

        put("winning", "https://open.spotify.com/track/7tT4uf3CpCVaXbvH2w450d?si=ATLTa-eET2qRodPjsUYIMg");
        put("beastmode", "https://www.youtube.com/watch?v=4rGl1KXV4eA");

        put("resolution", "https://www.youtube.com/watch?v=B4K7Hqv4vts");
        put("resolution2", "https://www.youtube.com/watch?v=xd0uMCUMMwo");

        put("loss", "https://www.youtube.com/watch?v=9Fx314TyuWI");
        put("sad", "https://www.youtube.com/watch?v=OK7a4EQVRc0");

        put("timesup", "https://www.youtube.com/watch?v=_KaspaKTBw8");
    }};
    public Hashmap<String, AudioTrack> tracks = new Hashmap<String, AudioTrack>();

    public TrackManager(MusicManager musicManager){
        this.musicManager = musicManager;
        for(Map.Entry<String, String> i : trackURLs.entrySet()){
            
        }
    }


}
