package net.doodcraft.oshcon.bukkit.enderpads.api;

import net.doodcraft.oshcon.bukkit.enderpads.EnderPadsPlugin;
import net.doodcraft.oshcon.bukkit.enderpads.config.Configuration;
import net.doodcraft.oshcon.bukkit.enderpads.config.Settings;
import net.doodcraft.oshcon.bukkit.enderpads.util.StaticMethods;
import net.doodcraft.oshcon.bukkit.enderpads.util.StringParser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EnderPad {

    private String padId;
    private String linkId;
    private UUID ownerUUID;
    private Location padLocation;
    private Block centerBlock;
    private Block northBlock;
    private Block eastBlock;
    private Block southBlock;
    private Block westBlock;

    public EnderPad(Location location) {
        setLocation(location);
        setPadId();

        if (isSaved()) {
            Configuration pads = new Configuration(EnderPadsPlugin.plugin.getDataFolder() + File.separator + "data" + File.separator + "pads.yml");
            ownerUUID = UUID.fromString(pads.getString(getPadId() + ".Owner.UUID"));
        }

        setAllSides(location);
    }

    public EnderPad(Location location, Player player) {
        setLocation(location);
        setPadId();
        setOwnerUUID(player);
        setAllSides(location);
    }

    public void setPadId() {
        padId = String.valueOf(padLocation.getWorld().getName() + " " + ((int) padLocation.getX()) + " " + (int) padLocation.getY() + " " + (int) padLocation.getZ());
    }

    public void setLinkId(String id) {
        linkId = id;
    }

    public void setLinkId(Block blocks[]) {
        List<String> blockNames = new ArrayList<>();

        for (Block block : blocks) {
            blockNames.add(EnderPadAPI.getBlockString(block));
        }

        blockNames.sort(Collator.getInstance());
        linkId = String.join("-", blockNames);
    }

    public void setOwnerUUID(Player player) {
        ownerUUID = player.getUniqueId();
    }

    public void setLocation(Location location) {
        padLocation = location;
    }

    public void setCenterBlock(Block block) {
        centerBlock = block;
    }

    public void setNorthBlock(Block block) {
        northBlock = block;
    }

    public void setEastBlock(Block block) {
        eastBlock = block;
    }

    public void setSouthBlock(Block block) {
        southBlock = block;
    }

    public void setWestBlock(Block block) {
        westBlock = block;
    }

    public void setAllSides(Location location) {
        Block block = location.getBlock();
        setCenterBlock(block);

        for (BlockFace face : EnderPadsPlugin.faces) {
            Block sideBlock = getCenterBlock().getRelative(face);
            if (!sideBlock.isEmpty() && !sideBlock.isLiquid()) {
                Material material = sideBlock.getType();

                if (Settings.blackListedBlocks.contains(material.toString().toUpperCase())) {
                    break;
                }

                if (!material.isBlock() || !material.isSolid() || !material.isOccluding() || material.isTransparent()) {
                    break;
                }

                if (face.equals(BlockFace.NORTH)) {
                    setNorthBlock(sideBlock);
                }

                if (face.equals(BlockFace.EAST)) {
                    setEastBlock(sideBlock);
                }

                if (face.equals(BlockFace.SOUTH)) {
                    setSouthBlock(sideBlock);
                }

                if (face.equals(BlockFace.WEST)) {
                    setWestBlock(sideBlock);
                }
            } else {
                break;
            }
        }
    }

    public String getPadId() {
        return padId;
    }

    public String getLinkId() {
        return linkId;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public Location getLocation() {
        return padLocation;
    }

    public Block getCenterBlock() {
        return centerBlock;
    }

    public Block getNorthBlock() {
        return northBlock;
    }

    public Block getEastBlock() {
        return eastBlock;
    }

    public Block getSouthBlock() {
        return southBlock;
    }

    public Block getWestBlock() {
        return westBlock;
    }

    public String getOwnerName() {
        return Bukkit.getOfflinePlayer(this.ownerUUID).getName();
    }

    public List<EnderPad> getLinkedPads() {
        Configuration linkedPads = new Configuration(EnderPadsPlugin.plugin.getDataFolder() + File.separator + "data" + File.separator + "linked.yml");
        List<String> list = linkedPads.getStringList(padId);
        List<EnderPad> links = new ArrayList<>();

        for (String s : list) {
            links.add(EnderPadAPI.getPadFromID(s));
        }

        return links;
    }

    public Block[] getSideBlocks() {
        return new Block[]{northBlock, eastBlock, southBlock, westBlock};
    }

    public boolean isValid() {
        Block blocks[] = getSideBlocks();

        for (Block block : blocks) {
            if (block == null) {
                return false;
            }
        }

        setLinkId(blocks);

        if (padId == null) {
            return false;
        }

        if (ownerUUID == null) {
            return false;
        }

        if (padLocation == null) {
            return false;
        }

        if (centerBlock == null) {
            return false;
        }

        if (!EnderPadAPI.isValidPlate(centerBlock.getRelative(BlockFace.UP).getType())) {
            return false;
        }

        String check = EnderPadAPI.getBlockString(getCenterBlock());
        String valid = Settings.centerMaterial.toUpperCase();

        if (!check.equals(valid)) {
            return false;
        }

        return linkId != null;
    }

    public boolean isSaved() {
        Configuration pads = new Configuration(EnderPadsPlugin.plugin.getDataFolder() + File.separator + "data" + File.separator + "pads.yml");
        return pads.contains(padId);
    }

    public void save() {
        if (ownerUUID != null) {
            Configuration pads = new Configuration(EnderPadsPlugin.plugin.getDataFolder() + File.separator + "data" + File.separator + "pads.yml");
            Configuration linkedPads = new Configuration(EnderPadsPlugin.plugin.getDataFolder() + File.separator + "data" + File.separator + "linked.yml");
            Configuration players = new Configuration(EnderPadsPlugin.plugin.getDataFolder() + File.separator + "data" + File.separator + "players.yml");

            List<String> owned = players.getStringList(getOwnerUUID().toString());

            if (Bukkit.getPlayer(ownerUUID) != null) {
                EnderPadCreateEvent createEvent = new EnderPadCreateEvent(this, Bukkit.getPlayer(ownerUUID));
                Bukkit.getServer().getPluginManager().callEvent(createEvent);

                if (!createEvent.isCancelled()) {
                    if (owned.size() + 1 > EnderPadAPI.getMaxPads(Bukkit.getPlayer(ownerUUID))) {
                        Bukkit.getPlayer(ownerUUID).sendMessage(StringParser.parse(Settings.atMaximum, null, null, null, false, false));
                        return;
                    }
                    if (owned.isEmpty()) {
                        owned.add(getPadId());
                        players.add(getOwnerUUID().toString(), owned);
                        players.save();
                    } else {
                        owned.add(getPadId());
                        players.remove(getOwnerUUID().toString());
                        players.add(getOwnerUUID().toString(), owned);
                        players.save();
                    }

                    pads.add(getPadId() + ".LinkId", getLinkId());
                    pads.add(getPadId() + ".Center.Location", getLocation().getWorld().getName() + ", " + (getLocation().getX() + 1) + ", " + getLocation().getY() + ", " + getLocation().getZ());
                    pads.add(getPadId() + ".Center.Block", EnderPadAPI.getBlockString(getCenterBlock()));
                    pads.add(getPadId() + ".Owner.UUID", String.valueOf(getOwnerUUID()));
                    pads.add(getPadId() + ".Faces.North", EnderPadAPI.getBlockString(getNorthBlock()));
                    pads.add(getPadId() + ".Faces.East", EnderPadAPI.getBlockString(getEastBlock()));
                    pads.add(getPadId() + ".Faces.South", EnderPadAPI.getBlockString(getSouthBlock()));
                    pads.add(getPadId() + ".Faces.West", EnderPadAPI.getBlockString(getWestBlock()));
                    pads.save();

                    List<String> links = linkedPads.getStringList(linkId);

                    if (links.isEmpty()) {
                        links.add(getPadId());
                        linkedPads.add(getLinkId(), links);
                        linkedPads.save();
                    } else {
                        links.add(getPadId());
                        linkedPads.remove(getLinkId());
                        linkedPads.add(getLinkId(), links);
                        linkedPads.save();
                    }

                    Player owner = Bukkit.getPlayer(ownerUUID);

                    if (StaticMethods.hasPermission(owner, "enderpads.alerts", false)) {
                        owner.sendMessage(StringParser.parse(Settings.created, null, this, null, false, false));
                    }

                    if (StaticMethods.hasPermission(owner, "enderpads.seeinfo", false)) {
                        if (!StaticMethods.hasPermission(owner, "enderpads.alerts", false)) {
                            owner.sendMessage(StringParser.parse(Settings.enderPadCreated, null, this, null, false, false));
                        }

                        String max = String.valueOf(EnderPadAPI.getMaxPads(owner));

                        if (max.equals("2147483647")) {
                            max = "&d∞";
                        }

                        List<String> linked = linkedPads.getStringList(linkId);

                        int size = linked.size();

                        if (size <= 0) {
                            size = 1;
                        }

                        if (size == 1) {
                            owner.sendMessage(StringParser.parse(Settings.links, null, this, "none", false, false));
                        } else {
                            owner.sendMessage(StringParser.parse(Settings.links, null, this, String.valueOf(size - 1), true, false));
                        }

                        max = StaticMethods.addColor(max);

                        owner.sendMessage(StringParser.parse(Settings.usage, null, this, String.valueOf(players.getStringList(ownerUUID.toString()).size()), false, false).replaceAll("<max>", max));
                    }

                    if (Settings.logUse || Settings.debug) {
                        StaticMethods.log(owner.getName() + " created an EnderPad: " + this.getPadId());
                    }

                    EnderPadAPI.addTelepadToMemory(this);
                } else {
                    StaticMethods.debug("EnderPadCreateEvent was cancelled.");
                }
            } else {
                StaticMethods.debug("Player was null. Cannot create an EnderPad without a player.");
            }
        }
    }

    public void delete(Player player) {
        EnderPadDestroyEvent destroyEvent = new EnderPadDestroyEvent(this, player);
        Bukkit.getServer().getPluginManager().callEvent(destroyEvent);

        if (isSaved()) {
            Configuration pads = new Configuration(EnderPadsPlugin.plugin.getDataFolder() + File.separator + "data" + File.separator + "pads.yml");
            Configuration linkedPads = new Configuration(EnderPadsPlugin.plugin.getDataFolder() + File.separator + "data" + File.separator + "linked.yml");
            Configuration players = new Configuration(EnderPadsPlugin.plugin.getDataFolder() + File.separator + "data" + File.separator + "players.yml");

            if (linkId == null) {
                for (String key : linkedPads.getKeys(false)) {
                    List<String> linked = linkedPads.getStringList(key);

                    if (linked.size() <= 1) {
                        linkedPads.remove(key);
                        linkedPads.save();
                    } else {
                        linkedPads.remove(key);
                        linked.remove(getPadId());
                        linkedPads.add(key, linked);
                        linkedPads.save();
                    }
                }
            } else {
                try {
                    List<String> linked = linkedPads.getStringList(linkId);

                    if (linked.size() <= 1) {
                        linkedPads.remove(getLinkId());
                        linkedPads.save();
                    } else {
                        linkedPads.remove(getLinkId());
                        linked.remove(getPadId());
                        linkedPads.add(getLinkId(), linked);
                        linkedPads.save();
                    }
                } catch (Exception ex) {
                    if (Settings.debug) {
                        ex.printStackTrace();
                    }
                }
            }

            List<String> owned = players.getStringList(getOwnerUUID().toString());

            if (owned.size() <= 1) {
                players.remove(getOwnerUUID().toString());
                players.save();
            } else {
                owned.remove(getPadId());
                players.remove(getOwnerUUID().toString());
                players.add(getOwnerUUID().toString(), owned);
                players.save();
            }

            if (pads.contains(getPadId())) {
                pads.remove(getPadId());
                pads.save();
            }

            OfflinePlayer owner = Bukkit.getOfflinePlayer(this.getOwnerUUID());

            if (owner != null) {
                if (owner.isOnline()) {
                    Player onlineOwner = (Player) owner;
                    if (player != null) {
                        if (player.equals(onlineOwner)) {
                            if (StaticMethods.hasPermission(player, "enderpads.alerts", false)) {
                                player.sendMessage(StringParser.parse(Settings.destroyed, null, this, null, false, false));
                            }
                        } else {
                            if (StaticMethods.hasPermission(onlineOwner, "enderpads.alerts", false)) {
                                onlineOwner.sendMessage(StringParser.parse(Settings.destroyedPlayer, null, this, null, false, false));
                            }
                        }
                    } else {
                        if (StaticMethods.hasPermission(onlineOwner, "enderpads.alerts", false)) {
                            onlineOwner.sendMessage(StringParser.parse(Settings.destroyedMisc, null, this, null, false, false));
                        }
                    }

                    if (StaticMethods.hasPermission((Player) owner, "enderpads.seeinfo", false)) {
                        String max = String.valueOf(EnderPadAPI.getMaxPads((Player) owner));

                        if (max.equals("2147483647")) {
                            max = "&d∞";
                        }

                        List<String> linked = linkedPads.getStringList(linkId);

                        int size = linked.size();

                        if (size <= 0) {
                            size = 1;
                        }

                        if (!StaticMethods.hasPermission(onlineOwner, "enderpads.alerts", false)) {
                            onlineOwner.sendMessage(StringParser.parse(Settings.enderPadRemoved, null, this, null, false, false));
                        }

                        if (size == 1) {
                            onlineOwner.sendMessage(StringParser.parse(Settings.links, null, this, "none", false, false));
                        } else {
                            onlineOwner.sendMessage(StringParser.parse(Settings.links, null, this, String.valueOf(size - 1), true, false));
                        }

                        max = StaticMethods.addColor(max);
                        onlineOwner.sendMessage(StringParser.parse(Settings.usage, null, this, String.valueOf(players.getStringList(ownerUUID.toString()).size()), false, false).replaceAll("<max>", max));
                    }
                }
            }

            if (Settings.logUse || Settings.debug) {
                if (player != null) {
                    StaticMethods.log(player.getName() + " destroyed an EnderPad: " + this.getPadId());
                } else {
                    StaticMethods.log("An EnderPad was destroyed: " + this.getPadId());
                }
            }

            EnderPadAPI.removeTelepadFromMemory(this);
        }
    }
}