package net.doodcraft.oshcon.bukkit.enderpads.enderpad;

import net.doodcraft.oshcon.bukkit.enderpads.PadsPlugin;
import net.doodcraft.oshcon.bukkit.enderpads.config.Settings;
import net.doodcraft.oshcon.bukkit.enderpads.util.BlockHelper;
import net.doodcraft.oshcon.bukkit.enderpads.util.NumberConverter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class EnderPadMethods {

    // This will attempt to verify and padCache all stored EnderPads.
    public static void verifyAll() {
        ArrayList<EnderPad> pads = PadsPlugin.database.getStoredPads();
        if (pads.size() > 0) {
            PadsPlugin.logger.log("Reading and caching all EnderPads from the database..");
            int count = 0;
            for (EnderPad pad : pads) {
                if (pad.verify()) {
                    count++;
                }
            }
            if (count > 0) {
                PadsPlugin.logger.log("Cached " + NumberConverter.convert(count) + " EnderPads!");
            }
        }
    }

    // todo: Not the most graceful way.
    public static EnderPad getPadFromString(String id) {
        String[] p = id.split("-");
        return PadsPlugin.padCache.getEnderPad(new SmallLocation(p[0], Double.valueOf(p[1]), Double.valueOf(p[2]), Double.valueOf(p[3])));
    }

    public static EnderPad getPadFromLocation(Location loc) {
        SmallLocation smallLoc = new SmallLocation(loc);
        if (PadsPlugin.padCache.isCached(smallLoc)) {
            return PadsPlugin.padCache.getEnderPad(loc);
        } else {
            return new EnderPad(smallLoc);
        }
    }

    // This will generate an EnderPad friendly string with the block's getData attached.
    public static String getBlockString(Block block) {
        return BlockHelper.fixDual(block) + "~" + BlockHelper.fixVariant(block);
    }

    // Run this when creating/placing a block. Set save to false to not actually save the EnderPad
    public static void saveCheck(Player player, Block block, boolean save) {
        // Player placed a pressure plate. Check the block below, now.
        if (isPlate(block.getType())) {
            PadsPlugin.logger.debug("Player placed pressure plate.");
            if (block.getRelative(BlockFace.DOWN).getType().equals(Material.valueOf(Settings.centerMaterial.split("~")[0]))) {
                PadsPlugin.logger.debug("Pressure plate has valid center material.");
                EnderPad p = new EnderPad(new SmallLocation(block.getRelative(BlockFace.DOWN).getLocation()), player.getUniqueId());
                if (p.isValid()) {
                    PadsPlugin.logger.debug("EnderPad object is valid. Check returns true.");
                    if (save) {
                        p.save(player);
                    }
                    return;
                }
            }
        }

        // Check neighboring blocks to placed block for center material.
        ArrayList<Block> faces = fetchFaces(block);
        if (faces.size() >= 1) {
            for (Block b : faces) {
                checkFace(player, b, save, false);
            }
        }

        PadsPlugin.logger.debug("Block did not contain an EnderPad.");
    }

    public static void checkFace(Player player, Block block, boolean save, boolean delete) {
        if (block.getType().equals(Material.valueOf(Settings.centerMaterial.split("~")[0]))) {
            PadsPlugin.logger.debug("Center material detected neighboring a block place.");
            if (isPlate(block.getRelative(BlockFace.UP).getType())) {
                PadsPlugin.logger.debug("Detected center material has a pressure plate.");
                EnderPad p = new EnderPad(new SmallLocation(block.getLocation()), player.getUniqueId());
                if (p.isValid()) {
                    PadsPlugin.logger.debug("EnderPad is valid. Check returns true.");
                    if (save) {
                        p.save(player);
                    }
                    if (delete) {
                        p.delete(player);
                    }
                }
            }
        }
    }

    // Run this when breaking/changing a block. Set delete to false to not actually delete the EnderPad.
    public static void deleteCheck(Player player, Block block, boolean delete) {
        // A pressure plate was broken. Check the block below, now.
        if (isPlate(block.getType())) {
            PadsPlugin.logger.debug("Player broke pressure plate.");
            if (block.getRelative(BlockFace.DOWN).getType().equals(Material.valueOf(Settings.centerMaterial.split("~")[0]))) {
                PadsPlugin.logger.debug("Pressure plate has valid center material.");
                EnderPad p = new EnderPad(new SmallLocation(block.getRelative(BlockFace.DOWN).getLocation()), player.getUniqueId());
                if (p.isValid()) {
                    PadsPlugin.logger.debug("EnderPad object is valid. Check returns true.");
                    if (delete) {
                        p.delete(player);
                    }
                    return;
                }
            }
        }
        // A center material block was broken. Check above for pressure plate.
        if (block.getType().equals(Material.valueOf(Settings.centerMaterial.split("~")[0]))) {
            if (isPlate(block.getRelative(BlockFace.UP).getType())) {
                EnderPad p = new EnderPad(new SmallLocation(block.getLocation()), player.getUniqueId());
                if (p.isValid()) {
                    PadsPlugin.logger.debug("EnderPad object is valid. Check returns true.");
                    if (delete) {
                        p.delete(player);
                    }
                    return;
                }
            }
        }
        // Check neighboring blocks to placed block for center material.
        ArrayList<Block> faces = fetchFaces(block);
        if (faces.size() >= 1) {
            for (Block b : faces) {
                checkFace(player, b, false, delete);
            }
        }
        PadsPlugin.logger.debug("Block did not contain an EnderPad.");
    }

    // This will check the faces of a specified block, then return an array of blocks suspected of being EnderPads.
    public static ArrayList<Block> fetchFaces(Block block) {
        ArrayList<Block> blocks = new ArrayList<>();
        for (BlockFace face : PadsPlugin.faces) {
            Block b = block.getRelative(face);
            if (b.getType().toString().equals(Settings.centerMaterial.split("~")[0])) {
               if (isPlate(b.getRelative(BlockFace.UP).getType())) {
                   blocks.add(b);
               }
            }
        }

        return blocks;
    }

    public static boolean isPlate(Material material) {
        return material.equals(Material.STONE_PLATE) ||
                material.equals(Material.WOOD_PLATE) ||
                material.equals(Material.IRON_PLATE) ||
                material.equals(Material.GOLD_PLATE);
    }
}