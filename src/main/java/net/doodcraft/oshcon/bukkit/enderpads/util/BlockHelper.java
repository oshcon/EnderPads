package net.doodcraft.oshcon.bukkit.enderpads.util;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class BlockHelper {

    // todo: data values will be removed in 1.13. Find an alternative asap.

    // If true, we ignore the data value for the block.
    private static boolean isDirectional(Material material) {
        for (directionalBlocks value : directionalBlocks.values()) {
            if (material.toString().equals(value.name())) {
                return true;
            }
        }
        return false;
    }

    // If true, we ignore the data value for the block and fix the name.
    private static boolean isDual(Material material) {
        for (dualBlocks value : dualBlocks.values()) {
            if (material.toString().equals(value.name())) {
                return true;
            }
        }
        return false;
    }

    // If true, we need to discriminate the data to keep for the block. Variants should always also be directional.
    // If the block is only a variant (maybe in the future), then we don't need to do anything to it.
    private static boolean isVariant(Material material) {
        for (variantBlocks value : variantBlocks.values()) {
            if (material.toString().equals(value.name())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPhysicsBlock(Material material) {
        return material.hasGravity();
    }

    public static String fixDual(Block block) {
        if (isDual(block.getType())) {
            if (block.getType() == Material.GLOWING_REDSTONE_ORE) {
                return "REDSTONE_ORE";
            }
            if (block.getType() == Material.BURNING_FURNACE) {
                return "FURNACE";
            }
            if (block.getType() == Material.REDSTONE_LAMP_ON) {
                return "REDSTONE_LAMP_OFF";
            }
        }
        return block.getType().toString();
    }

    public static byte fixVariant(Block block) {
        // Check for variant first, then check directional.
        if (isVariant(block.getType())) {
            if (block.getType() == Material.QUARTZ_BLOCK) {
                if (block.getData() == 0) {
                    return 0;
                }
                if (block.getData() == 1) {
                    return 1;
                }
                if (block.getData() >= 2) {
                    return 2;
                }
                return 0;
            }
            if (block.getType() == Material.LOG) {
                return fixLog(block);
            }
            if (block.getType() == Material.LOG_2) {
                return fixLog(block);
            }
            if (block.getType() == Material.HUGE_MUSHROOM_1) {
                return 0;
            }
            if (block.getType() == Material.HUGE_MUSHROOM_2) {
                return 0;
            }
            if (block.getType() == Material.SPONGE) {
                return 0;
            }
        }
        if (isDirectional(block.getType())) {
            return 0;
        }
        return block.getData();
    }

    private static byte fixLog(Block block) {
        byte data = block.getData();
        if (block.getType().equals(Material.LOG)) {
            if (data == 0 || data == 4 || data == 8) {
                return 0;
            }
            if (data == 1 || data == 5 || data == 9) {
                return 1;
            }
            if (data == 2 || data == 6 || data == 10) {
                return 2;
            }
            if (data == 3 || data == 7 || data == 11) {
                return 3;
            }
        }
        if (block.getType().equals(Material.LOG_2)) {
            if (data == 0 || data == 4 || data == 8) {
                return 0;
            }
            if (data == 1 || data == 5 || data == 9) {
                return 1;
            }
        }
        return block.getData();
    }

    // We ignore the data for these blocks because it only represents the direction, with variants as the exception.
    public enum directionalBlocks {
        BONE_BLOCK, // directional
        PUMPKIN, // directional
        JACK_O_LANTERN, // directional
        DISPENSER, // directional
        DROPPER, // directional
        HAY_BLOCK, // directional
        COMMAND, // directional
        OBSERVER, // directional
        STRUCTURE_BLOCK, // directional
        PURPUR_PILLAR, // directional
        WHITE_GLAZED_TERRACOTTA, // directional and variant
        ORANGE_GLAZED_TERRACOTTA, // directional and variant
        MAGENTA_GLAZED_TERRACOTTA, // directional and variant
        LIGHT_BLUE_GLAZED_TERRACOTTA, // directional and variant
        YELLOW_GLAZED_TERRACOTTA, // directional and variant
        LIME_GLAZED_TERRACOTTA, // directional and variant
        PINK_GLAZED_TERRACOTTA, // directional and variant
        GRAY_GLAZED_TERRACOTTA, // directional and variant
        SILVER_GLAZED_TERRACOTTA, // directional and variant
        CYAN_GLAZED_TERRACOTTA, // directional and variant
        PURPLE_GLAZED_TERRACOTTA, // directional and variant
        BLUE_GLAZED_TERRACOTTA, // directional and variant
        BROWN_GLAZED_TERRACOTTA, // directional and variant
        GREEN_GLAZED_TERRACOTTA, // directional and variant
        RED_GLAZED_TERRACOTTA, // directional and variant
        BLACK_GLAZED_TERRACOTTA, // directional and variant
        FURNACE, // directional and dual
        BURNING_FURNACE, // directional and dual
        QUARTZ_BLOCK, // directional and variant
        LOG, // directional and variant
        LOG_2, // directional and variant
        HUGE_MUSHROOM_1, // directional and variant
        HUGE_MUSHROOM_2 // directional and variant
    }

    // These blocks have multiple material names but for the purposes of EnderPads, should be the same.
    public enum dualBlocks {
        GLOWING_REDSTONE_ORE, // dual
        REDSTONE_ORE, // dual
        REDSTONE_LAMP_ON, // dual
        REDSTONE_LAMP_OFF, // dual
        FURNACE, // directional and dual
        BURNING_FURNACE // directional and dual
    }

    // These blocks should be considered different blocks based on their data values for the purposes of EnderPads.
    public enum variantBlocks {
        SPONGE, // variant
        QUARTZ_BLOCK, // directional and variant
        LOG, // directional and variant
        LOG_2, // directional and variant
        HUGE_MUSHROOM_1, // directional and variant
        HUGE_MUSHROOM_2 // directional and variant
    }
}