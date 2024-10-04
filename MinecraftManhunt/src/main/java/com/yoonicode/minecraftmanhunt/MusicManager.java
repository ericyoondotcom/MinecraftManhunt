package com.yoonicode.minecraftmanhunt;

import com.sedmelluq.discord.lavaplayer.player.*;
import com.sedmelluq.discord.lavaplayer.source.*;
import com.sedmelluq.discord.lavaplayer.track.*;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import org.bukkit.configuration.file.FileConfiguration;

public class MusicManager {
    public final AudioPlayer player;
    public final AudioPlayerManager playerManager;
    public final AudioManager audioManager;
    public PluginMain main;

    public MusicManager(AudioPlayerManager playerManager, AudioManager audioManager, PluginMain main){
        this.playerManager = playerManager;
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
        this.audioManager = audioManager;
        player = this.playerManager.createPlayer();
        this.main = main;
    }

    public AudioPlayerSendHandler getSendHandler() {
        return new AudioPlayerSendHandler(player);
    }

    public void Connect(){
        if (!audioManager.isConnected()) {
            FileConfiguration config = main.getConfig();
            String voiceId = config.getString("musicChannelId");
            if(voiceId == null){
                main.logger.warning("Music channel ID not specified in config.");
                return;
            }
            AudioChannel vc = audioManager.getGuild().getVoiceChannelById(voiceId);
            if(vc == null){
                main.logger.warning("Voice channel with id " + voiceId + " does not exist.");
                return;
            }
            audioManager.openAudioConnection(vc);
        }
    }

    public void playTrack(AudioTrack track){
        Connect();
        boolean result = player.startTrack(track.makeClone(), false);
        main.logger.info("Started track with a result of " + result);
    }

    public void stopTrack(){
        player.stopTrack();
        main.discord.trackManager.specialPlaying = false;
    }

}
