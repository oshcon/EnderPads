package net.doodcraft.oshcon.bukkit.enderpads.enderpad;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class SmallLocation {

    private String world;
    private double x = 0;
    private double y = 0;
    private double z = 0;
    private float yaw = 0;
    private float pitch = 0;

    public SmallLocation(Location location) {
        this.world = location.getWorld().getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
    }

    public SmallLocation(String world, double x, double y, double z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public SmallLocation(String world, double x, double y, double z, float yaw, float pitch) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Location getBukkitLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

    public String getPadId() {
        return this.world + ", " + (int) this.x + ", " + (int) this.y + ", " + (int) this.z;
    }

    // todo: This is sloppy. Change this soon.
    public static SmallLocation fromString(String s, String splitRegex) {
        String[] part = s.split(splitRegex);
        return new SmallLocation(part[0], Double.valueOf(part[1]), Double.valueOf(part[2]), Double.valueOf(part[3]));
    }

    @Override
    public String toString() {
        return getPadId();
    }
}