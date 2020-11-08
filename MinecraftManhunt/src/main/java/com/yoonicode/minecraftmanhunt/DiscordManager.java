package com.yoonicode.minecraftmanhunt;


import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.util.List;

public class DiscordManager extends ListenerAdapter {
    public boolean enabled;
    boolean processDiscordCommands;
    String discordToken;
    PluginMain main;
    JDA client;
    Guild guild;
    Role hunterRole;
    Role runnerRole;
    Role spectatorRole;
    MusicManager music;
    AudioPlayerManager playerManager;
    TrackManager trackManager;

    public DiscordManager(PluginMain main) {
        this.main = main;
        FileConfiguration config = main.getConfig();
        enabled = config.getBoolean("enableDiscord");
        processDiscordCommands = config.getBoolean("processDiscordCommands", false);
        discordToken = config.getString("discordToken");
        String presenceMessage = config.getString("ip", "");

        if(enabled){
            JDABuilder builder = JDABuilder.createDefault(discordToken);
            if(presenceMessage.length() > 0) {
                builder.setActivity(Activity.playing(presenceMessage));
            }
            try {
                client = builder.build();
            } catch (LoginException e) {
                main.logger.warning("LoginException: Discord token is invalid. " + e.getMessage());
            }
            client.addEventListener(this);
        }
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        FileConfiguration config = main.getConfig();
        String guildId = config.getString("discordServerId");
        String hunterRoleId = config.getString("hunterRoleId");
        String runnerRoleId = config.getString("runnerRoleId");
        String spectatorRoleId = config.getString("spectatorRoleId");
        if(guildId == null || hunterRoleId == null || runnerRoleId == null || spectatorRoleId == null){
            main.logger.warning("Discord guild, hunter role, runner role, or spectator role ID is null. Make sure it's specified in the YAML file.");
        }
        guild = client.getGuildById(guildId);
        main.logger.info("Found guild: " + guildId);
        hunterRole = guild.getRoleById(hunterRoleId);
        runnerRole = guild.getRoleById(runnerRoleId);
        spectatorRole = guild.getRoleById(spectatorRoleId);
        if(guild == null || hunterRole == null || runnerRole == null || spectatorRole == null){
            main.logger.warning("The guild or one of the roles was not found");
            Bukkit.broadcastMessage("Manhunt plugin has most likely been configured improperly. Make sure your Discord guild ID and role IDs are correct and reload the server.");
        }
        playerManager = new DefaultAudioPlayerManager();
        AudioManager audioManager = guild.getAudioManager();
        music = new MusicManager(playerManager, audioManager, main);
        audioManager.setSendingHandler(music.getSendHandler());
        trackManager = new TrackManager(music, main);
        Bukkit.getServer().getPluginManager().registerEvents(trackManager, main);
        main.logger.info("Discord up and running");
    }

    /**
     * Assigns a team role to a Discord user.
     * @param team Which team role to assign.
     * @param username The Discord/Minecraft username of the target.
     * @return true if a user was found and a role was assigned, false otherwise.
     */
    public boolean assignRole(ManhuntTeam team, String username){
        if(!enabled) return true;
        main.logger.info("Assigning role " + team.toString() + " to " + username);
        if(guild == null){
            main.logger.warning("Guild is null. Make sure the Discord Server ID is set correctly in the config file.");
            return false;
        }
        List<Member> found = guild.retrieveMembersByPrefix(username, 1).get();
        if(found.size() == 0){
            main.logger.warning("No username " + username + " found in Discord server");
            return false;
        }

        Member target = found.get(0);
        if(team == ManhuntTeam.HUNTER) {
            guild.addRoleToMember(target, hunterRole).queue();
            guild.removeRoleFromMember(target, spectatorRole).queue();
            guild.removeRoleFromMember(target, runnerRole).queue();
        } else if(team == ManhuntTeam.RUNNER) {
            guild.addRoleToMember(target, runnerRole).queue();
            guild.removeRoleFromMember(target, spectatorRole).queue();
            guild.removeRoleFromMember(target, hunterRole).queue();
        } else if(team == ManhuntTeam.SPECTATOR) {
            guild.addRoleToMember(target, spectatorRole).queue();
            guild.removeRoleFromMember(target, hunterRole).queue();
            guild.removeRoleFromMember(target, runnerRole).queue();
        } else {
            main.logger.warning("Team was not one of the valid options.");
            return false;
        }
        return true;
    }

    public boolean removeRoles(String username){
        if(!enabled) return true;
        if(guild == null){
            main.logger.warning("Guild is null. Make sure the Discord Server ID is set correctly in the config file.");
            return false;
        }
        List<Member> found = guild.retrieveMembersByPrefix(username, 1).get();
        if(found.size() == 0){
            main.logger.warning("No username " + username + " found");
            return false;
        }
        Member target = found.get(0);
        guild.removeRoleFromMember(target, runnerRole).queue();
        guild.removeRoleFromMember(target, spectatorRole).queue();
        guild.removeRoleFromMember(target, hunterRole).queue();
        return true;
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if(event.getAuthor().isBot()) return;
        if(!processDiscordCommands) return;
        if(!event.getGuild().getId().equalsIgnoreCase(guild.getId())) return;
        String message = event.getMessage().getContentDisplay().trim().toLowerCase();
        if(message.startsWith("music")){
            String arg1 = message.replaceAll("music", "").trim();
            event.getChannel().sendMessage(trackManager.parseCommand(arg1)).queue();
        }
    }
}
