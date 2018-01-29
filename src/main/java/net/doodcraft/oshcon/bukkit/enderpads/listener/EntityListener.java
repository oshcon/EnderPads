package net.doodcraft.oshcon.bukkit.enderpads.listener;

import net.doodcraft.oshcon.bukkit.enderpads.PadsPlugin;
import net.doodcraft.oshcon.bukkit.enderpads.config.Settings;
import net.doodcraft.oshcon.bukkit.enderpads.enderpad.EnderPad;
import net.doodcraft.oshcon.bukkit.enderpads.enderpad.EnderPadMethods;
import net.doodcraft.oshcon.bukkit.enderpads.enderpad.SmallLocation;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;

public class EntityListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onChange(EntityChangeBlockEvent event) {
        EnderPadMethods.deleteCheck(null, event.getBlock(), true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onChange(EntityBlockFormEvent event) {
        EnderPadMethods.deleteCheck(null, event.getBlock(), true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onExplosion(EntityExplodeEvent event) {
        for (Block block : event.blockList()) {
            EnderPadMethods.deleteCheck(null, block, true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityInteract(EntityInteractEvent event) {
        Entity entity = event.getEntity();
        Block clicked = event.getBlock();
        Material type = clicked.getType();
        if (!(entity instanceof Player)) {
            if (entity instanceof Creature) {
                if (!Settings.teleportMobs) {
                    return;
                }
            }
            if (entity instanceof Item) {
                if (!Settings.teleportItems) {
                    return;
                }
            }
            if (EnderPadMethods.isPlate(type)) {
                Block centerBlock = event.getBlock().getRelative(BlockFace.DOWN);
                EnderPad enderPad = PadsPlugin.padCache.getEnderPad(new SmallLocation(centerBlock.getLocation()));
                if (enderPad != null) {
                    if (PadsPlugin.entityCooldowns.containsKey(entity.getEntityId())) {
                        if ((System.currentTimeMillis() - PadsPlugin.entityCooldowns.get(entity.getEntityId()) > (Settings.playerCooldown * 1000))) {
                            if (entity.getPassengers().size() >= 1) {
                                for (Entity e : entity.getPassengers()) {
                                    e.eject();
                                }
                            }
                            enderPad.teleportEntity(entity, null);
                        }
                    } else {
                        if (entity.getPassengers().size() >= 1) {
                            for (Entity e : entity.getPassengers()) {
                                e.eject();
                            }
                        }
                        enderPad.teleportEntity(entity, null);
                    }
                }
            }
        }
    }
}