package com.yoonicode.minecraftmanhunt;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

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
}
