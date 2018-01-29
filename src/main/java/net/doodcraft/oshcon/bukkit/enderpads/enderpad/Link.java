package net.doodcraft.oshcon.bukkit.enderpads.enderpad;

import net.doodcraft.oshcon.bukkit.enderpads.PadsPlugin;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.*;

public class Link {

    private static Map<Link, String> cachedStrings = new HashMap<>();

    private String north;
    private String east;
    private String south;
    private String west;

    public Link(SmallLocation smallLocation) {
        Block centerBlock = smallLocation.getBukkitLocation().getBlock();

        if (centerBlock != null) {
            for (BlockFace f : PadsPlugin.faces) {
                String b = EnderPadMethods.getBlockString(centerBlock.getRelative(f));
                switch (f) {
                    case NORTH:
                        this.north = b;
                        break;
                    case EAST:
                        this.east = b;
                        break;
                    case SOUTH:
                        this.south = b;
                        break;
                    case WEST:
                        this.west = b;
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public String toString() {
        if (cachedStrings.containsKey(this)) {
            return cachedStrings.get(this);
        }
        List<String> blocks = new ArrayList<>();
        blocks.add(north);
        blocks.add(east);
        blocks.add(south);
        blocks.add(west);
        Collections.sort(blocks);
        String l = String.join("-", blocks);
        cachedStrings.put(this, l);
        return l;
    }
}