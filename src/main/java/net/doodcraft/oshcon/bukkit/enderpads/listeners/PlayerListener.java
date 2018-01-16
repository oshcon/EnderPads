package net.doodcraft.oshcon.bukkit.enderpads.listeners;

import net.doodcraft.oshcon.bukkit.enderpads.EnderPadsPlugin;
import net.doodcraft.oshcon.bukkit.enderpads.api.EnderPad;
import net.doodcraft.oshcon.bukkit.enderpads.api.EnderPadAPI;
import net.doodcraft.oshcon.bukkit.enderpads.api.EnderPadClickEvent;
import net.doodcraft.oshcon.bukkit.enderpads.config.Configuration;
import net.doodcraft.oshcon.bukkit.enderpads.config.Settings;
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

            if (StaticMethods.isOffHandClick(event)) {
                return;
            }

            if (EnderPadAPI.isValidPlate(type)) {

                EnderPad enderPad = new EnderPad(clicked.getRelative(BlockFace.DOWN).getLocation());

                if (enderPad.isValid()) {

                    EnderPadClickEvent clickEvent = new EnderPadClickEvent(player, enderPad);
                    Bukkit.getPluginManager().callEvent(clickEvent);

                    if (!clickEvent.isCancelled()) {

                        if (!StaticMethods.hasPermission(player, "enderpads.seeinfo", false)) {
                            return;
                        }

                        if (!UUIDCache.getUniqueID(player.getName()).equals(enderPad.getOwnerUUID())) {
                            if (!StaticMethods.hasPermission(player, "enderpads.seeinfo.others", false)) {
                                return;
                            }
                        }

                        if (player.isSneaking()) {
                            event.setCancelled(true);
                        }

                        String linkId = enderPad.getLinkId();

                        if (StaticMethods.hasPermission(player, "enderpads.command.list", false)) {

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

                        if (StaticMethods.hasPermission(player, "enderpads.seeinfo.owner", false)) {

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

            if (EnderPadAPI.isValidPlate(type)) {

                Block centerBlock = event.getClickedBlock().getRelative(BlockFace.DOWN);

                if (centerBlock.getType().equals(Material.valueOf(Settings.centerMaterial.split("~")[0].toUpperCase()))) {

                    if (EnderPadsPlugin.playerCooldowns.containsKey(player.getName())) {

                        if ((System.currentTimeMillis() - EnderPadsPlugin.playerCooldowns.get(player.getName()) > (Settings.playerCooldown * 1000))) {

                            if (player.getPassengers().size() >= 1) {
                                for (Entity e : player.getPassengers()) {
                                    e.eject();
                                }
                            }

                            if (StaticMethods.hasPermission(player, "enderpads.use", true)) {
                                EnderPad enderPad = EnderPadAPI.getPadFromLocation(centerBlock.getLocation());
                                EnderPadAPI.teleportEntity(enderPad, player);
                            }

                        } else {

                            long remaining = (Settings.playerCooldown * 1000) - (System.currentTimeMillis() - EnderPadsPlugin.playerCooldowns.get(player.getName()));
                            long portion = (Settings.playerCooldown * 1000) / 4;

                            if (remaining < (Settings.playerCooldown * 1000) - portion) {

                                if (StaticMethods.hasPermission(player, "enderpads.use", true)) {

                                    if (!Settings.cooldownMessage.equals(" ") || Settings.cooldownMessage != null) {

                                        if (!StaticMethods.isVanished(player)) {
                                            ReflectionUtil.sendActionBar(player, StaticMethods.addColor(Settings.cooldownMessage.replaceAll("<remaining>", String.valueOf(remaining))));
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

                        EnderPad enderPad = EnderPadAPI.getPadFromLocation(centerBlock.getLocation());
                        EnderPadAPI.teleportEntity(enderPad, player);
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

        Block block = event.getBlock();
        Material material = block.getType();

        if (BlockHelper.isPhysicsBlock(material)) {
            return;
        }

        String valid = Settings.centerMaterial.toUpperCase();

        if (EnderPadAPI.isValidPlate(material)) {
            Block below = block.getRelative(BlockFace.DOWN);
            String checkBelow = EnderPadAPI.getBlockString(below);

            if (checkBelow.equals(valid)) {
                EnderPadAPI.runTelepadCheck(below, player, true);
            }
        }

        String check = EnderPadAPI.getBlockString(block);

        if (check.equals(valid)) {
            EnderPadAPI.runTelepadCheck(block, player, true);
        }

        for (BlockFace face : EnderPadsPlugin.faces) {
            Block b = block.getRelative(face);
            String checkB = EnderPadAPI.getBlockString(b);

            if (checkB.equals(valid)) {
                EnderPadAPI.runTelepadCheck(b, player, true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {

        Player player = event.getPlayer();

        if (player != null) {
            EnderPadAPI.destroyCheck(event.getBlock(), player);
        } else {
            EnderPadAPI.destroyCheck(event.getBlock(), null);
        }
    }
}