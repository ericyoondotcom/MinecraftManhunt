package com.yoonicode.minecraftmanhunt;

import net.dv8tion.jda.api.Permission;
import org.bukkit.*;
import org.bukkit.block.Skull;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.annotation.Target;
import java.util.List;

import static com.yoonicode.minecraftmanhunt.PluginCommands.gameIsRunning;
import static org.bukkit.Bukkit.getServer;

public class PluginListener implements Listener {

    boolean setRunnersToSpecOnDeath;
    static boolean worldBorderModified = false;
    static World world;
    PluginMain main;
    public PluginListener(PluginMain main) {
        this.main = main;
        setRunnersToSpecOnDeath = main.getConfig().getBoolean("setRunnersToSpecOnDeath", true);
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e){
        Player player = e.getPlayer();

        if(player.getEquipment().getItemInMainHand().getType() == Material.COMPASS){
            if(!main.playerIsOnTeam(player)){
                if(player.isOp()){
                    player.sendMessage("Join a Manhunt team before using the compass!");
                }
                return;
            }
            if(main.runners.contains(player.getName()) && !main.debugMode){
                player.sendMessage("Speedrunners cannot use the compass!");
                return;
            }
            if(main.commands.compassTask == -1){
                player.sendMessage("Start the Manhunt game before using the compass!");
                return;
            }
            TargetSelectInventory inv = new TargetSelectInventory(main);
            inv.DisplayToPlayer(player);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player hunter = (Player) event.getWhoClicked();

        ItemStack clickedHead = event.getCurrentItem();
        if (event.getView().getTitle().equals(TargetSelectInventory.INVENTORY_NAME)) {
            if(!main.playerIsOnTeam(hunter)){
                hunter.sendMessage("You're not on a Manhunt team!");
                event.setCancelled(true);
                return;
            }

            if(clickedHead == null || clickedHead.getType() != Material.PLAYER_HEAD){
                main.logger.warning("Item clicked is not player head.");
                event.setCancelled(true);
                return;
            }
            if(!clickedHead.hasItemMeta()) {
                main.logger.warning("Clicked head has no item meta.");
                hunter.sendMessage("Something went wrong: Does not have ItemMeta");
                event.setCancelled(true);
                return;
            }
            ItemMeta itemmeta = clickedHead.getItemMeta();
            if(!(itemmeta instanceof SkullMeta)){
                main.logger.warning("Clicked head meta is not instanceof SkullMeta.");
                main.logger.info(itemmeta.getClass().toString());
                hunter.sendMessage("Something went wrong: Not an instanceof SkullMeta");
                event.setCancelled(true);
                return;
            }
            SkullMeta meta = (SkullMeta)itemmeta;
            OfflinePlayer target = meta.getOwningPlayer();
            String targetName = target.getName();
            if(targetName == null){
                targetName = meta.getDisplayName();
                main.logger.info("Target name is null, applying offline mode workaround. Using item display name: " + targetName);
            }
            main.targets.put(hunter.getName(), targetName);
            event.setCancelled(true);
            hunter.closeInventory();
            hunter.sendMessage("Compass is now targeting " + targetName);
        }
    }

    @EventHandler
    public void onPlayerEnterPortal(PlayerPortalEvent event){
        main.portals.put(event.getPlayer().getName(), event.getFrom());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!worldBorderModified && main.getConfig().getBoolean("preGameWorldBorder", false)) {
            Location joinLoc = event.getPlayer().getLocation();
            world = event.getPlayer().getWorld();
            WorldBorder wb = world.getWorldBorder();

            wb.setDamageAmount(0);
            wb.setWarningDistance(0);
            wb.setCenter(joinLoc);
            wb.setSize(main.getConfig().getInt("preGameBorderSize", 100));

            worldBorderModified = true;
        }
    }

    @EventHandler
    public void onPlayerAttacked(EntityDamageByEntityEvent event) {
        if (!PluginCommands.gameIsRunning && main.getConfig().getBoolean("startGameByHit", false)) {
            Entity victim = event.getEntity();
            Entity attacker = event.getDamager();
            EntityDamageEvent.DamageCause cause = event.getCause();
            if (attacker.getType() == EntityType.PLAYER && victim.getType() == EntityType.PLAYER
                    && cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK
                    && PluginMain.huntersTeam.getEntries().contains(victim.getName())
                    && PluginMain.runnersTeam.getEntries().contains(attacker.getName())) {
                PluginCommands.hitHasRegistered = true;
                attacker.getServer().dispatchCommand(getServer().getConsoleSender(), "start");
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event){
        String playerName = event.getPlayer().getName();
        if(gameIsRunning && main.hunters.contains(playerName)){
            event.getPlayer().getInventory().addItem(new ItemStack(Material.COMPASS, 1));
        }
        if(setRunnersToSpecOnDeath && gameIsRunning && main.runners.contains(playerName)){
            event.getPlayer().setGameMode(GameMode.SPECTATOR);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        if(main.hunters.contains(event.getEntity().getName())){
            event.getDrops().removeIf(i -> i.getType() == Material.COMPASS);
        }
    }

    @EventHandler
    public void onAutocomplete(TabCompleteEvent event){
        String buffer = event.getBuffer();
        if(!buffer.startsWith("/")) return;
        String[] args = buffer.split(" ");

        List<String> completions = main.commands.getCompletions(args, event.getCompletions());

        event.setCompletions(completions);
    }

}
