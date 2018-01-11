package net.doodcraft.oshcon.bukkit.enderpads.api;

import net.doodcraft.oshcon.bukkit.enderpads.EnderPadsPlugin;
import net.doodcraft.oshcon.bukkit.enderpads.config.Configuration;
import net.doodcraft.oshcon.bukkit.enderpads.config.Settings;
import net.doodcraft.oshcon.bukkit.enderpads.util.BlockHelper;
import net.doodcraft.oshcon.bukkit.enderpads.util.StaticMethods;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class EnderPadAPI {

    public static Boolean isValidPlate(Material material) {
        return material.equals(Material.STONE_PLATE)
                || material.equals(Material.WOOD_PLATE)
                || material.equals(Material.GOLD_PLATE)
                || material.equals(Material.IRON_PLATE);
    }

    public static void teleportEntity(EnderPad enderPad, Entity entity) {

        if (entity instanceof Player) {
            if (!StaticMethods.hasPermission((Player) entity, "enderpads.use", true)) {
                return;
            }
        }

        for (String name : Settings.blackListedWorlds) {
            if (name.toLowerCase().equals(enderPad.getLocation().getWorld().getName().toLowerCase())) {
                return;
            }
        }

        Configuration pads = new Configuration(EnderPadsPlugin.plugin.getDataFolder() + File.separator + "data" + File.separator + "pads.yml");

        if (pads.contains(enderPad.getPadId())) {
            String linkedId = pads.getString(enderPad.getPadId() + ".LinkId");

            Configuration linkedPads = new Configuration(EnderPadsPlugin.plugin.getDataFolder() + File.separator + "data" + File.separator + "linked.yml");

            List<String> list = linkedPads.getStringList(linkedId);
            list.remove(enderPad.getPadId());

            if (list.size() >= 1) {
                ArrayList<Location> locations = new ArrayList<>();

                for (String loc : list) {
                    String[] coords = loc.split(" ");
                    String world = String.valueOf(coords[0]);
                    double x = Double.valueOf(coords[1]);
                    double y = Double.valueOf(coords[2]);
                    double z = Double.valueOf(coords[3]);
                    Location location = new Location(Bukkit.getWorld(world), x, y, z);

                    if (Settings.safeTeleport) {
                        Block block = location.getWorld().getBlockAt(((int) location.getX()), ((int) location.getY()) + 2, ((int) location.getZ()));

                        if (!block.isEmpty()) {
                            if (!block.getType().isSolid()) {
                                locations.add(location);
                            }
                        } else {
                            locations.add(location);
                        }
                    } else {
                        locations.add(location);
                    }
                }

                if (locations.size() > 0) {
                    Location random = locations.get(EnderPadsPlugin.random.nextInt(locations.size()));
                    EnderPad dest = new EnderPad(random);

                    for (String name : Settings.blackListedWorlds) {
                        if (name.toLowerCase().equals(dest.getLocation().getWorld().getName().toLowerCase())) {
                            return;
                        }
                    }

                    EnderPadUseEvent useEvent = new EnderPadUseEvent(enderPad, dest, entity);
                    Bukkit.getPluginManager().callEvent(useEvent);
                }
            }
        }
    }

    public static String getLocString(Location loc) {
        return loc.getWorld().getName() + ", " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ();
    }

    public static void runTelepadCheck(Block block, Player player, Boolean create) {
        for (String name : Settings.blackListedWorlds) {
            if (name.toLowerCase().equals(block.getLocation().getWorld().getName().toLowerCase())) {
                return;
            }
        }

        if (player != null) {
            EnderPad enderPad = new EnderPad(block.getLocation(), player);

            if (create) {
                if (enderPad.isValid()) {
                    enderPad.save();
                }
            } else {
                if (enderPad.isValid()) {
                    EnderPad t = getPadFromLocation(enderPad.getLocation());

                    if (t != null) {
                        if (t.isValid()) {
                            t.delete(player);
                        }
                    }
                }
            }
        } else {
            runTelepadCheck(block, create);
        }
    }

    public static void runTelepadCheck(Block block, Boolean create) {
        EnderPad enderPad = new EnderPad(block.getLocation());

        if (!create) {
            if (enderPad.isValid()) {
                enderPad.delete(null);
            }
        } else {
            if (enderPad.isValid()) {
                enderPad.save();
            }
        }
    }

    public static void destroyCheck(Block block, Player player) {
        Material material = block.getType();

        runTelepadCheck(block, player, false);

        if (isValidPlate(material)) {
            Block below = block.getRelative(BlockFace.DOWN);
            String check = getBlockString(below);
            String valid = Settings.centerMaterial.toUpperCase();
            if (check.equals(valid)) {
                runTelepadCheck(below, player, false);
                return;
            }
        }

        for (BlockFace face : EnderPadsPlugin.faces) {
            Block b = block.getRelative(face);
            String check = getBlockString(b);
            String valid = Settings.centerMaterial.toUpperCase();

            if (check.equals(valid)) {
                runTelepadCheck(b, player, false);
            }
        }
    }

    public static EnderPad getPadFromLocation(Location location) {
        Block block = location.getBlock();

        if (!block.isEmpty()) {
            Material type = block.getType();

            if (isValidPlate(type)) {
                Block center = block.getRelative(BlockFace.DOWN);
                return getPadFromBlock(center);
            } else {
                EnderPad pad = getPadFromBlock(block);

                if (pad == null) {
                    for (BlockFace face : EnderPadsPlugin.faces) {
                        Block b = block.getRelative(face);
                        EnderPad facePad = getPadFromBlock(b);
                        if (facePad != null) {
                            return facePad;
                        }
                    }
                } else {
                    return pad;
                }
            }
        }

        return null;
    }

    public static EnderPad getPadFromID(String id) {
        return EnderPadsPlugin.enderPads.get(id);
    }

    public static EnderPad getPadFromBlock(Block block) {
        String check = EnderPadAPI.getBlockString(block);
        String valid = Settings.centerMaterial.toUpperCase();

        if (check.equals(valid)) {
            EnderPad enderPad = new EnderPad(block.getLocation());

            if (enderPad.isValid() && enderPad.isSaved()) {
                return enderPad;
            } else {
                return null;
            }
        }

        return null;
    }

    public static String getBlockString(Block block) {
        return BlockHelper.fixDual(block) + "~" + BlockHelper.fixVariant(block);
    }

    public static int getMaxPads(Player player) {
        if (StaticMethods.hasPermission(player, "enderpads.use", false)) {
            if (player.isOp()) {
                return 2147483647;
            }

            if (player.hasPermission("enderpads.*")) {
                return 2147483647;
            }

            boolean hasNode = false;

            Set<PermissionAttachmentInfo> perms = player.getEffectivePermissions();

            ArrayList<Integer> possibleValues = new ArrayList<>();
            possibleValues.add(Settings.defaultMax);

            for (PermissionAttachmentInfo perm : perms) {
                String permission = perm.getPermission();

                if (permission.toLowerCase().startsWith("enderpads.max.")) {
                    hasNode = true;
                    String args[] = permission.split("\\.");

                    if (permission.toLowerCase().equals("enderpads.max.*")) {
                        return 2147483647;
                    }

                    try {
                        possibleValues.add(Integer.valueOf(args[2]));
                    } catch (Exception ex) {
                        StaticMethods.debug("&eDiscovered an invalid permission node for &b" + player.getName() + "&e: &c" + permission);
                        possibleValues.add(Settings.defaultMax);
                    }
                }
            }

            if (hasNode) {
                return Collections.max(possibleValues);
            } else {
                if (Settings.defaultMax == 0) {
                    return 0;
                }

                if (Settings.defaultMax < 0) {
                    return 2147483647;
                }

                return Settings.defaultMax;
            }
        } else {
            return 0;
        }
    }

    public static void addTelepadToMemory(EnderPad enderPad) {
        if (!EnderPadsPlugin.enderPads.containsKey(enderPad.getPadId())) {
            EnderPadsPlugin.enderPads.put(enderPad.getPadId(), enderPad);

            AddToMemoryEvent event = new AddToMemoryEvent(enderPad);
            Bukkit.getPluginManager().callEvent(event);
        }
    }

    public static void removeTelepadFromMemory(EnderPad enderPad) {
        EnderPad actualPad = new EnderPad(enderPad.getLocation());

        if (EnderPadsPlugin.enderPads.containsKey(actualPad.getPadId())) {
            EnderPadsPlugin.enderPads.remove(actualPad.getPadId());

            RemoveFromMemoryEvent event = new RemoveFromMemoryEvent(actualPad);
            Bukkit.getPluginManager().callEvent(event);
        }
    }

    public static boolean verify(EnderPad enderPad, Configuration pads) {
        Block block = enderPad.getLocation().getBlock();

        for (String name : Settings.blackListedWorlds) {
            if (name.toLowerCase().equals(enderPad.getLocation().getWorld().getName().toLowerCase())) {
                return false;
            }
        }

        if (!enderPad.isValid()) {
            return false;
        }

        // Compare all the current blocks with those stored in the configuration.
        String bCenter = getBlockString(block);
        String bNorth = getBlockString(block.getRelative(BlockFace.NORTH));
        String bEast = getBlockString(block.getRelative(BlockFace.EAST));
        String bSouth = getBlockString(block.getRelative(BlockFace.SOUTH));
        String bWest = getBlockString(block.getRelative(BlockFace.WEST));

        if (!bCenter.equals(pads.getString(enderPad.getPadId() + ".Center.Block"))) {
            return false;
        }

        if (!bNorth.equals(pads.getString(enderPad.getPadId() + ".Faces.North"))) {
            return false;
        }

        if (!bEast.equals(pads.getString(enderPad.getPadId() + ".Faces.East"))) {
            return false;
        }

        if (!bSouth.equals(pads.getString(enderPad.getPadId() + ".Faces.South"))) {
            return false;
        }

        return bWest.equals(pads.getString(enderPad.getPadId() + ".Faces.West"));
    }

    public static void verifyAllTelepads() {
        long start = System.currentTimeMillis();

        StaticMethods.log("&bVerifying and caching all EnderPads..");

        final Configuration pads = new Configuration(EnderPadsPlugin.plugin.getDataFolder() + File.separator + "data" + File.separator + "pads.yml");

        Bukkit.getServer().getScheduler().runTask(EnderPadsPlugin.plugin, new Runnable() {
            @Override
            public void run() {
                for (String padId : pads.getKeys(false)) {

                    String args[] = padId.split(" ");

                    World world = Bukkit.getWorld(args[0]);

                    // If the world no longer exists, skip the check.
                    if (world == null) {
                        return;
                    }

                    double x = Integer.valueOf(args[1]);
                    double y = Integer.valueOf(args[2]);
                    double z = Integer.valueOf(args[3]);

                    Location padLocation = new Location(world, x, y, z);

                    EnderPad enderPad = new EnderPad(padLocation);

                    if (verify(enderPad, pads)) {
                        addTelepadToMemory(enderPad);
                    } else {
                        StaticMethods.log("&cDiscovered and removed an invalid EnderPad: &e" + padId);
                        enderPad.delete(null);
                        removeTelepadFromMemory(enderPad);
                    }
                }
            }
        });

        long finish = System.currentTimeMillis();

        if (pads.getKeys(false).size() == 1) {
            StaticMethods.log("&bVerified and cached " + pads.getKeys(false).size() + " EnderPad. &e(" + (finish - start) + "ms)");
        } else {
            StaticMethods.log("&bVerified and cached " + pads.getKeys(false).size() + " EnderPads. &e(" + (finish - start) + "ms)");
        }
    }
}