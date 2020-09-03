package com.yoonicode.minecraftmanhunt;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Map;

import static org.bukkit.Bukkit.*;

public class PluginCommands implements CommandExecutor {

    public static final String[] registeredCommands = {
            "hunter",
            "speedrunner",
            "spectator",
            "start",
            "end",
            "compass",
            "music"
    };

    int compassTask = -1;
    int dangerLevelTask = -1;

    private final PluginMain main;

    public PluginCommands(PluginMain main) {
        this.main = main;
    }

    public void UpdateCompass(){
        for(Map.Entry<String, String> i : main.targets.entrySet()){
            Player hunter = getPlayer(i.getKey());
            Player target = getPlayer(i.getValue());
            if(hunter == null || target == null){
                continue;
            }
            if(hunter.getWorld().getEnvironment() != target.getWorld().getEnvironment()){
                Location loc = main.portals.get(target.getName());
                if(loc != null){
                    hunter.setCompassTarget(loc);
                }
            }else{
                hunter.setCompassTarget(target.getLocation());
            }
        }
    }

    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if ("hunter".equals(label)) {
            if (args.length != 1) return false;
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                commandSender.sendMessage("Target is not online");
                return false;
            }
            for (String i : main.hunters) {
                if (target.getName().equalsIgnoreCase(i)) {
                    commandSender.sendMessage("Target is already a hunter");
                    return true;
                }
            }
            for (String i : main.runners) {
                if (target.getName().equalsIgnoreCase(i)) {
                    main.runners.remove(i);
                    break;
                }
            }
            for (String i : main.spectators) {
                if (target.getName().equalsIgnoreCase(i)) {
                    main.spectators.remove(i);
                    break;
                }
            }
            main.hunters.add(target.getName());
            main.huntersTeam.addEntry(target.getName());
            if(!main.discord.AssignRole(ManhuntTeam.HUNTER, target.getName())){
                commandSender.sendMessage("Could not assign Discord role. Make sure the target's username is also their Discord nickname.");
            }
            target.sendMessage("You have been marked as a hunter.");
            commandSender.sendMessage("Marked player as hunter");
            return true;
        } else if ("speedrunner".equals(label)) {
            if (args.length != 1) return false;
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                commandSender.sendMessage("Target is not online");
                return false;
            }
            for (String i : main.runners) {
                if (target.getName().equalsIgnoreCase(i)) {
                    commandSender.sendMessage("Target is already a runner");
                    return true;
                }
            }
            for (String i : main.hunters) {
                if (target.getName().equalsIgnoreCase(i)) {
                    main.hunters.remove(i);
                    break;
                }
            }
            for (String i : main.spectators) {
                if (target.getName().equalsIgnoreCase(i)) {
                    main.spectators.remove(i);
                    break;
                }
            }
            main.runners.add(target.getName());
            main.runnersTeam.addEntry(target.getName());
            if(!main.discord.AssignRole(ManhuntTeam.RUNNER, target.getName())){
                commandSender.sendMessage("Could not assign Discord role. Make sure the target's username is also their Discord nickname.");
            }
            target.sendMessage("You have been marked as a speedrunner.");
            commandSender.sendMessage("Marked player as speedrunner");
            return true;
        } else if ("spectator".equals(label)) {
            if (args.length != 1) return false;
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                commandSender.sendMessage("Target is not online");
                return false;
            }
            for (String i : main.spectators) {
                if (target.getName().equalsIgnoreCase(i)) {
                    commandSender.sendMessage("Target is already a spectator");
                    return true;
                }
            }
            for (String i : main.runners) {
                if (target.getName().equalsIgnoreCase(i)) {
                    main.runners.remove(i);
                    break;
                }
            }
            for (String i : main.hunters) {
                if (target.getName().equalsIgnoreCase(i)) {
                    main.hunters.remove(i);
                    break;
                }
            }
            main.spectators.add(target.getName());
            main.spectatorsTeam.addEntry(target.getName());
            if(!main.discord.AssignRole(ManhuntTeam.SPECTATOR, target.getName())){
                commandSender.sendMessage("Could not assign Discord role. Make sure the target's username is also their Discord nickname.");
            }
            target.sendMessage("You have been marked as a spectator.");
            commandSender.sendMessage("Marked player as spectator");
            return true;
        } else if ("start".equals(label)) {
            if (main.runners.size() < 1) {
                commandSender.sendMessage("Not enough speedrunners to start");
                return true;
            }

            main.targets.clear();
            int headStartDuration = main.getConfig().getInt("headStartDuration");

            for(String i : main.spectatorsTeam.getEntries()){
                main.spectatorsTeam.removeEntry(i);
            }
            for(String i : main.huntersTeam.getEntries()){
                main.huntersTeam.removeEntry(i);
            }
            for(String i : main.runnersTeam.getEntries()){
                main.runnersTeam.removeEntry(i);
            }

            for (String i : main.spectators) {
                Player player = Bukkit.getPlayer(i);
                if (player == null) continue;
                player.setGameMode(GameMode.SPECTATOR);
                main.spectatorsTeam.addEntry(player.getName());
                if(!main.discord.AssignRole(ManhuntTeam.SPECTATOR, player.getName())){
                    commandSender.sendMessage("Could not assign Discord role. Make sure the target's username is also their Discord nickname.");
                }
            }
            for (String i : main.runners) {
                Player player = Bukkit.getPlayer(i);
                if (player == null) continue;
                player.setGameMode(GameMode.SURVIVAL);
                player.setHealth(20.0);
                player.setFoodLevel(20);
                player.getInventory().clear();
                main.runnersTeam.addEntry(player.getName());
                if(!main.discord.AssignRole(ManhuntTeam.RUNNER, player.getName())){
                    commandSender.sendMessage("Could not assign Discord role. Make sure the target's username is also their Discord nickname.");
                }
            }
            for (String i : main.hunters) {
                Player player = Bukkit.getPlayer(i);
                if (player == null) continue;
                player.setGameMode(GameMode.SURVIVAL);
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * headStartDuration, 5));
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * headStartDuration, 3));
                player.setHealth(20.0);
                player.setFoodLevel(20);
                player.getInventory().clear();
                player.getInventory().addItem(new ItemStack(Material.COMPASS, 1));
                main.huntersTeam.addEntry(player.getName());
                if(!main.discord.AssignRole(ManhuntTeam.HUNTER, player.getName())){
                    commandSender.sendMessage("Could not assign Discord role. Make sure the target's username is also their Discord nickname.");
                }
            }
            if(main.discord.enabled){
                main.discord.trackManager.reset();
                main.discord.trackManager.playSpecialTrack("headstart");
            }
            BukkitScheduler scheduler = getServer().getScheduler();
            compassTask = scheduler.scheduleSyncRepeatingTask(main, new Runnable() {
                public void run() {
                    UpdateCompass();
                }
            }, 0L, 20L);

            if(main.discord.enabled) {
                dangerLevelTask = scheduler.scheduleSyncRepeatingTask(main, new Runnable() {
                    public void run() {
                        main.discord.trackManager.updateDangerLevel();
                    }
                }, 0L, 20L * 5);
            }
            getServer().broadcastMessage("Manhunt started!");

            return true;
        } else if ("end".equals(label)) {
            BukkitScheduler scheduler = getServer().getScheduler();
            if (compassTask != -1) {
                scheduler.cancelTask(compassTask);
            }
            if (dangerLevelTask != -1) {
                scheduler.cancelTask(dangerLevelTask);
            }
            getServer().broadcastMessage("Manhunt stopped!");

            return true;
        } else if("compass".equals(label)){
            Player sender = (Player) commandSender;
            sender.getInventory().addItem(new ItemStack(Material.COMPASS, 1));
            commandSender.sendMessage("Here you go!");

            return true;
        }else if("music".equals(label)){
            if(!main.discord.enabled){
                commandSender.sendMessage("Cannot use this command when Discord is disabled.");
            }
            if(args.length == 0){
                return false;
            }
            commandSender.sendMessage(main.discord.trackManager.parseCommand(args[0]));
            return true;
        }
        return false;
    }
}
