package net.doodcraft.oshcon.bukkit.enderpads.listener;

import net.doodcraft.oshcon.bukkit.enderpads.enderpad.EnderPadMethods;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;

public class BlockExplodeListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onExplode(BlockExplodeEvent event) {
        for (Block block : event.blockList()) {
            if (block.isEmpty()) {
                EnderPadMethods.deleteCheck(null, block, true);
            }
        }
    }
}