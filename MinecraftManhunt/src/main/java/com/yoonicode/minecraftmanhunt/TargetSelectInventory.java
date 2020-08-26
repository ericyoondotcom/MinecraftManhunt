package com.yoonicode.minecraftmanhunt;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
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
        for(String i : main.hunters){
            Player hunter = Bukkit.getPlayer(i);
            if(hunter == null) continue;
            ItemStack stack = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta meta = (SkullMeta) stack.getItemMeta();
            meta.setOwningPlayer(hunter);
            meta.setDisplayName(hunter.getName());
            if(hunter.getName().equalsIgnoreCase("i18n")) meta.setLore(Arrays.asList("The best player ever"));
            if(hunter.getName().equalsIgnoreCase("xnvt")) meta.setLore(Arrays.asList("<3"));
            stack.setItemMeta(meta);

            inv.setItem(pos, stack);
        }
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player hunter = (Player) event.getWhoClicked();
        ItemStack clickedHead = event.getCurrentItem();

        if (event.getView().getTitle().equals(INVENTORY_NAME)) {
            SkullMeta meta = (SkullMeta) clickedHead.getItemMeta();
            OfflinePlayer target = meta.getOwningPlayer();
            main.targets.put(hunter.getName(), target.getName());
            event.setCancelled(true);
            hunter.closeInventory();
        }
    }

    public Inventory getInventory() {
        return inv;
    }

    public void DisplayToPlayer(Player player){
        player.openInventory(inv);
    }

}
