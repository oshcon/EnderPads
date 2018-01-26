package net.doodcraft.oshcon.bukkit.enderpads.cache;

import net.doodcraft.oshcon.bukkit.enderpads.enderpad.EnderPad;
import net.doodcraft.oshcon.bukkit.enderpads.enderpad.SmallLocation;
import org.bukkit.Location;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EnderPadCache {

    private static Map<String, EnderPad> pads = new ConcurrentHashMap<>();

    public static void cache(EnderPad pad) {
        pads.put(pad.getSmallLocation().toString(), pad);
    }

    public static void uncache(EnderPad pad) {
        if (isCached(pad.getSmallLocation())) {
            pads.remove(pad.getSmallLocation().toString());
        }
    }

    public static EnderPad getEnderPad(SmallLocation loc) {
        return pads.get(loc.toString());
    }

    public static EnderPad getEnderPad(Location loc) {
        return getEnderPad(new SmallLocation(loc));
    }

    public static boolean isCached(SmallLocation loc) {
        return pads.containsKey(loc.toString());
    }
}