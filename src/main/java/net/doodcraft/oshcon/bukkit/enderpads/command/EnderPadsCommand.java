package net.doodcraft.oshcon.bukkit.enderpads.command;

import net.doodcraft.oshcon.bukkit.enderpads.EnderPadsPlugin;
import net.doodcraft.oshcon.bukkit.enderpads.cache.PermissionCache;
import net.doodcraft.oshcon.bukkit.enderpads.cache.UUIDCache;
import net.doodcraft.oshcon.bukkit.enderpads.config.Settings;
import net.doodcraft.oshcon.bukkit.enderpads.util.StaticMenuMethods;
import net.doodcraft.oshcon.bukkit.enderpads.util.StringParser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EnderPadsCommand implements CommandExecutor {

    public static void sendReloaded(boolean error, CommandSender sender) {
        if (!error) {
            if (sender instanceof Player) {
                sender.sendMessage(StringParser.parse(Settings.reloadSuccess, null, null, null, false, false));
                EnderPadsPlugin.logger.log("&aPlugin reloaded!");
            } else {
                EnderPadsPlugin.logger.log("&aPlugin reloaded!");
            }

        } else {
            if (sender instanceof Player) {
                sender.sendMessage(StringParser.parse(Settings.reloadFailed, null, null, null, false, false));
                EnderPadsPlugin.logger.log("&cError reloading plugin!");
            } else {
                EnderPadsPlugin.logger.log("&cError reloading plugin!");
            }
        }
    }

    public static void sendValidCommands(CommandSender sender) {
        if (sender instanceof Player) {
            sender.sendMessage(EnderPadsPlugin.logger.addColor(Settings.pluginPrefix + " &3Valid Commands:"));
            sender.sendMessage(EnderPadsPlugin.logger.addColor(Settings.pluginPrefix + " &b/enderpads reload: &7Reloads the config and verifies all pad data"));
            sender.sendMessage(EnderPadsPlugin.logger.addColor(Settings.pluginPrefix + " &b/enderpads list: &7List a player's EnderPads"));
        } else {
            EnderPadsPlugin.logger.log("&3Valid Commands:");
            EnderPadsPlugin.logger.log("&b/enderpads reload: &7Reloads the config and verifies all pad data");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("enderpads")) {
            if (sender instanceof Player) {
                final Player player = (Player) sender;
                if (!PermissionCache.hasPermission(player, "enderpads.command.enderpads", true)) {
                    return false;
                }
                if (args.length == 0) {
                    sendValidCommands(sender);
                    return true;
                }
                if (args[0].equalsIgnoreCase("reload")) {
                    if (!PermissionCache.hasPermission(player, "enderpads.command.reload", true)) {
                        return false;
                    }
                    boolean error = Settings.reload();
                    sendReloaded(error, sender);
                    return true;
                }
                if (args[0].equalsIgnoreCase("list")) {
                    if (!PermissionCache.hasPermission(player, "enderpads.command.list", true)) {
                        return false;
                    }
                    if (args.length <= 1) {
                        // list players using EnderPads
                        // create iconmenu
                        StaticMenuMethods.openPlayerList(player, 0);
                        return true;
                    } else {
                        // specified player name as second arg
                        StaticMenuMethods.openPadListPage(player, UUIDCache.getUniqueID(args[1]), 0);
                        return true;
                    }
                }
                player.sendMessage(EnderPadsPlugin.logger.addColor(Settings.pluginPrefix + "&cIncorrect subcommand."));
                sendValidCommands(sender);
                return false;
            } else {
                if (args.length == 0) {
                    sendValidCommands(sender);
                    return true;
                }
                if (args[0].equalsIgnoreCase("reload")) {
                    boolean error = Settings.reload();
                    sendReloaded(error, sender);
                    return true;
                }
            }
        }
        return false;
    }
}