package net.doodcraft.oshcon.bukkit.enderpads.enderpad;

import net.doodcraft.oshcon.bukkit.enderpads.EnderPadsPlugin;
import net.doodcraft.oshcon.bukkit.enderpads.cache.EnderPadCache;
import net.doodcraft.oshcon.bukkit.enderpads.cache.LinkCache;
import net.doodcraft.oshcon.bukkit.enderpads.cache.NameCache;
import net.doodcraft.oshcon.bukkit.enderpads.cache.PermissionCache;
import net.doodcraft.oshcon.bukkit.enderpads.config.Settings;
import net.doodcraft.oshcon.bukkit.enderpads.event.EnderPadCacheEvent;
import net.doodcraft.oshcon.bukkit.enderpads.event.EnderPadUseEvent;
import net.doodcraft.oshcon.bukkit.enderpads.util.BlockHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EnderPad {

    private SmallLocation smallLocation; // SmallLocation#toString() is the new unique identifier. No two pads can occupy the same location.
    private Link link;
    private UUID owner;

    public EnderPad(Location location) {
        setSmallLocation(new SmallLocation(location));
        setCurrentLink();
        setOwnerUUID();

        EnderPadsPlugin.logger.debug("Created new object [" + this.getSmallLocation().toString() + "] by: location");
    }

    public EnderPad(SmallLocation smallLocation) {
        setSmallLocation(smallLocation);
        setCurrentLink();
        setOwnerUUID();

        EnderPadsPlugin.logger.debug("Created new object [" + this.getSmallLocation().toString() + "] by: smallLocation");

    }

    public EnderPad(Location location, UUID uuid) {
        setSmallLocation(new SmallLocation(location));
        setCurrentLink();
        setOwnerUUID(uuid);

        EnderPadsPlugin.logger.debug("Created new object [" + this.getSmallLocation().toString() + "] by: location, uuid");
    }

    public EnderPad(SmallLocation smallLocation, UUID uuid) {
        setSmallLocation(smallLocation);
        setCurrentLink();
        setOwnerUUID(uuid);

        EnderPadsPlugin.logger.debug("Created new object [" + this.getSmallLocation().toString() + "] by: smallLocation, uuid");
    }

    public SmallLocation getSmallLocation() {
        return this.smallLocation;
    }

    public void setSmallLocation(Location location) {
        this.smallLocation = new SmallLocation(location);
    }

    public void setSmallLocation(SmallLocation smallLocation) {
        this.smallLocation = smallLocation;
    }

    public Location getBukkitLocation() {
        return this.smallLocation.getBukkitLocation();
    }

    public Link getCurrentLink() {
        return this.link;
    }

    public Link getStoredLink() {
        EnderPad pad = EnderPadCache.getEnderPad(this.smallLocation);
        if (pad != null) {
            return pad.getCurrentLink();
        }
        return null;
    }

    public void setCurrentLink() {
        this.link = new Link(this.getSmallLocation());
    }

    public UUID getOwnerUUID() {
        return this.owner;
    }

    public void setOwnerUUID(UUID uuid) {
        this.owner = uuid;
    }

    public void setOwnerUUID() {
        EnderPad pad = EnderPadCache.getEnderPad(this.smallLocation);
        if (pad != null) {
            this.owner = EnderPadCache.getEnderPad(this.getSmallLocation()).getOwnerUUID();
        }
    }

    public String getOwnerName() {
        return NameCache.getUsername(this.owner);
    }

    public List getLinks() {
        return LinkCache.getLinks(this.link);
    }

    public boolean isCached() {
        // return if the EnderPad is in EnderPad cache
        return EnderPadCache.isCached(this.getSmallLocation());
    }

    public boolean isStored() {
        // return if the EnderPad is in the database
        return EnderPadsPlugin.database.isSaved(this);
    }

    // Come
    public boolean verify() {
        if (!this.isValid()) {
            EnderPadsPlugin.logger.debug("Deleting object [" + this.getSmallLocation().toString() + "]");

            LinkCache.uncacheLink(this);
            EnderPadCache.uncache(this);
            EnderPadsPlugin.database.delete(this);

            EnderPadCacheEvent event = new EnderPadCacheEvent(this, false);
            event.setEffects(false);
            Bukkit.getPluginManager().callEvent(event);
            return false;
        } else {
            if (this.isCached()) {
                EnderPadsPlugin.logger.debug("Ignoring cache for already cached object [" + this.getSmallLocation().toString() + "]");
                return false;
            }

            EnderPadsPlugin.logger.debug("Saving object [" + this.getSmallLocation().toString() + "]");

            EnderPadCache.cache(this);
            LinkCache.cacheLink(this);
            EnderPadsPlugin.database.save(this);

            EnderPadCacheEvent event = new EnderPadCacheEvent(this, true);
            event.setEffects(false);
            Bukkit.getPluginManager().callEvent(event);
            return true;
        }
    }

    // This has a purpose, don't shoot me!
    public boolean isValid() {
        return this.validate();
    }

    public boolean validate() {
        Block center = this.getBukkitLocation().getBlock();
        if (center.getType().toString().equals(Settings.centerMaterial.split("~")[0])) {
            if (EnderPadMethods.isPlate(center.getRelative(BlockFace.UP).getType())) {
                for (BlockFace face : EnderPadsPlugin.faces) {
                    Block b = center.getRelative(face);
                    if (b.isEmpty()) {
                        return false;
                    }
                    if (b.isLiquid()) {
                        return false;
                    }
                    if (b.getType().isTransparent()) {
                        return false;
                    }
                    if (BlockHelper.isPhysicsBlock(b.getType())) {
                        return false;
                    }
                    if (Settings.blackListedBlocks.contains(b.getType().toString())) {
                        return false;
                    }
                }
            }
        }
        return this.owner != null && this.link != null;
    }

    public void save(Player player) {
        if (this.isCached()) {
            EnderPadsPlugin.logger.debug("Ignoring save for already cached object [" + this.getSmallLocation().toString() + "]");
            return;
        }

        EnderPadsPlugin.logger.debug("Saving/caching object [" + this.getSmallLocation().toString() + "]");

        EnderPadCache.cache(this);
        LinkCache.cacheLink(this);
        EnderPadsPlugin.database.save(this);

        EnderPadCacheEvent event = new EnderPadCacheEvent(this, true);
        event.setCreator(player);
        Bukkit.getPluginManager().callEvent(event);
    }

    public void delete(Player player) {
        if (!this.isCached()) {
            EnderPadsPlugin.logger.debug("Ignoring delete for un-cached object [" + this.getSmallLocation().toString() + "]");
            return;
        }

        EnderPadsPlugin.logger.debug("Deleting/un-caching object [" + this.getSmallLocation().toString() + "]");

        LinkCache.uncacheLink(this);
        EnderPadCache.uncache(this);
        EnderPadsPlugin.database.delete(this);

        EnderPadCacheEvent event = new EnderPadCacheEvent(this, false);
        event.setDestroyer(player);
        Bukkit.getPluginManager().callEvent(event);
    }

    // todo: archaic, rewrite soon
    public void teleportEntity(Entity entity) {
        if (entity instanceof Player) {
            if (!PermissionCache.hasPermission((Player) entity, "enderpads.use", true)) {
                return;
            }
        }

        for (String name : Settings.blackListedWorlds) {
            if (name.toLowerCase().equals(this.getBukkitLocation().getWorld().getName().toLowerCase())) {
                return;
            }
        }

        List links = this.getLinks();
        links.remove(this);

        ArrayList<Location> locations = new ArrayList<>();

        for (Object p : links) {
            Location location = ((EnderPad) p).getBukkitLocation().add(0, 1, 0);
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
                if (name.toLowerCase().equals(dest.getBukkitLocation().getWorld().getName().toLowerCase())) {
                    return;
                }
            }

            EnderPadUseEvent useEvent = new EnderPadUseEvent(this, dest, entity);
            Bukkit.getPluginManager().callEvent(useEvent);
        }
    }
}