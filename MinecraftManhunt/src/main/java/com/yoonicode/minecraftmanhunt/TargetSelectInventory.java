package com.yoonicode.minecraftmanhunt;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;

public class TargetSelectInventory {

    public static final String INVENTORY_NAME = "Select player to track";

    Inventory inv;
    PluginMain main;


    public TargetSelectInventory(PluginMain main){
        this.main = main;
        inv = Bukkit.createInventory(null, 9, INVENTORY_NAME);
        int pos = 0;
        for(String i : main.runners){
            Player runner = Bukkit.getPlayer(i);
            if(runner == null) continue;
            ItemStack stack = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta meta = (SkullMeta) stack.getItemMeta();
            meta.setOwningPlayer(runner);
            meta.setDisplayName(runner.getName());

            String easteregg = main.getConfig().getString(runner.getName(), "");
            if(easteregg.length() > 0) meta.setLore(Arrays.asList(easteregg));
            stack.setItemMeta(meta);

            inv.setItem(pos, stack);
            pos++;
        }
    }

    public Inventory getInventory() {
        return inv;
    }

    public void DisplayToPlayer(Player player){
        player.openInventory(inv);
    }

}
