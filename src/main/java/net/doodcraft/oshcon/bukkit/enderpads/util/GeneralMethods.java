package net.doodcraft.oshcon.bukkit.enderpads.util;

import net.doodcraft.oshcon.bukkit.enderpads.EnderPadsPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.metadata.MetadataValue;

public class GeneralMethods {

    private static boolean PRE_19 = true;

    public static boolean isVanished(Player player) {
        if (player == null) {
            return false;
        }
        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) return true;
        }
        return false;
    }

    public static boolean isOffHandClick(PlayerInteractEvent event) {
        if (!PRE_19) {
            return event.getHand().equals(EquipmentSlot.valueOf("OFF_HAND"));
        }
        // Maintain compatibility with versions prior to the 1.9 combat update.
        if (Compatibility.isSupported(EnderPadsPlugin.version, "1.9", "2.0")) {
            PRE_19 = false;
            return event.getHand().equals(EquipmentSlot.valueOf("OFF_HAND"));
        } else {
            try {
                PRE_19 = true;
                return event.getHand().equals(EquipmentSlot.valueOf("OFF_HAND"));
            } catch (NoSuchMethodError ex) {
                return false;
            }
        }
    }
}