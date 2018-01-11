package net.doodcraft.oshcon.bukkit.enderpads.util;

import net.doodcraft.oshcon.bukkit.enderpads.EnderPadsPlugin;
import net.doodcraft.oshcon.bukkit.enderpads.api.EnderPad;
import net.doodcraft.oshcon.bukkit.enderpads.api.EnderPadAPI;
import net.doodcraft.oshcon.bukkit.enderpads.config.Configuration;
import net.doodcraft.oshcon.bukkit.enderpads.config.Settings;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.util.*;

public class StaticMenuMethods {

    public static void openPlayerList(final Player player, final int page) {

        Bukkit.getScheduler().runTaskAsynchronously(EnderPadsPlugin.plugin, new Runnable() {
            @Override
            public void run() {
                Configuration players = new Configuration(EnderPadsPlugin.plugin.getDataFolder() + File.separator + "data" + File.separator + "players.yml");

                int row = 0;
                int slot = 0;
                int size = players.getKeys(false).size();

                if (size <= 0) {
                    player.sendMessage(StaticMethods.addColor(Settings.pluginPrefix + " &cNobody owns an EnderPad."));
                    return;
                }

                int pages = (int) Math.ceil((double)size/27);

                IconMenu menu = new IconMenu(StaticMethods.addColor("&8EnderPad Owners &8[&5" + (page + 1) + "&8/&5" + pages + "&8]"), 4, new IconMenu.OnClick() {
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

                List<String> uuids = getPage(new ArrayList<>(players.getKeys(false)), page + 1, 27);

                for (String uuid : uuids) {
                    String name = NameCache.getUsername(UUID.fromString(uuid));
                    ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                    SkullMeta meta = (SkullMeta) skull.getItemMeta();
                    meta.setOwner(name);
                    skull.setItemMeta(meta);
                    if (getCachedPads(UUID.fromString(uuid)).size() == 1) {
                        menu.addButton(menu.getRow(row), slot, skull, StaticMethods.addColor("&e" + name), StaticMethods.addColor("&7" + getCachedPads(UUID.fromString(uuid)).size() + " EnderPad"));
                    } else {
                        menu.addButton(menu.getRow(row), slot, skull, StaticMethods.addColor("&e" + name), StaticMethods.addColor("&7" + getCachedPads(UUID.fromString(uuid)).size() + " EnderPads"));
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

                menu.addButton(menu.getRow(3), 4, new ItemStack(Material.BLAZE_POWDER), StaticMethods.addColor("&8[&cClose&8]"));

                if (page < (pages - 1)) {
                    menu.addButton(menu.getRow(3), 8, new ItemStack(Material.PAPER), StaticMethods.addColor("&8[&3Next Page&8]->"), String.valueOf(page + 2));
                }

                if (page > 0) {
                    menu.addButton(menu.getRow(3), 7, new ItemStack(Material.PAPER), StaticMethods.addColor("&8<-[&3Previous Page&8]"), String.valueOf(page));
                }

                menu.open(player);
            }
        });
    }

    public static void openPadListPage(final Player player, final UUID uuid, final int page) {

        final List<EnderPad> pads = getPage(getCachedPads(uuid), page + 1, 27);

        if (pads.size() <= 0) {
            player.sendMessage(StaticMethods.addColor(Settings.pluginPrefix + " &cThat player doesn't own an EnderPad."));
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(EnderPadsPlugin.plugin, new Runnable() {
            @Override
            public void run() {
                int row = 0;
                int slot = 0;
                int size = getCachedPads(uuid).size();

                int pages = (int) Math.ceil((double)size/27);

                IconMenu menu = new IconMenu(StaticMethods.addColor("&8Search by Player [&5" + String.valueOf(page + 1) + "&8/&5" + pages + "&8]"), 4, new IconMenu.OnClick() {
                    @Override
                    public boolean click(Player p, IconMenu menu, IconMenu.Row row, int slot, ItemStack item) {

                        if (item.getType().equals(Material.valueOf(Settings.centerMaterial.toUpperCase().split("~")[0]))) {
                            closeMenu(player, menu);
                            openPadOptions(player, EnderPadAPI.getPadFromID(ChatColor.stripColor(item.getItemMeta().getDisplayName())));
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
                                EnderPad d = EnderPadAPI.getPadFromLocation(pad.getLocation());
                                if (d != null && d.isValid()) {
                                    d.delete(null);
                                }
                            }
                        }

                        return true;
                    }
                });

                for (EnderPad pad : pads) {

                    ItemStack p = new ItemStack(Material.valueOf(Settings.centerMaterial.toUpperCase().split("~")[0]));
                    ItemMeta m = p.getItemMeta();
                    m.setLore(Arrays.asList(pad.getLinkId().split("-")));
                    p.setItemMeta(m);
                    menu.addButton(menu.getRow(row), slot, p, StaticMethods.addColor(pad.getPadId()));
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

                menu.addButton(menu.getRow(3), 2, skull, StaticMethods.addColor("&e" + player.getName()), StaticMethods.addColor("&7" + getCachedPads(player.getUniqueId()).size() + " EnderPads"));
                menu.addButton(menu.getRow(3), 0, new ItemStack(Material.TNT), StaticMethods.addColor("&8[&cDestroy All&8]"), StaticMethods.addColor("&4This cannot be undone!"));
                menu.addButton(menu.getRow(3), 4, new ItemStack(Material.BLAZE_POWDER), StaticMethods.addColor("&8[&cClose&8]"));

                if (page < (pages - 1)) {
                    menu.addButton(menu.getRow(3), 8, new ItemStack(Material.PAPER), StaticMethods.addColor("&8[&3Next Page&8]->"), String.valueOf(page + 2));
                }

                if (page > 0) {
                    menu.addButton(menu.getRow(3), 7, new ItemStack(Material.PAPER), StaticMethods.addColor("&8<-[&3Previous Page&8]"), String.valueOf(page));
                }

                menu.open(player);
            }
        });
    }

    public static void openPadListPageByLink(final Player player, final String link, final int page) {
        final List<EnderPad> pads = getPage(getCachedPadsByLink(link), page + 1, 27);

        if (pads.size() <= 0) {
            player.sendMessage(StaticMethods.addColor(Settings.pluginPrefix + " &cThere are no EnderPads with that Link ID."));
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(EnderPadsPlugin.plugin, new Runnable() {
            @Override
            public void run() {
                int row = 0;
                int slot = 0;
                int size = getCachedPadsByLink(link).size();

                int pages = (int) Math.ceil((double)size/27);

                IconMenu menu = new IconMenu(StaticMethods.addColor("&8Search by LinkID [&5" + String.valueOf(page + 1) + "&8/&5" + pages + "&8]"), 4, new IconMenu.OnClick() {
                    @Override
                    public boolean click(Player p, IconMenu menu, IconMenu.Row row, int slot, ItemStack item) {

                        if (item.getType().equals(Material.valueOf(Settings.centerMaterial.toUpperCase().split("~")[0]))) {
                            closeMenu(player, menu);
                            openPadOptions(player, EnderPadAPI.getPadFromID(item.getItemMeta().getDisplayName()));
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
                                EnderPad d = EnderPadAPI.getPadFromLocation(pad.getLocation());
                                if (d != null && d.isValid()) {
                                    d.delete(null);
                                }
                            }
                        }

                        return true;
                    }
                });

                for (EnderPad pad : pads) {
                    ItemStack p = new ItemStack(Material.valueOf(Settings.centerMaterial.toUpperCase().split("~")[0]));
                    ItemMeta m = p.getItemMeta();
                    m.setLore(Arrays.asList(pad.getLinkId().split("-")));
                    p.setItemMeta(m);
                    menu.addButton(menu.getRow(row), slot, p, StaticMethods.addColor(pad.getPadId()),  pad.getOwnerName());
                    slot++;
                    if (slot >= 9) {
                        row++;
                        slot = 0;
                    }
                    if (row >= 3) {
                        break;
                    }
                }

                menu.addButton(menu.getRow(3), 0, new ItemStack(Material.TNT), StaticMethods.addColor("&8[&cDestroy All&8]"), StaticMethods.addColor("&4This cannot be undone!"));
                menu.addButton(menu.getRow(3), 4, new ItemStack(Material.BLAZE_POWDER), StaticMethods.addColor("&8[&cClose&8]"));

                if (page < (pages - 1)) {
                    menu.addButton(menu.getRow(3), 8, new ItemStack(Material.PAPER), StaticMethods.addColor("&8[&3Next Page&8]->"), String.valueOf((page + 2)));
                }

                if (page > 0) {
                    menu.addButton(menu.getRow(3), 7, new ItemStack(Material.PAPER), StaticMethods.addColor("&8<-[&3Previous Page&8]"), String.valueOf(page));
                }

                menu.open(player);
            }
        });
    }

    public static void openPadOptions(final Player player, final EnderPad pad) {

        if (pad == null) {
            player.sendMessage(StaticMethods.addColor(Settings.pluginPrefix + " &cThat EnderPad no longer exists!"));
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(EnderPadsPlugin.plugin, new Runnable() {
            @Override
            public void run() {
                IconMenu menu = new IconMenu(StaticMethods.addColor("&8" + pad.getPadId()), 2, new IconMenu.OnClick() {
                    @Override
                    public boolean click(Player p, IconMenu menu, IconMenu.Row row, int slot, ItemStack item) {

                        if (item.getType().equals(Material.valueOf(Settings.centerMaterial.toUpperCase().split("~")[0]))) {
                            closeMenu(player, menu);
                            openPadOptions(player, EnderPadAPI.getPadFromID(item.getItemMeta().getDisplayName()));
                        }

                        if (item.getType().equals(Material.ENDER_PEARL)) {
                            closeMenu(player, menu);
                            Location to = pad.getLocation();
                            to.setPitch(player.getLocation().getPitch());
                            to.setYaw(player.getLocation().getYaw());
                            player.teleport(to);
                            EnderPadsPlugin.playerCooldowns.put(player.getName(), System.currentTimeMillis());
                        }

                        if (item.getType().equals(Material.EYE_OF_ENDER)) {
                            closeMenu(player, menu);
                            openPadListPageByLink(player, pad.getLinkId(), 0);
                        }

                        if (item.getType().equals(Material.SKULL_ITEM)) {
                            closeMenu(player, menu);
                            openPadListPage(player, pad.getOwnerUUID(), 0);
                        }

                        if (item.getType().equals(Material.TNT)) {
                            closeMenu(player, menu);
                            EnderPad d = EnderPadAPI.getPadFromLocation(pad.getLocation());
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

                menu.addButton(menu.getRow(0), 0, new ItemStack(Material.ENDER_PEARL), StaticMethods.addColor("&8[&dTeleport&8]"));
                menu.addButton(menu.getRow(0), 2, skull, StaticMethods.addColor("&8[&eOwner&8]"), pad.getOwnerName());
                menu.addButton(menu.getRow(0), 6, new ItemStack(Material.EYE_OF_ENDER), StaticMethods.addColor("&8[&3Link ID&8]"));
                menu.addButton(menu.getRow(0), 8, new ItemStack(Material.TNT), StaticMethods.addColor("&8[&cDestroy&8]"), StaticMethods.addColor("&4This cannot be undone!"));
                menu.addButton(menu.getRow(1), 4, new ItemStack(Material.BLAZE_POWDER), StaticMethods.addColor("&8[&cClose&8]"));

                menu.open(player);
            }
        });
    }

    public static void closeMenu(Player player, IconMenu menu) {
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
        menu.close(player);
    }

    public static List<EnderPad> getCachedPads(UUID uuid) {
        List<EnderPad> pads = new ArrayList<>();
        for (String id : EnderPadsPlugin.enderPads.keySet()) {
            EnderPad pad = EnderPadAPI.getPadFromID(id);
            if (pad.getOwnerUUID().equals(uuid)) {
                pads.add(EnderPadsPlugin.enderPads.get(id));
            }
        }
        return pads;
    }

    public static List<EnderPad> getCachedPadsByLink(String link) {
        List<EnderPad> pads = new ArrayList<>();
        for (String id : EnderPadsPlugin.enderPads.keySet()) {
            EnderPad pad = EnderPadAPI.getPadFromID(id);
            if (pad.getLinkId().equals(link)) {
                pads.add(pad);
            }
        }
        return pads;
    }

    public static <T> List<T> getPage(List<T> sourceList, int page, int pageSize) {
        if(pageSize <= 0 || page <= 0) {
            throw new IllegalArgumentException("Invalid page size: " + pageSize);
        }

        int fromIndex = (page - 1) * pageSize;
        if(sourceList == null || sourceList.size() < fromIndex){
            return Collections.emptyList();
        }

        return sourceList.subList(fromIndex, Math.min(fromIndex + pageSize, sourceList.size()));
    }
}