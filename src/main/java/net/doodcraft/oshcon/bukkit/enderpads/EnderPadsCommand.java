package net.doodcraft.oshcon.bukkit.enderpads;

import net.doodcraft.oshcon.bukkit.enderpads.config.Configuration;
import net.doodcraft.oshcon.bukkit.enderpads.config.Settings;
import net.doodcraft.oshcon.bukkit.enderpads.util.StaticMethods;
import net.doodcraft.oshcon.bukkit.enderpads.util.StringParser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;

public class EnderPadsCommand implements CommandExecutor {
    private double calculateAverage(List<Long> times) {
        Long sum = 0L;
        if (!times.isEmpty()) {
            for (Long time : times) {
                sum += time;
            }
            return sum.doubleValue() / times.size();
        }
        return sum;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("enderpads")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

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
        } else {
            StaticMethods.log("&3Valid Commands:");
            StaticMethods.log("&b/enderpads reload: &7Reloads the config and verifies all pad data");
        }
    }
}