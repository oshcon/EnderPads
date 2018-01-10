package net.doodcraft.oshcon.bukkit.enderpads.util;

import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.UUID;

public class NameCache {

    public static HashMap<UUID, String> names = new HashMap<>();

    public static String getUsername(UUID uuid) {
        if (!names.containsKey(uuid)) {
            // uuid is not cached, lets get the username and cache it
            names.put(uuid, Bukkit.getOfflinePlayer(uuid).getName());
        }

        if (names.get(uuid) == null) {
            return "INVALID";
        } else {
            return names.get(uuid);
        }
    }
}