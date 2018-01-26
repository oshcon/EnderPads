package net.doodcraft.oshcon.bukkit.enderpads.cache;

import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.UUID;

public class UUIDCache {

    private static HashMap<String, UUID> uuids = new HashMap<>();

    public static UUID getUniqueID(String name) {

        if (!uuids.containsKey(name)) {
            uuids.put(name, Bukkit.getOfflinePlayer(name).getUniqueId());
        }

        if (uuids.get(name) == null) {

            return Bukkit.getOfflinePlayer(name).getUniqueId();
        } else {

            return uuids.get(name);
        }
    }
}