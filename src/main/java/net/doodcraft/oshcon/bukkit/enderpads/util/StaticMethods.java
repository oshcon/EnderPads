package net.doodcraft.oshcon.bukkit.enderpads.util;

import net.doodcraft.oshcon.bukkit.enderpads.EnderPadsPlugin;
import net.doodcraft.oshcon.bukkit.enderpads.config.Settings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.metadata.MetadataValue;

import java.util.logging.Level;
import java.util.logging.Logger;

public class StaticMethods {

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

    public static Boolean hasPermission(Player player, String node, Boolean sendError) {

        if (PermissionCache.hasPermission(player.getUniqueId(), node)) {

            return true;

        } else {

            if (sendError) {
                player.sendMessage(StringParser.parse(Settings.noPermission, null, null, null, false, false));
            }

            return false;
        }
    }

    public static void log(String message) {

        try {
            message = Settings.pluginPrefix + " &r" + message;
            sendConsole(message);
        } catch (Exception ex) {
            Logger logger = Bukkit.getLogger();
            logger.log(Level.INFO, removeColor("[EnderPads] " + message));
        }
    }

    public static void debug(String message) {

        try {
            if (Settings.debug) {
                message = "&8[&dDEBUG&8] &e" + message;
                log(message);
            }
        } catch (Exception ex) {
            Logger logger = Bukkit.getLogger();
            logger.log(Level.INFO, removeColor("[EnderPads] [DEBUG] " + message));
        }
    }

    private static void sendConsole(String message) {

        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();

        try {
            if (Settings.colorfulLogging) {
                console.sendMessage(addColor(message));
            } else {
                console.sendMessage(removeColor(addColor(message)));
            }
        } catch (Exception ignored) {
            console.sendMessage(removeColor(addColor(message)));
        }
    }

    public static String addColor(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    private static String removeColor(String message) {
        // This strips the color and formatting codes for us.
        message = addColor(message);
        return ChatColor.stripColor(message);
    }
}