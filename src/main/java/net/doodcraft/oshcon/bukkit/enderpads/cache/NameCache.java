package net.doodcraft.oshcon.bukkit.enderpads.cache;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class NameCache {

    private Map<UUID, String> names;

    public NameCache() {
        this.names = new ConcurrentHashMap<>();
    }

    public String getUsername(UUID uuid) {
        if (!names.containsKey(uuid)) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                names.put(uuid, player.getName());
            } else {
                names.put(uuid, Bukkit.getOfflinePlayer(uuid).getName());
            }
        }
        return names.get(uuid);
    }
}