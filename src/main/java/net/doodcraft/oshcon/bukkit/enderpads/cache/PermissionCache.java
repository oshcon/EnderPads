package net.doodcraft.oshcon.bukkit.enderpads.cache;

import net.doodcraft.oshcon.bukkit.enderpads.PadsPlugin;
import net.doodcraft.oshcon.bukkit.enderpads.config.Settings;
import net.doodcraft.oshcon.bukkit.enderpads.util.StringParser;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PermissionCache implements Listener {

    private Map<UUID, Map<String, Boolean>> permissions;

    public PermissionCache() {
        this.permissions = new ConcurrentHashMap<>();
    }

    private boolean hasPermission(final UUID uuid, final String node) {
        if (permissions.containsKey(uuid)) {
            if (permissions.get(uuid).containsKey(node)) {
                return permissions.get(uuid).get(node);
            }
        }
        boolean result = getPermissionResult(uuid, node);
        cachePermission(uuid, node, result);
        Bukkit.getScheduler().runTaskLater(PadsPlugin.plugin, new Runnable() {
            @Override
            public void run() {
                removePermission(uuid, node);
            }
        }, 18000L);
        return result;
    }

    private void cachePermission(UUID uuid, String node, Boolean result) {
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

    private void removePermission(UUID uuid, String node) {
        if (permissions.containsKey(uuid)) {
            permissions.get(uuid).remove(node);
        }
    }

    public void removeAllPermissions(UUID uuid) {
        permissions.remove(uuid);
    }

    private boolean getPermissionResult(UUID uuid, String node) {
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

    public Boolean hasPermission(Player player, String node, Boolean sendError) {
        if (hasPermission(player.getUniqueId(), node)) {
            return true;
        } else {
            if (sendError) {
                player.sendMessage(StringParser.parse(Settings.noPermission, null, null, null, false, false));
            }
            return false;
        }
    }
}