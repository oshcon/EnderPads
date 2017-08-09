package net.doodcraft.oshcon.bukkit.enderpads.listeners;

import net.doodcraft.oshcon.bukkit.enderpads.EnderPadsPlugin;
import net.doodcraft.oshcon.bukkit.enderpads.api.EnderPad;
import net.doodcraft.oshcon.bukkit.enderpads.api.EnderPadAPI;
import net.doodcraft.oshcon.bukkit.enderpads.api.EnderPadUseEvent;
import net.doodcraft.oshcon.bukkit.enderpads.config.Settings;
import net.doodcraft.oshcon.bukkit.enderpads.util.StaticMethods;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class EnderPadListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onUse(EnderPadUseEvent event) {
        if (event.getEntity() instanceof Player) {
            final Player player = (Player) event.getEntity();

            EnderPad origin = event.getOriginEnderPad();
            EnderPad dest = event.getDestinationEnderPad();

            Location from = origin.getLocation().add(0, 1, 0);
            Location to = dest.getLocation().add(0, 1, 0);

            if (!dest.isValid()) {
                dest.delete(null);
                EnderPadAPI.teleportEntity(origin, player);
                return;
            }

            EnderPadsPlugin.playerCooldowns.put(player.getName(), System.currentTimeMillis());

            if (Settings.logUse || Settings.debug) {
                StaticMethods.log("&b" + player.getName() + " used an EnderPad: &d" + EnderPadAPI.getLocString(from) + " &b-> &d" + EnderPadAPI.getLocString(to));
            }

            to.getChunk().load();
            to.setYaw(player.getLocation().getYaw());
            to.setPitch(player.getLocation().getPitch());

            final Location finalTo = to;

            Bukkit.getScheduler().runTaskLater(EnderPadsPlugin.plugin, new Runnable() {
                @Override
                public void run() {
                    player.teleport(finalTo, PlayerTeleportEvent.TeleportCause.PLUGIN);
                }
            }, 1L);
        }
    }
}