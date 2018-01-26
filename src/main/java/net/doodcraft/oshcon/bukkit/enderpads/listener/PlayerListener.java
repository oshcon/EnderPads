package net.doodcraft.oshcon.bukkit.enderpads.listener;

import net.doodcraft.oshcon.bukkit.enderpads.EnderPadsPlugin;
import net.doodcraft.oshcon.bukkit.enderpads.cache.EnderPadCache;
import net.doodcraft.oshcon.bukkit.enderpads.cache.PermissionCache;
import net.doodcraft.oshcon.bukkit.enderpads.cache.UUIDCache;
import net.doodcraft.oshcon.bukkit.enderpads.config.Configuration;
import net.doodcraft.oshcon.bukkit.enderpads.config.Settings;
import net.doodcraft.oshcon.bukkit.enderpads.enderpad.EnderPad;
import net.doodcraft.oshcon.bukkit.enderpads.enderpad.EnderPadMethods;
import net.doodcraft.oshcon.bukkit.enderpads.event.EnderPadClickEvent;
import net.doodcraft.oshcon.bukkit.enderpads.util.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.File;
import java.util.List;

public class PlayerListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block clicked = event.getClickedBlock();
        Material type = clicked.getType();

        if (event.getAction().equals(Action.valueOf("RIGHT_CLICK_BLOCK"))) {
            if (GeneralMethods.isOffHandClick(event)) {
                return;
            }

            if (EnderPadMethods.isPlate(type)) {

                EnderPad enderPad = new EnderPad(clicked.getRelative(BlockFace.DOWN).getLocation());

                if (enderPad.isValid()) {

                    EnderPadClickEvent clickEvent = new EnderPadClickEvent(player, enderPad);
                    Bukkit.getPluginManager().callEvent(clickEvent);

                    if (!clickEvent.isCancelled()) {

                        if (!PermissionCache.hasPermission(player, "enderpads.seeinfo", false)) {
                            return;
                        }

                        if (!UUIDCache.getUniqueID(player.getName()).equals(enderPad.getOwnerUUID())) {
                            if (!PermissionCache.hasPermission(player, "enderpads.seeinfo.others", false)) {
                                return;
                            }
                        }

                        if (player.isSneaking()) {
                            event.setCancelled(true);
                        }

                        String linkId = enderPad.getCurrentLink().toString();

                        if (PermissionCache.hasPermission(player, "enderpads.command.list", false)) {

                            if (player.isSneaking()) {
                                StaticMenuMethods.openPadListPageByLink(player, linkId, 0);
                                event.setCancelled(true);
                                return;
                            } else {
                                StaticMenuMethods.openPadOptions(player, enderPad);
                                event.setCancelled(true);
                                return;
                            }
                        }

                        Configuration linkedPads = new Configuration(EnderPadsPlugin.plugin.getDataFolder() + File.separator + "data" + File.separator + "linked.yml");

                        List<String> linked = linkedPads.getStringList(linkId);

                        player.sendMessage(StringParser.parse(Settings.enderPad, null, enderPad, null, false, false));

                        if (linked.size() == 1) {
                            player.sendMessage(StringParser.parse(Settings.links, null, enderPad, Settings.numbersZero, false, false));
                        } else {
                            player.sendMessage(StringParser.parse(Settings.links, null, enderPad, NumberConverter.convert(linked.size() - 1), false, false));
                        }

                        if (PermissionCache.hasPermission(player, "enderpads.seeinfo.owner", false)) {

                            if (Bukkit.getPlayer(enderPad.getOwnerUUID()) != null) {

                                if (Bukkit.getPlayer(enderPad.getOwnerUUID()).isOnline()) {
                                    player.sendMessage(StringParser.parse(Settings.owner, null, enderPad, enderPad.getOwnerName(), false, true));
                                    return;
                                }
                            }

                            player.sendMessage(StringParser.parse(Settings.owner, null, enderPad, enderPad.getOwnerName(), false, false));
                        }
                    }
                }
            }
        }

        if (event.getAction().equals(Action.PHYSICAL)) {
            if (EnderPadMethods.isPlate(type)) {
                Block centerBlock = event.getClickedBlock().getRelative(BlockFace.DOWN);
                if (centerBlock.getType().equals(Material.valueOf(Settings.centerMaterial.split("~")[0]))) {
                    if (EnderPadsPlugin.playerCooldowns.containsKey(player.getName())) {
                        if ((System.currentTimeMillis() - EnderPadsPlugin.playerCooldowns.get(player.getName()) > (Settings.playerCooldown * 1000))) {
                            if (PermissionCache.hasPermission(player, "enderpads.use", true)) {
                                if (player.getPassengers().size() >= 1) {
                                    for (Entity e : player.getPassengers()) {
                                        e.eject();
                                    }
                                }

                                EnderPad enderPad = EnderPadCache.getEnderPad(centerBlock.getLocation());
                                if (enderPad != null) {
                                    enderPad.teleportEntity(player);
                                }
                            }
                        } else {
                            long remaining = (Settings.playerCooldown * 1000) - (System.currentTimeMillis() - EnderPadsPlugin.playerCooldowns.get(player.getName()));
                            long portion = (Settings.playerCooldown * 1000) / 4;
                            if (remaining < (Settings.playerCooldown * 1000) - portion) {
                                if (PermissionCache.hasPermission(player, "enderpads.use", true)) {
                                    if (!Settings.cooldownMessage.equals(" ") || Settings.cooldownMessage != null) {
                                        if (!GeneralMethods.isVanished(player)) {
                                            ReflectionUtil.sendActionBar(player, EnderPadsPlugin.logger.addColor(Settings.cooldownMessage.replaceAll("<remaining>", String.valueOf(remaining))));
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        if (player.getPassengers().size() >= 1) {
                            for (Entity e : player.getPassengers()) {
                                e.eject();
                            }
                        }
                        EnderPad enderPad = EnderPadCache.getEnderPad(centerBlock.getLocation());
                        if (enderPad != null) {
                            enderPad.teleportEntity(player);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        Block block = event.getBlock().getLocation().getBlock();
        EnderPadMethods.saveCheck(player, block, true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player != null) {
            EnderPadMethods.deleteCheck(player, event.getBlock(), true);
        } else {
            EnderPadMethods.deleteCheck(null, event.getBlock(), true);
        }
    }
}