package net.doodcraft.oshcon.bukkit.enderpads.listeners;

import net.doodcraft.oshcon.bukkit.enderpads.EnderPadsPlugin;
import net.doodcraft.oshcon.bukkit.enderpads.api.EnderPadAPI;
import net.doodcraft.oshcon.bukkit.enderpads.util.Compatibility;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;

public class BlockListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onBurn(BlockBurnEvent event) {
        EnderPadAPI.destroyCheck(event.getBlock(), null);
    }

    @EventHandler(ignoreCancelled = true)
    public void onGrow(BlockGrowEvent event) {
        EnderPadAPI.destroyCheck(event.getBlock(), null);
    }

    @EventHandler(ignoreCancelled = true)
    public void onSpread(BlockSpreadEvent event) {
        EnderPadAPI.destroyCheck(event.getBlock(), null);
    }

    @EventHandler(ignoreCancelled = true)
    public void onFade(BlockFadeEvent event) {
        EnderPadAPI.destroyCheck(event.getBlock(), null);
    }

    @EventHandler(ignoreCancelled = true)
    public void onExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            EnderPadAPI.destroyCheck(block, null);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onRetract(BlockPistonRetractEvent event) {
        // The addition of slime blocks and their interaction with pistons.
        if (Compatibility.isSupported(EnderPadsPlugin.version, "1.8", "2.0")) {
            for (Block block : event.getBlocks()) {
                EnderPadAPI.destroyCheck(block, null);
            }
        } else {
            EnderPadAPI.destroyCheck(event.getBlock(), null);
            EnderPadAPI.destroyCheck(event.getRetractLocation().getBlock(), null);
        }
    }
}