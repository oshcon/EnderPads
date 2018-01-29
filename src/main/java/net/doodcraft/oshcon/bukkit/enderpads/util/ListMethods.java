package net.doodcraft.oshcon.bukkit.enderpads.util;

import net.doodcraft.oshcon.bukkit.enderpads.PadsPlugin;
import net.doodcraft.oshcon.bukkit.enderpads.cache.EnderPadCache;
import net.doodcraft.oshcon.bukkit.enderpads.config.Settings;
import net.doodcraft.oshcon.bukkit.enderpads.enderpad.EnderPad;
import net.doodcraft.oshcon.bukkit.enderpads.enderpad.EnderPadMethods;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class ListMethods {

    public static void openPlayerList(final Player player, final int page) {
        Bukkit.getScheduler().runTaskAsynchronously(PadsPlugin.plugin, new Runnable() {
            EnderPadCache cache = PadsPlugin.padCache;
            @Override
            public void run() {
                int row = 0;
                int slot = 0;
                int size = cache.getCache().size();
                if (size <= 0) {
                    player.sendMessage(PadsPlugin.logger.addColor(Settings.pluginPrefix + " &cNobody owns an EnderPad."));
                    return;
                }
                int pages = (int) Math.ceil((double) size / 27);
                IconMenu menu = new IconMenu(PadsPlugin.logger.addColor("&8EnderPad Owners &8[&5" + (page + 1) + "&8/&5" + pages + "&8]"), 4, new IconMenu.onClick() {
                    @Override
                    public boolean click(Player p, IconMenu menu, IconMenu.Row row, int slot, ItemStack item) {
                        if (item.getType().equals(Material.SKULL_ITEM)) {
                            UUID uuid = Bukkit.getOfflinePlayer(ChatColor.stripColor(item.getItemMeta().getDisplayName())).getUniqueId();
                            closeMenu(player, menu);
                            openPadListPage(player, uuid, 0);
                        }
                        if (item.getType().equals(Material.BLAZE_POWDER)) {
                            closeMenu(player, menu);
                        }
                        if (item.getType().equals(Material.PAPER)) {
                            closeMenu(player, menu);
                            openPlayerList(player, Integer.valueOf(ChatColor.stripColor(item.getItemMeta().getLore().get(0))) - 1);
                        }
                        return true;
                    }
                });
                List<UUID> uuids = getPage(new ArrayList<>(cache.getOwners().keySet()), page + 1, 27);
                for (UUID uuid : uuids) {
                    String name = PadsPlugin.nameCache.getUsername(uuid);
                    ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                    SkullMeta meta = (SkullMeta) skull.getItemMeta();
                    meta.setOwner(name);
                    skull.setItemMeta(meta);
                    if (getCachedPads(uuid).size() == 1) {
                        menu.addButton(menu.getRow(row), slot, skull, PadsPlugin.logger.addColor("&e" + name), PadsPlugin.logger.addColor("&7" + getCachedPads(uuid).size() + " EnderPad"));
                    } else {
                        menu.addButton(menu.getRow(row), slot, skull, PadsPlugin.logger.addColor("&e" + name), PadsPlugin.logger.addColor("&7" + getCachedPads(uuid).size() + " EnderPads"));
                    }
                    slot++;
                    if (slot >= 9) {
                        row++;
                        slot = 0;
                    }
                    if (row >= 3) {
                        break;
                    }
                }
                menu.addButton(menu.getRow(3), 4, new ItemStack(Material.BLAZE_POWDER), PadsPlugin.logger.addColor("&8[&cClose&8]"));
                if (page < (pages - 1)) {
                    menu.addButton(menu.getRow(3), 8, new ItemStack(Material.PAPER), PadsPlugin.logger.addColor("&8[&3Next Page&8]->"), String.valueOf(page + 2));
                }
                if (page > 0) {
                    menu.addButton(menu.getRow(3), 7, new ItemStack(Material.PAPER), PadsPlugin.logger.addColor("&8<-[&3Previous Page&8]"), String.valueOf(page));
                }
                menu.open(player);
            }
        });
    }

    public static void openPadListPage(final Player player, final UUID uuid, final int page) {
        final List<EnderPad> pads = getPage(getCachedPads(uuid), page + 1, 27);
        if (pads.size() <= 0) {
            player.sendMessage(PadsPlugin.logger.addColor(Settings.pluginPrefix + " &cThat player doesn't own an EnderPad."));
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(PadsPlugin.plugin, new Runnable() {
            @Override
            public void run() {
                int row = 0;
                int slot = 0;
                int size = getCachedPads(uuid).size();
                int pages = (int) Math.ceil((double) size / 27);
                IconMenu menu = new IconMenu(PadsPlugin.logger.addColor("&8Search by Player [&5" + String.valueOf(page + 1) + "&8/&5" + pages + "&8]"), 4, new IconMenu.onClick() {
                    @Override
                    public boolean click(Player p, IconMenu menu, IconMenu.Row row, int slot, ItemStack item) {
                        if (item.getType().equals(Material.valueOf(Settings.centerMaterial.split("~")[0]))) {
                            closeMenu(player, menu);
                            openPadOptions(player, EnderPadMethods.getPadFromString(ChatColor.stripColor(item.getItemMeta().getDisplayName())));
                        }
                        if (item.getType().equals(Material.BLAZE_POWDER)) {
                            closeMenu(player, menu);
                        }
                        if (item.getType().equals(Material.PAPER)) {
                            closeMenu(player, menu);
                            openPadListPage(player, uuid, Integer.valueOf(ChatColor.stripColor(item.getItemMeta().getLore().get(0))) - 1);
                        }
                        if (item.getType().equals(Material.TNT)) {
                            closeMenu(player, menu);
                            for (EnderPad pad : getCachedPads(player.getUniqueId())) {
                                if (pad != null) {
                                    pad.delete(null);
                                }
                            }
                        }
                        return true;
                    }
                });
                for (EnderPad pad : pads) {
                    ItemStack p = new ItemStack(Material.valueOf(Settings.centerMaterial.split("~")[0]));
                    ItemMeta m = p.getItemMeta();
                    m.setLore(Arrays.asList(pad.getCurrentLink().toString().split("-")));
                    p.setItemMeta(m);
                    menu.addButton(menu.getRow(row), slot, p, PadsPlugin.logger.addColor(pad.getSmallLocation().toString()));
                    slot++;
                    if (slot >= 9) {
                        row++;
                        slot = 0;
                    }
                    if (row >= 3) {
                        break;
                    }
                }
                ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                SkullMeta meta = (SkullMeta) skull.getItemMeta();
                meta.setOwner(player.getName());
                skull.setItemMeta(meta);
                menu.addButton(menu.getRow(3), 2, skull, PadsPlugin.logger.addColor("&e" + player.getName()), PadsPlugin.logger.addColor("&7" + getCachedPads(player.getUniqueId()).size() + " EnderPads"));
                menu.addButton(menu.getRow(3), 0, new ItemStack(Material.TNT), PadsPlugin.logger.addColor("&8[&cDestroy All&8]"), PadsPlugin.logger.addColor("&4This cannot be undone!"));
                menu.addButton(menu.getRow(3), 4, new ItemStack(Material.BLAZE_POWDER), PadsPlugin.logger.addColor("&8[&cClose&8]"));
                if (page < (pages - 1)) {
                    menu.addButton(menu.getRow(3), 8, new ItemStack(Material.PAPER), PadsPlugin.logger.addColor("&8[&3Next Page&8]->"), String.valueOf(page + 2));
                }
                if (page > 0) {
                    menu.addButton(menu.getRow(3), 7, new ItemStack(Material.PAPER), PadsPlugin.logger.addColor("&8<-[&3Previous Page&8]"), String.valueOf(page));
                }
                menu.open(player);
            }
        });
    }

    public static void openPadListPageByLink(final Player player, final String link, final int page) {
        final List<EnderPad> pads = getPage(getCachedPadsByLink(link), page + 1, 27);
        if (pads.size() <= 0) {
            player.sendMessage(PadsPlugin.logger.addColor(Settings.pluginPrefix + " &cThere are no EnderPads with that Link ID."));
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(PadsPlugin.plugin, new Runnable() {
            @Override
            public void run() {
                int row = 0;
                int slot = 0;
                int size = getCachedPadsByLink(link).size();
                int pages = (int) Math.ceil((double) size / 27);
                IconMenu menu = new IconMenu(PadsPlugin.logger.addColor("&8Search by LinkID [&5" + String.valueOf(page + 1) + "&8/&5" + pages + "&8]"), 4, new IconMenu.onClick() {
                    @Override
                    public boolean click(Player p, IconMenu menu, IconMenu.Row row, int slot, ItemStack item) {
                        if (item.getType().equals(Material.valueOf(Settings.centerMaterial.split("~")[0]))) {
                            closeMenu(player, menu);
                            openPadOptions(player, EnderPadMethods.getPadFromString(item.getItemMeta().getDisplayName()));
                        }
                        if (item.getType().equals(Material.BLAZE_POWDER)) {
                            closeMenu(player, menu);
                        }
                        if (item.getType().equals(Material.PAPER)) {
                            closeMenu(player, menu);
                            openPadListPageByLink(player, link, Integer.valueOf(ChatColor.stripColor(item.getItemMeta().getLore().get(0))) - 1);
                        }
                        if (item.getType().equals(Material.TNT)) {
                            closeMenu(player, menu);
                            for (EnderPad pad : getCachedPadsByLink(link)) {
                                if (pad != null && pad.isValid()) {
                                    pad.delete(null);
                                }
                            }
                        }
                        return true;
                    }
                });
                for (EnderPad pad : pads) {
                    ItemStack p = new ItemStack(Material.valueOf(Settings.centerMaterial.split("~")[0]));
                    ItemMeta m = p.getItemMeta();
                    m.setLore(Arrays.asList(pad.getCurrentLink().toString().split("-")));
                    p.setItemMeta(m);
                    menu.addButton(menu.getRow(row), slot, p, PadsPlugin.logger.addColor(pad.getSmallLocation().toString()), pad.getOwnerName());
                    slot++;
                    if (slot >= 9) {
                        row++;
                        slot = 0;
                    }
                    if (row >= 3) {
                        break;
                    }
                }
                menu.addButton(menu.getRow(3), 0, new ItemStack(Material.TNT), PadsPlugin.logger.addColor("&8[&cDestroy All&8]"), PadsPlugin.logger.addColor("&4This cannot be undone!"));
                menu.addButton(menu.getRow(3), 4, new ItemStack(Material.BLAZE_POWDER), PadsPlugin.logger.addColor("&8[&cClose&8]"));
                if (page < (pages - 1)) {
                    menu.addButton(menu.getRow(3), 8, new ItemStack(Material.PAPER), PadsPlugin.logger.addColor("&8[&3Next Page&8]->"), String.valueOf((page + 2)));
                }
                if (page > 0) {
                    menu.addButton(menu.getRow(3), 7, new ItemStack(Material.PAPER), PadsPlugin.logger.addColor("&8<-[&3Previous Page&8]"), String.valueOf(page));
                }
                menu.open(player);
            }
        });
    }

    public static void openPadOptions(final Player player, final EnderPad pad) {
        if (pad == null) {
            player.sendMessage(PadsPlugin.logger.addColor(Settings.pluginPrefix + " &cThat EnderPad no longer exists!"));
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(PadsPlugin.plugin, new Runnable() {
            @Override
            public void run() {
                IconMenu menu = new IconMenu(PadsPlugin.logger.addColor("&8" + pad.getSmallLocation().toString()), 2, new IconMenu.onClick() {
                    @Override
                    public boolean click(Player p, IconMenu menu, IconMenu.Row row, int slot, ItemStack item) {
                        if (item.getType().equals(Material.valueOf(Settings.centerMaterial.split("~")[0]))) {
                            closeMenu(player, menu);
                            openPadOptions(player, EnderPadMethods.getPadFromString(item.getItemMeta().getDisplayName()));
                        }
                        if (item.getType().equals(Material.ENDER_PEARL)) {
                            closeMenu(player, menu);
                            Location to = pad.getBukkitLocation();
                            to.setPitch(player.getLocation().getPitch());
                            to.setYaw(player.getLocation().getYaw());
                            player.teleport(to);
                            PadsPlugin.playerCooldowns.put(player.getName(), System.currentTimeMillis());
                        }
                        if (item.getType().equals(Material.EYE_OF_ENDER)) {
                            closeMenu(player, menu);
                            openPadListPageByLink(player, pad.getCurrentLink().toString(), 0);
                        }
                        if (item.getType().equals(Material.SKULL_ITEM)) {
                            closeMenu(player, menu);
                            openPadListPage(player, pad.getOwnerUUID(), 0);
                        }
                        if (item.getType().equals(Material.TNT)) {
                            closeMenu(player, menu);
                            EnderPad d = EnderPadMethods.getPadFromLocation(pad.getBukkitLocation());
                            if (d != null && d.isValid()) {
                                d.delete(null);
                            }
                        }
                        if (item.getType().equals(Material.BLAZE_POWDER)) {
                            closeMenu(player, menu);
                        }
                        return true;
                    }
                });
                ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                SkullMeta meta = (SkullMeta) skull.getItemMeta();
                meta.setOwner(pad.getOwnerName());
                skull.setItemMeta(meta);
                menu.addButton(menu.getRow(0), 0, new ItemStack(Material.ENDER_PEARL), PadsPlugin.logger.addColor("&8[&dTeleport&8]"));
                menu.addButton(menu.getRow(0), 2, skull, PadsPlugin.logger.addColor("&8[&eOwner&8]"), pad.getOwnerName());
                menu.addButton(menu.getRow(0), 6, new ItemStack(Material.EYE_OF_ENDER), PadsPlugin.logger.addColor("&8[&3Link ID&8]"));
                menu.addButton(menu.getRow(0), 8, new ItemStack(Material.TNT), PadsPlugin.logger.addColor("&8[&cDestroy&8]"), PadsPlugin.logger.addColor("&4This cannot be undone!"));
                menu.addButton(menu.getRow(1), 4, new ItemStack(Material.BLAZE_POWDER), PadsPlugin.logger.addColor("&8[&cClose&8]"));
                menu.open(player);
            }
        });
    }

    public static void closeMenu(Player player, IconMenu menu) {
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
        menu.close(player);
    }

    public static List<EnderPad> getCachedPads(UUID uuid) {
        if (PadsPlugin.padCache.getOwners().get(uuid) != null) {
            return PadsPlugin.padCache.getOwners().get(uuid);
        } else {
            return new ArrayList<>();
        }
    }

    public static List<EnderPad> getCachedPadsByLink(String link) {
        List<EnderPad> pads = new ArrayList<>();
        for (String id : PadsPlugin.padCache.getCache().keySet()) {
            EnderPad pad = EnderPadMethods.getPadFromString(id);
            if (pad.getCurrentLink().toString().equals(link)) {
                pads.add(pad);
            }
        }
        return pads;
    }

    public static <T> List<T> getPage(List<T> sourceList, int page, int pageSize) {
        if (pageSize <= 0 || page <= 0) {
            throw new IllegalArgumentException("Invalid page size: " + pageSize);
        }
        int fromIndex = (page - 1) * pageSize;
        if (sourceList == null || sourceList.size() < fromIndex) {
            return Collections.emptyList();
        }
        return sourceList.subList(fromIndex, Math.min(fromIndex + pageSize, sourceList.size()));
    }
}