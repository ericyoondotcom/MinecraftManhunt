package com.yoonicode.minecraftmanhunt;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class PluginCommands implements CommandExecutor {

    public static final String[] registeredCommands = {
            "hunter",
            "speedrunner",
            "spectator"
    };

    public ArrayList<String> hunters = new ArrayList<String>();
    public ArrayList<String> runners = new ArrayList<String>();


    private final PluginMain main;

    public PluginCommands(PluginMain main) {
        this.main = main;
    }

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        return false;
    }
}
