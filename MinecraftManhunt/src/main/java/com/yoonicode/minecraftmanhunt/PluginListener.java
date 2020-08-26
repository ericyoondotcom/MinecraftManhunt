package com.yoonicode.minecraftmanhunt;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.annotation.Target;

public class PluginListener implements Listener {

    PluginMain main;

    public PluginListener(PluginMain main) {
        this.main = main;
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e){
        Player player = e.getPlayer();
        if(player.getEquipment().getItemInMainHand().getType() == Material.COMPASS){
            TargetSelectInventory inv = new TargetSelectInventory(main);
            inv.DisplayToPlayer(player);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player hunter = (Player) event.getWhoClicked();
        ItemStack clickedHead = event.getCurrentItem();
        if (event.getView().getTitle().equals(TargetSelectInventory.INVENTORY_NAME)) {
            SkullMeta meta = (SkullMeta) clickedHead.getItemMeta();
            OfflinePlayer target = meta.getOwningPlayer();
            main.targets.put(hunter.getName(), target.getName());
            event.setCancelled(true);
            hunter.closeInventory();
            hunter.sendMessage("Compass is now targeting " + target.getName());
        }
    }
}
