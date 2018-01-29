package net.doodcraft.oshcon.bukkit.enderpads.cache;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UUIDCache {

    private Map<String, UUID> uuids;

    public UUIDCache() {
        this.uuids = new ConcurrentHashMap<>();
    }

    @SuppressWarnings("deprecation")
    public UUID getUniqueID(String name) {
        if (!uuids.containsKey(name)) {
            Player player = Bukkit.getPlayer(name);
            if (player != null) {
                uuids.put(name, player.getUniqueId());
            } else {
                uuids.put(name, Bukkit.getOfflinePlayer(name).getUniqueId());
            }
        }
        return uuids.get(name);
    }
}