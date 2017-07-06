package net.doodcraft.oshcon.bukkit.enderpads.util;

import de.myzelyam.api.vanish.VanishAPI;
import net.doodcraft.oshcon.bukkit.enderpads.EnderPadsPlugin;
import net.doodcraft.oshcon.bukkit.enderpads.config.Settings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.logging.Level;
import java.util.logging.Logger;

public class StaticMethods
{
    public static boolean isVanished(Player player)
    {
        if (player == null)
        {
            return false;
        }

        if (Compatibility.isHooked("SuperVanish") || Compatibility.isHooked("PremiumVanish"))
        {
            try
            {
                if (VanishAPI.isInvisible(player))
                {
                    return true;
                }
            } catch (NoSuchMethodError ex)
            {
                StaticMethods.log("&cThere was an error using VanishAPI for Vanish. It may not exist?");
                StaticMethods.log(ex.getLocalizedMessage());
                StaticMethods.log("&cCheck your SuperVanish or PremiumVanish version.");
                return false;
            }
        }

        if (Compatibility.isHooked("Essentials"))
        {
            try
            {
                if (Compatibility.essentials.getVanishedPlayers().contains(player.getName()))
                {
                    return true;
                }
            } catch (NoSuchMethodError ex)
            {
                StaticMethods.log("&cThere was an error using Essentials for Vanish. It may not exist?");
                StaticMethods.log(ex.getLocalizedMessage());
                StaticMethods.log("&cCheck your Essentials version.");
                return false;
            }
        }

        return false;
    }

    public static boolean isOffHandClick(PlayerInteractEvent event)
    {
        if (Compatibility.isSupported(EnderPadsPlugin.version, "1.9", "2.0"))
        {
            return event.getHand().equals(EquipmentSlot.valueOf("OFF_HAND"));
        }
        else
        {
            try
            {
                return event.getHand().equals(EquipmentSlot.valueOf("OFF_HAND"));
            } catch (NoSuchMethodError ex)
            {
                return false;
            }
        }
    }

    public static Boolean hasPermission(Player player, String node, Boolean sendError)
    {
        if (player.isOp())
        {
            return true;
        }

        if (player.hasPermission(EnderPadsPlugin.plugin.getName().toLowerCase() + ".*"))
        {
            return true;
        }

        if (player.hasPermission(node))
        {
            return true;
        }

        if (sendError)
        {
            player.sendMessage(StringParser.parse(Settings.noPermission, null, null, null, false, false));
        }

        return false;
    }

    public static void log(String message)
    {
        try
        {
            message = Settings.pluginPrefix + " &r" + message;
            sendConsole(message);
        } catch (Exception ex)
        {
            Logger logger = Bukkit.getLogger();
            logger.log(Level.INFO, removeColor("[EnderPads] " + message));
        }
    }

    public static void debug(String message)
    {
        try
        {
            if (Settings.debug)
            {
                message = "&8[&dDEBUG&8] &e" + message;
                log(message);
            }
        } catch (Exception ex)
        {
            Logger logger = Bukkit.getLogger();
            logger.log(Level.INFO, removeColor("[EnderPads] [DEBUG] " + message));
        }
    }

    private static void sendConsole(String message)
    {
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();

        try
        {
            if (Settings.colorfulLogging)
            {
                console.sendMessage(addColor(message));
            }
            else
            {
                console.sendMessage(removeColor(addColor(message)));
            }
        } catch (Exception ignored)
        {
            console.sendMessage(removeColor(addColor(message)));
        }
    }

    public static String addColor(String message)
    {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    private static String removeColor(String message)
    {
        message = addColor(message);
        return ChatColor.stripColor(message);
    }
}