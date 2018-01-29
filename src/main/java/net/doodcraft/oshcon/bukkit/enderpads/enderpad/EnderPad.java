package net.doodcraft.oshcon.bukkit.enderpads.enderpad;

import com.sun.istack.internal.Nullable;
import net.doodcraft.oshcon.bukkit.enderpads.PadsPlugin;
import net.doodcraft.oshcon.bukkit.enderpads.config.Settings;
import net.doodcraft.oshcon.bukkit.enderpads.event.EnderPadCacheEvent;
import net.doodcraft.oshcon.bukkit.enderpads.event.EnderPadUseEvent;
import net.doodcraft.oshcon.bukkit.enderpads.util.BlockHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class EnderPad {

    private SmallLocation smallLocation; // SmallLocation#toString() is the new unique identifier. No two pads can occupy the same location.
    private Link link;
    private UUID owner;

    public EnderPad(SmallLocation smallLocation) {
        setSmallLocation(smallLocation);
        setCurrentLink();
        setOwnerUUID();

        PadsPlugin.logger.debug("Created new object [" + this.getSmallLocation().toString() + "] by: smallLocation");
    }

    public EnderPad(SmallLocation smallLocation, UUID uuid) {
        setSmallLocation(smallLocation);
        setCurrentLink();
        setOwnerUUID(uuid);

        PadsPlugin.logger.debug("Created new object [" + this.getSmallLocation().toString() + "] by: smallLocation, uuid");
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
        EnderPad pad = PadsPlugin.padCache.getEnderPad(this.smallLocation);
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
        EnderPad pad = PadsPlugin.padCache.getEnderPad(this.smallLocation);
        if (pad != null) {
            this.owner = PadsPlugin.padCache.getEnderPad(this.getSmallLocation()).getOwnerUUID();
        }
    }

    public String getOwnerName() {
        return PadsPlugin.nameCache.getUsername(this.owner);
    }

    public ArrayList<EnderPad> getLinks() {
        ArrayList<EnderPad> linked = PadsPlugin.padCache.getLinks(this.link);
        linked.remove(PadsPlugin.padCache.getEnderPad(this.smallLocation));
        return linked;
    }

    public boolean isCached() {
        // return if the EnderPad is in EnderPad padCache
        return PadsPlugin.padCache.isCached(this.getSmallLocation());
    }

    public boolean isStored() {
        // return if the EnderPad is in the database
        return PadsPlugin.database.isSaved(this);
    }

    public boolean verify() {
        if (!this.isValid()) {
            PadsPlugin.logger.debug("Deleting object [" + this.getSmallLocation().toString() + "]");

            PadsPlugin.padCache.uncache(this);
            PadsPlugin.database.delete(this);

            EnderPadCacheEvent event = new EnderPadCacheEvent(this, false);
            event.setEffects(false);
            Bukkit.getPluginManager().callEvent(event);
            return false;
        } else {
            if (this.isCached()) {
                PadsPlugin.logger.debug("Ignoring padCache for already cached object [" + this.getSmallLocation().toString() + "]");
                return false;
            }

            PadsPlugin.logger.debug("Saving object [" + this.getSmallLocation().toString() + "]");

            PadsPlugin.padCache.cache(this);
            PadsPlugin.database.save(this);

            EnderPadCacheEvent event = new EnderPadCacheEvent(this, true);
            event.setEffects(false);
            Bukkit.getPluginManager().callEvent(event);
            return true;
        }
    }

    public boolean isValid() {
        return this.validate();
    }

    private boolean validate() {
        Block center = this.getBukkitLocation().getBlock();
        if (center.getType().toString().equals(Settings.centerMaterial.split("~")[0])) {
            if (EnderPadMethods.isPlate(center.getRelative(BlockFace.UP).getType())) {
                for (BlockFace face : PadsPlugin.faces) {
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
            PadsPlugin.logger.debug("Ignoring save for already cached object [" + this.getSmallLocation().toString() + "]");
            return;
        }

        PadsPlugin.logger.debug("Saving/caching object [" + this.getSmallLocation().toString() + "]");

        PadsPlugin.padCache.cache(this);
        PadsPlugin.database.save(this);

        EnderPadCacheEvent event = new EnderPadCacheEvent(this, true);
        event.setCreator(player);
        Bukkit.getPluginManager().callEvent(event);
    }

    public void delete(Player player) {
        if (!this.isCached()) {
            PadsPlugin.logger.debug("Ignoring delete for un-cached object [" + this.getSmallLocation().toString() + "]");
            return;
        }

        PadsPlugin.logger.debug("Deleting/un-caching object [" + this.getSmallLocation().toString() + "]");

        PadsPlugin.padCache.uncache(this);
        PadsPlugin.database.delete(this);

        EnderPadCacheEvent event = new EnderPadCacheEvent(this, false);
        event.setDestroyer(player);
        Bukkit.getPluginManager().callEvent(event);
    }

    public void teleportEntity(Entity entity, @Nullable ArrayList<EnderPad> links) {
        if (entity instanceof Player) {
            if (!PadsPlugin.permissionCache.hasPermission((Player) entity, "enderpads.use", true)) {
                return;
            }
        }

        if (links == null) {
            links = this.getLinks();
        }

        for (String name : Settings.blackListedWorlds) {
            if (name.toLowerCase().equals(this.getBukkitLocation().getWorld().getName().toLowerCase())) {
                return;
            }
        }

        for (EnderPad pad : links) {
            Location location = pad.getBukkitLocation();
            if (Settings.safeTeleport) {
                Block block = location.getWorld().getBlockAt(((int) location.getX()), ((int) location.getY()) + 3, ((int) location.getZ()));
                if (!block.isEmpty()) {
                    links.remove(pad);
                }
                if (block.getType().isSolid()) {
                    links.remove(pad);
                }
                if (block.getType().equals(Material.LAVA)) {
                    links.remove(pad);
                }
                if (block.getType().equals(Material.STATIONARY_LAVA)) {
                    links.remove(pad);
                }
            }
        }

        if (links.size() > 0) {
            EnderPad destination = links.get(PadsPlugin.random.nextInt(links.size()));
            EnderPadUseEvent useEvent = new EnderPadUseEvent(this, destination, entity);
            Bukkit.getPluginManager().callEvent(useEvent);
        }
    }
}