package net.doodcraft.oshcon.bukkit.enderpads.cache;

import net.doodcraft.oshcon.bukkit.enderpads.enderpad.EnderPad;
import net.doodcraft.oshcon.bukkit.enderpads.enderpad.Link;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LinkCache {

    private static Map<Link, ArrayList> links = new ConcurrentHashMap<>();

    public static List getLinks(Link link) {
        return links.get(link);
    }

    public static void cacheLink(EnderPad pad) {
        Link current = pad.getCurrentLink();
        if (links.containsKey(current)) {
            ArrayList l = links.get(current);
            l.add(pad);
            links.put(current, l);
            return;
        }
        ArrayList<EnderPad> l = new ArrayList<>();
        l.add(pad);
        links.put(current, l);
    }

    public static void uncacheLink(EnderPad pad) {
        Link current = pad.getCurrentLink();
        if (links.containsKey(current)) {
            links.get(current).remove(pad);
        }
    }
}