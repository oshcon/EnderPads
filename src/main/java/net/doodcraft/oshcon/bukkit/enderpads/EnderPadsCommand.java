package net.doodcraft.oshcon.bukkit.enderpads;

import net.doodcraft.oshcon.bukkit.enderpads.config.Settings;
import net.doodcraft.oshcon.bukkit.enderpads.util.StaticMenuMethods;
import net.doodcraft.oshcon.bukkit.enderpads.util.StaticMethods;
import net.doodcraft.oshcon.bukkit.enderpads.util.StringParser;
import net.doodcraft.oshcon.bukkit.enderpads.util.UUIDCache;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EnderPadsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (label.equalsIgnoreCase("enderpads")) {

            if (sender instanceof Player) {

                final Player player = (Player) sender;

                if (!StaticMethods.hasPermission(player, "enderpads.command.enderpads", true)) {
                    return false;
                }

                if (args.length == 0) {
                    sendValidCommands(sender);
                    return true;
                }

                if (args[0].equalsIgnoreCase("reload")) {

                    if (!StaticMethods.hasPermission(player, "enderpads.command.reload", true)) {
                        return false;
                    }

                    boolean error = Settings.reload();
                    sendReloaded(error, sender);
                    return true;
                }

                if (args[0].equalsIgnoreCase("list")) {

                    if (!StaticMethods.hasPermission(player, "enderpads.command.list", true)) {
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

                player.sendMessage(StaticMethods.addColor(Settings.pluginPrefix + "&cIncorrect subcommand."));
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

    public static void sendReloaded(boolean error, CommandSender sender) {

        if (!error) {

            if (sender instanceof Player) {
                sender.sendMessage(StringParser.parse(Settings.reloadSuccess, null, null, null, false, false));
                StaticMethods.log("&aPlugin reloaded!");
            } else {
                StaticMethods.log("&aPlugin reloaded!");
            }

        } else {

            if (sender instanceof Player) {
                sender.sendMessage(StringParser.parse(Settings.reloadFailed, null, null, null, false, false));
                StaticMethods.log("&cError reloading plugin!");
            } else {
                StaticMethods.log("&cError reloading plugin!");
            }
        }
    }

    public static void sendValidCommands(CommandSender sender) {

        if (sender instanceof Player) {
            sender.sendMessage(StaticMethods.addColor(Settings.pluginPrefix + " &3Valid Commands:"));
            sender.sendMessage(StaticMethods.addColor(Settings.pluginPrefix + " &b/enderpads reload: &7Reloads the config and verifies all pad data"));
            sender.sendMessage(StaticMethods.addColor(Settings.pluginPrefix + " &b/enderpads list: &7List a player's EnderPads"));
        } else {
            StaticMethods.log("&3Valid Commands:");
            StaticMethods.log("&b/enderpads reload: &7Reloads the config and verifies all pad data");
            StaticMethods.log("&b/enderpads list: &7List a player's EnderPads");
        }
    }
}