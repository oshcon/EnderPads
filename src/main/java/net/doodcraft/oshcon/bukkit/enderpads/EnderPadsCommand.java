package net.doodcraft.oshcon.bukkit.enderpads;

import net.doodcraft.oshcon.bukkit.enderpads.config.Configuration;
import net.doodcraft.oshcon.bukkit.enderpads.config.Settings;
import net.doodcraft.oshcon.bukkit.enderpads.util.IconMenu;
import net.doodcraft.oshcon.bukkit.enderpads.util.StaticMethods;
import net.doodcraft.oshcon.bukkit.enderpads.util.StringParser;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;
import java.util.UUID;

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
                        IconMenu menu = new IconMenu("Players Using EnderPads", 4, new IconMenu.onClick() {
                            @Override
                            public boolean click(Player p, IconMenu menu, IconMenu.Row row, int slot, ItemStack item) {
                                if (item.getType().equals(Material.SKULL)) {
                                    // its a player head
                                    OfflinePlayer op = Bukkit.getOfflinePlayer(item.getItemMeta().getDisplayName());
                                    UUID uuid = op.getUniqueId();
                                    // create new IconMenu for that player, with their EnderPads as icons
                                }

                                if (item.getType().equals(Material.BARRIER)) {
                                    menu.close(player);
                                }
                                return true;
                            }
                        });

                        Configuration players = new Configuration(EnderPadsPlugin.plugin.getDataFolder() + File.separator + "data" + File.separator + "players.yml");
                        int row = 0;
                        int slot = 0;
                        int size = players.getKeys(false).size();

                        if (size <= 0) {
                            player.sendMessage(StaticMethods.addColor("Nobody is using EnderPads."));
                        }

                        int pages = (int) Math.ceil(size/27);
                        int count = 0;
                        for (String uuid : players.getKeys(false)) {
                            OfflinePlayer op = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                            menu.addButton(menu.getRow(row), slot, new ItemStack(Material.SKULL), op.getName());
                            slot++;
                            count++;
                            if (slot >= 8) {
                                // too many for one row, add another row
                                row++;
                                slot = 0;
                            }
                            if (count >= 27) {
                                // more than one page
                                // create another menu we can access?
                                break;
                            }
                        }
                        menu.addButton(menu.getRow(4), 4, new ItemStack(Material.BARRIER), "[Close]");
                        if (count >= 27) {
                            menu.addButton(menu.getRow(4), 8, new ItemStack(Material.PAPER), "Next Page");
                        }
                        menu.open(player);
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