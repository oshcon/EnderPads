package net.doodcraft.oshcon.bukkit.enderpads.util;

import net.doodcraft.oshcon.bukkit.enderpads.EnderPadsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PermissionCache implements Listener {

    private static Map<UUID, Map> permissions = new HashMap<>();

    public static boolean hasPermission(final UUID uuid, final String node) {

        if (permissions.containsKey(uuid)) {
            if (permissions.get(uuid).containsKey(node)) {
                return (boolean) permissions.get(uuid).get(node);
            }
        }

        // Doesn't exist, get the result then cache it.
        boolean result = getPermissionResult(uuid, node);

        cachePermission(uuid, node, result);

        Bukkit.getScheduler().runTaskLater(EnderPadsPlugin.plugin, new Runnable() {
            @Override
            public void run() {
                removePermission(uuid, node);
            }
        },18000L);

        return result;
    }

    private static void cachePermission(UUID uuid, String node, Boolean result) {
        if (permissions.containsKey(uuid)) {
            Map<String, Boolean> nodes = permissions.get(uuid);
            nodes.put(node, result);
            permissions.put(uuid, nodes);
        } else {
            Map<String, Boolean> nodes = new HashMap<>();
            nodes.put(node, result);
            permissions.put(uuid, nodes);
        }
    }

    private static void removePermission(UUID uuid, String node) {
        if (permissions.containsKey(uuid)) {
            permissions.get(uuid).remove(node);
        }
    }

    private static void removeAllPermissions(UUID uuid) {
        permissions.remove(uuid);
    }

    private static boolean getPermissionResult(UUID uuid, String node) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

        if (player.isOnline()) {

            if (player.isOp()) {
                return true;
            }

            if (player.getPlayer().hasPermission("enderpads.*")) {
                return true;
            }

            if (player.getPlayer().hasPermission(node)) {
                return true;
            }
        }

        return false;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        removeAllPermissions(event.getPlayer().getUniqueId());
    }
}