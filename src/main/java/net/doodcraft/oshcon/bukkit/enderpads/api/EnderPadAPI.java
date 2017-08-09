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
    // TODO: Create a method to return a collection of EnderPads based on a non-center block.

    /**
     * Check whether a block is part of an EnderPad.
     * Useful if you want to cancel/stop destruction of an EnderPad.
     * <p>
     * This method will not work at present. Also, I've realized that because of the nature of
     * EnderPads themselves means I cannot accurately create a method to return an EnderPad based
     * on a non-center block. I could however return a Collection of EnderPads. I will implement this
     * in the next update.
     *
     * @param block the block being checked
     * @return true if the block is part of an EnderPad
     * false if it is not
     */
    public static Boolean isEnderPad(Block block) {
        Material material = block.getType();

        if (isValidPlate(material)) {
            Block below = block.getRelative(BlockFace.DOWN);
            if (getPadFromBlock(below) != null) {
                EnderPad pad = getPadFromBlock(below);
                if (pad != null && pad.isValid() && pad.isSaved()) {
                    return true;
                }
            }
        }

        if (isValidCenter(material.toString())) {
            if (getPadFromBlock(block) != null) {
                EnderPad pad = getPadFromBlock(block);
                if (pad != null && pad.isValid() && pad.isSaved()) {
                    return true;
                }
            }
        }

        for (BlockFace face : EnderPadsPlugin.faces) {
            Block b = block.getRelative(face);
            String check = getBlockString(b);
            String valid = Settings.centerMaterial.toUpperCase();

            if (check.equals(valid)) {
                if (getPadFromBlock(b) != null) {
                    EnderPad pad = getPadFromBlock(b);
                    if (pad != null && pad.isValid() && pad.isSaved()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Checks to see if the material in question matches the value provided in the configuration.
     * <p>
     * Will return false if the provided material name does not match.
     *
     * @param string the material name to check
     * @return true if the material name matches
     * false otherwise
     */
    public static boolean isValidCenter(String string) {
        return string.toUpperCase().equals(Settings.centerMaterial.toUpperCase());
    }

    /**
     * Checks if the material provided is a pressure plate.
     * <p>
     * Will return false if the material is not.
     *
     * @param material the material to check
     * @return true if the material matches a pressure plate
     * false otherwise
     */
    public static Boolean isValidPlate(Material material) {
        return material.equals(Material.STONE_PLATE)
                || material.equals(Material.WOOD_PLATE)
                || material.equals(Material.GOLD_PLATE)
                || material.equals(Material.IRON_PLATE);
    }

    /**
     * Attempts to teleport an Entity from an EnderPad.
     * If there are valid destinations, the destination EnderPad will be chosen at random.
     * After choosing a destination EnderPad, this will fire an EnderPadUseEvent.
     * <p>
     * The actual teleportation is then handled internally by the plugin, not via this specific method.
     * Cancelling the event and creating your own listener can modify the result of this method.
     *
     * @param enderPad the EnderPad being used by the Entity
     * @param entity   the Entity using the EnderPad
     */
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

    /**
     * Creates a String used internally by the plugin.
     * This string is used to display a more readable format of a location object in chat or the console.
     *
     * @param loc the location to convert
     * @return the converted location
     */
    public static String getLocString(Location loc) {
        return loc.getWorld().getName() + ", " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ();
    }

    /**
     * Validates whether or not a block is the center of an EnderPad, then saves/removes the EnderPad.
     * Then it will proceed to create a new EnderPad object and check if it is a complete valid EnderPad.
     * If it is a valid EnderPad, this will then attempt to save, or remove the EnderPad object.
     * This method is typically called on BlockPlaceEvents and run for every BlockFace of the placed block/s.
     * It is also automatically called when destroyCheck() is used.
     *
     * @param block  the block in question
     * @param player the player responsible for placing or breaking the block, this can be null
     * @param create if true, the method will attempt to save the EnderPad if valid
     *               if false, the method will attempt to remove the EnderPad if valid
     */
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

    /**
     * This method is automatically called by the other runTelepadCheck() when the Player is null.
     *
     * @param block  the block in question
     * @param create if true, the method will attempt to save the EnderPad if valid
     *               if false, the method will attempt to remove the EnderPad if valid
     */
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

    /**
     * This method should be run whenever a block is broken or changed.
     * It will iterate over each blockface of the block in question and call runTelepadCheck() for each face.
     *
     * @param block  the block being broken
     * @param player the player responsible, can be null
     */
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

    /**
     * Get an EnderPad object based on the supplied location.
     * This method does not validate the EnderPad object for you. It only gets a generic EnderPad object.
     *
     * @param location the Location in question.
     * @return an EnderPad object with the provided location as it's center
     * will return null if the location does not contain a block
     */
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

    /**
     * Converts a provided PadId string to a Location, then calls getPadFromLocation() to return the EnderPad.
     * This will not validate the EnderPad object. It will only return a generic EnderPad object.
     *
     * @param id the PadId in question
     * @return this will return getPadFromLocation()
     * it can return null if the location does not contain a block
     */
    public static EnderPad getPadFromID(String id) {
        String[] coords = id.split(" ");
        String world = String.valueOf(coords[0]);
        double x = Double.valueOf(coords[1]);
        double y = Double.valueOf(coords[2]);
        double z = Double.valueOf(coords[3]);
        Location location = new Location(Bukkit.getWorld(world), x, y, z);
        return getPadFromLocation(location);
    }

    /**
     * Checks if the provided block is the center of an EnderPad, then return it.
     *
     * @param block the Block being checked
     * @return an EnderPad object
     */
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

    /**
     * Converts a Block into a store-able/readable string used by the linking system of the plugin.
     * The block string is used to indicate the LinkId and the blocks surrounding each EnderPad.
     * The string is made of the material name followed by the data byte of the material.
     * The material name and data will then be checked and "fixed" for certain conditions, such as whether or not it is rotatable.
     *
     * @param block the Block being converted
     * @return block string used in an EnderPad's LinkId
     */
    public static String getBlockString(Block block) {
        return BlockHelper.fixDual(block) + "~" + BlockHelper.fixVariant(block);
    }

    /**
     * Attempts to get a players maximum allowed EnderPads.
     * Since I am using Integers, the maximum possible value is 2147483647.
     * Although 2147483647 is treated as infinite in certain messages, you cannot actually have infinite EnderPads.
     *
     * @param player the Player being checked
     * @return the number of EnderPads the Player is allowed to construct
     */
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

    /**
     * Adds an EnderPad object to the plugin's memory. Only add valid EnderPads, or there could be major issues.
     * This is also sometimes referred to as the EnderPad cache. EnderPads in the cache are trusted as complete and valid EnderPads.
     * Having the EnderPads already cached speeds up the teleportation process greatly, reducing CPU usage.
     * // TODO: Currently by default, EnderPads are still being validated on use. I need to add a configuration option to disable this
     * // TODO: and actually use the cache only during use. The drawback will be that broken EnderPads could still potentially be used.
     *
     * @param enderPad the EnderPad to add
     */
    public static void addTelepadToMemory(EnderPad enderPad) {
        if (!EnderPadsPlugin.enderPads.containsKey(enderPad.getPadId())) {
            EnderPadsPlugin.enderPads.put(enderPad.getPadId(), enderPad);

            AddToMemoryEvent event = new AddToMemoryEvent(enderPad);
            Bukkit.getPluginManager().callEvent(event);
        }
    }

    /**
     * Removes an EnderPad from the memory/cache.
     *
     * @param enderPad the EnderPad to remove
     */
    public static void removeTelepadFromMemory(EnderPad enderPad) {
        EnderPad actualPad = new EnderPad(enderPad.getLocation());

        if (EnderPadsPlugin.enderPads.containsKey(actualPad.getPadId())) {
            EnderPadsPlugin.enderPads.remove(actualPad.getPadId());

            RemoveFromMemoryEvent event = new RemoveFromMemoryEvent(actualPad);
            Bukkit.getPluginManager().callEvent(event);
        }
    }

    /**
     * Compares an EnderPad object with it's stored counterpart in the database.
     * Will return false if any of the blocks are different from those originally stored in the database.
     *
     * @param enderPad the original EnderPad object
     * @param pads     the Configuration where EnderPads are stored
     * @return
     */
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

    /**
     * Runs verify() for every EnderPad stored in the database.
     * This can take approx. 1-3ms per EnderPad depending on system specifications and usage.
     */
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
                    double x = Integer.valueOf(args[1]);
                    double y = Integer.valueOf(args[2]);
                    double z = Integer.valueOf(args[3]);
                    Location padLocation = new Location(world, x, y, z);
                    EnderPad enderPad = new EnderPad(padLocation);

                    if (verify(enderPad, pads)) {
                        addTelepadToMemory(enderPad);
                    } else {
                        StaticMethods.log("&cDiscovered and removed an invalid EnderPad: &e" + enderPad.getPadId());
                        enderPad.delete(null);
                        removeTelepadFromMemory(enderPad);
                    }
                }
            }
        });

        long finish = System.currentTimeMillis();

        StaticMethods.log("&bVerified and cached " + pads.getKeys(false).size() + " EnderPads! &e(" + (finish - start) + "ms)");
    }
}