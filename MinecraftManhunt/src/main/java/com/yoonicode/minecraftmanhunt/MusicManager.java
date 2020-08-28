package com.yoonicode.minecraftmanhunt;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.VoiceChannel;
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
        // player.addListener blah blah
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
            VoiceChannel vc = audioManager.getGuild().getVoiceChannelById(voiceId);
            if(vc == null){
                main.logger.warning("Voice channel with id " + voiceId + " does not exist.");
                return;
            }
            audioManager.openAudioConnection(vc);
        }
    }

    public void playTrack(AudioTrack track){
        Connect();
        player.startTrack(track.makeClone(), false);
    }

    public void stopTrack(){
        player.stopTrack();
    }

}
