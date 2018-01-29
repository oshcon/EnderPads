package net.doodcraft.oshcon.bukkit.enderpads.cache;

import net.doodcraft.oshcon.bukkit.enderpads.enderpad.EnderPad;
import net.doodcraft.oshcon.bukkit.enderpads.enderpad.Link;
import net.doodcraft.oshcon.bukkit.enderpads.enderpad.SmallLocation;
import org.bukkit.Location;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EnderPadCache {

    private Map<String, EnderPad> pads;
    private Map<UUID, List<EnderPad>> owners;
    private Map<Link, ArrayList<EnderPad>> links;

    public EnderPadCache() {
        this.pads = new ConcurrentHashMap<>();
        this.owners = new ConcurrentHashMap<>();
        this.links = new ConcurrentHashMap<>();
    }

    public Map<String, EnderPad> getCache() {
        return this.pads;
    }

    public Map<UUID, List<EnderPad>> getOwners() {
        return this.owners;
    }

    public Set<Link> getLinks() {
        return this.links.keySet();
    }

    public ArrayList<EnderPad> getLinks(Link link) {
        return links.get(link);
    }

    public EnderPad getEnderPad(SmallLocation loc) {
        return pads.get(loc.toString());
    }

    public EnderPad getEnderPad(Location loc) {
        return getEnderPad(new SmallLocation(loc));
    }

    public boolean isCached(SmallLocation loc) {
        return pads.containsKey(loc.toString());
    }

    public void cache(EnderPad pad) {
        if (!isCached(pad.getSmallLocation())) {
            pads.put(pad.getSmallLocation().toString(), pad);
            if (owners.containsKey(pad.getOwnerUUID())) {
                List<EnderPad> owned = owners.get(pad.getOwnerUUID());
                owned.add(pad);
                owners.put(pad.getOwnerUUID(), owned);
            } else {
                List<EnderPad> owned = new ArrayList<>();
                owned.add(pad);
                owners.put(pad.getOwnerUUID(), owned);
            }
        }

        Link current = pad.getCurrentLink();
        if (links.containsKey(current)) {
            ArrayList<EnderPad> l = links.get(current);
            l.add(pad);
            links.put(current, l);
            return;
        }
        ArrayList<EnderPad> l = new ArrayList<>();
        l.add(pad);
        links.put(current, l);
    }

    public void uncache(EnderPad pad) {
        Link current = pad.getCurrentLink();
        if (links.containsKey(current)) {
            links.get(current).remove(pad);
        }

        if (isCached(pad.getSmallLocation())) {
            pads.remove(pad.getSmallLocation().toString());
            if (owners.containsKey(pad.getOwnerUUID())) {
                List<EnderPad> owned = owners.get(pad.getOwnerUUID());
                owned.remove(pad);
                owners.put(pad.getOwnerUUID(), owned);
            }
        }
    }
}