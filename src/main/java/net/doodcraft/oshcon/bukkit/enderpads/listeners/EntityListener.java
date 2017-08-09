package net.doodcraft.oshcon.bukkit.enderpads.listeners;

import net.doodcraft.oshcon.bukkit.enderpads.api.EnderPadAPI;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class EntityListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onChange(EntityChangeBlockEvent event) {
        EnderPadAPI.runTelepadCheck(event.getBlock(), false);
    }

    @EventHandler(ignoreCancelled = true)
    public void onChange(EntityBlockFormEvent event) {
        EnderPadAPI.runTelepadCheck(event.getBlock(), false);
    }

    @EventHandler(ignoreCancelled = true)
    public void onExplosion(EntityExplodeEvent event) {
        for (Block block : event.blockList()) {
            EnderPadAPI.destroyCheck(block, null);
        }
    }
}