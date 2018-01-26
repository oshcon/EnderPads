package net.doodcraft.oshcon.bukkit.enderpads.listener;

import net.doodcraft.oshcon.bukkit.enderpads.EnderPadsPlugin;
import net.doodcraft.oshcon.bukkit.enderpads.enderpad.EnderPadMethods;
import net.doodcraft.oshcon.bukkit.enderpads.util.Compatibility;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

public class BlockListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onBurn(BlockBurnEvent event) {
        EnderPadMethods.deleteCheck(null, event.getBlock(), true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            EnderPadMethods.deleteCheck(null, block, true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onRetract(BlockPistonRetractEvent event) {
        // The addition of slime blocks and their interaction with pistons.
        if (Compatibility.isSupported(EnderPadsPlugin.version, "1.8", "2.0")) {
            for (Block block : event.getBlocks()) {
                EnderPadMethods.deleteCheck(null, block, true);
            }
        } else {
            EnderPadMethods.deleteCheck(null, event.getBlock(), true);
            EnderPadMethods.deleteCheck(null, event.getRetractLocation().getBlock(), true);
        }
    }
}