package net.doodcraft.oshcon.bukkit.enderpads.util;

import net.doodcraft.oshcon.bukkit.enderpads.config.Settings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

import java.util.logging.Level;

public class Logger {

    private ConsoleCommandSender console;

    public Logger() {
        console = Bukkit.getServer().getConsoleSender();
    }

    public void log(String message) {
        try {
            message = Settings.pluginPrefix + " &r" + message;
            sendConsole(message);
        } catch (Exception ex) {
            java.util.logging.Logger logger = Bukkit.getLogger();
            logger.log(Level.INFO, removeColor("[EnderPads] " + message));
        }
    }

    public void debug(String message) {
        try {
            if (Settings.debug) {
                message = "&8[&dDEBUG&8] &e" + message;
                log(message);
            }
        } catch (Exception ex) {
            java.util.logging.Logger logger = Bukkit.getLogger();
            logger.log(Level.INFO, removeColor("[EnderPads] [DEBUG] " + message));
        }
    }

    private void sendConsole(String message) {
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

    public String addColor(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    private String removeColor(String message) {
        // This strips the color and formatting codes for us.
        message = addColor(message);
        return ChatColor.stripColor(message);
    }
}