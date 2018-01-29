package net.doodcraft.oshcon.bukkit.enderpads.listener;

import net.doodcraft.oshcon.bukkit.enderpads.PadsPlugin;
import net.doodcraft.oshcon.bukkit.enderpads.config.Settings;
import net.doodcraft.oshcon.bukkit.enderpads.enderpad.EnderPad;
import net.doodcraft.oshcon.bukkit.enderpads.enderpad.SmallLocation;
import net.doodcraft.oshcon.bukkit.enderpads.event.EnderPadUseEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class EnderPadListener implements Listener {

    @EventHandler
    public void onEntityUse(EnderPadUseEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            final Entity entity = event.getEntity();
            EnderPad origin = event.getOriginEnderPad();
            EnderPad dest = event.getDestinationEnderPad();
            Location to = dest.getBukkitLocation();
            if (!dest.isValid()) {
                dest.delete(null);
                origin.teleportEntity(entity, null);
                return;
            }
            PadsPlugin.entityCooldowns.put(entity.getEntityId(), System.currentTimeMillis());
            to.setYaw(entity.getLocation().getYaw());
            to.setPitch(entity.getLocation().getPitch());
            final Location finalTo = to.add(0, 1, 0);
            Bukkit.getScheduler().runTaskLater(PadsPlugin.plugin, new Runnable() {
                @Override
                public void run() {
                    entity.teleport(finalTo, PlayerTeleportEvent.TeleportCause.PLUGIN);
                }
            }, 1L);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerUse(EnderPadUseEvent event) {
        if (event.getEntity() instanceof Player) {
            final Player player = (Player) event.getEntity();
            EnderPad origin = event.getOriginEnderPad();
            EnderPad dest = event.getDestinationEnderPad();
            Location from = origin.getBukkitLocation();
            Location to = dest.getBukkitLocation();
            if (!dest.isValid()) {
                dest.delete(null);
                origin.teleportEntity(player, null);
                return;
            }
            PadsPlugin.playerCooldowns.put(player.getName(), System.currentTimeMillis());
            if (Settings.logUse || Settings.debug) {
                PadsPlugin.logger.log("&b" + player.getName() + " used an EnderPad: &d" + new SmallLocation(from).toString() + " &b-> &d" + new SmallLocation(to).toString());
            }
            to.getChunk().load();
            to.setYaw(player.getLocation().getYaw());
            to.setPitch(player.getLocation().getPitch());
            final Location finalTo = to.add(0, 1, 0);
            Bukkit.getScheduler().runTaskLater(PadsPlugin.plugin, new Runnable() {
                @Override
                public void run() {
                    player.teleport(finalTo, PlayerTeleportEvent.TeleportCause.PLUGIN);
                }
            }, 1L);
        }
    }
}