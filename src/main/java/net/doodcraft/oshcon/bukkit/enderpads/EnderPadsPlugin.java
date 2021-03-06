package net.doodcraft.oshcon.bukkit.enderpads;

import de.slikey.effectlib.EffectManager;
import net.doodcraft.oshcon.bukkit.enderpads.api.EnderPad;
import net.doodcraft.oshcon.bukkit.enderpads.api.EnderPadAPI;
import net.doodcraft.oshcon.bukkit.enderpads.config.Configuration;
import net.doodcraft.oshcon.bukkit.enderpads.config.Settings;
import net.doodcraft.oshcon.bukkit.enderpads.listeners.*;
import net.doodcraft.oshcon.bukkit.enderpads.util.Compatibility;
import net.doodcraft.oshcon.bukkit.enderpads.util.PermissionCache;
import net.doodcraft.oshcon.bukkit.enderpads.util.StaticMethods;
import org.bstats.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class EnderPadsPlugin extends JavaPlugin {

    public static Plugin plugin;
    public static String version;
    public static Random random;
    public static Metrics metrics;

    public static Map<String, Long> playerCooldowns = new HashMap<>();
    public static Map<String, EnderPad> enderPads = new HashMap<>();

    public static BlockFace faces[] = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

    @Override
    public void onEnable() {
        version = Bukkit.getBukkitVersion().split("-")[0];

        plugin = this;

        random = new Random();

        Settings.setupDefaults();

        if (!Compatibility.isSupported(version, "1.7.10", "1.12.2")) {
            StaticMethods.log("&c[PSA]: This version of EnderPads has not been tested with this version of Minecraft. Support may not be given if there are errors. Avoid using this in production. An update is already likely underway and will release soon. Thank you for your patience!");
        }

        if (Settings.enablePsas) {
            if (version.equals("1.12")) {
                StaticMethods.log("&c[PSA]: &eThere is a game-breaking bug in 1.12 with the crafting guide. Players can DUPLICATE items without being detected. Consider updating your server NOW.");
            }
        }

        Compatibility.checkHooks();

        registerListeners();
        setExecutors();

        StaticMethods.log("&aEnderPads v" + plugin.getDescription().getVersion() + " is now loaded.");

        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                EnderPadAPI.verifyAllTelepads();
            }
        }, 1L);

        try {
            metrics = new Metrics(this);
            metrics.addCustomChart(new Metrics.SingleLineChart("total_enderpads") {
                @Override
                public int getValue() {
                    Configuration pads = new Configuration(EnderPadsPlugin.plugin.getDataFolder() + File.separator + "data" + File.separator + "pads.yml");
                    return pads.getKeys(false).size();
                }
            });
            metrics.addCustomChart(new Metrics.SingleLineChart("total_combinations") {
                @Override
                public int getValue() {
                    Configuration linked = new Configuration(EnderPadsPlugin.plugin.getDataFolder() + File.separator + "data" + File.separator + "linked.yml");
                    return linked.getKeys(false).size();
                }
            });
        } catch (Exception ex) {
            StaticMethods.log("&a[METRICS] &cThere was an error sending metrics to bStats.");
        }

        Effects.effectManager = new EffectManager(plugin);
        Effects.addAll();
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(plugin);
        Effects.idleTasks.clear();
    }

    public void registerListeners() {
        registerEvents(plugin, new PlayerListener());
        registerEvents(plugin, new EntityListener());
        registerEvents(plugin, new BlockListener());
        registerEvents(plugin, new EnderPadListener());
        registerEvents(plugin, new Effects());
        registerEvents(plugin, new PermissionCache());

        // BlockExplodeEvent was added in 1.8. We still want to support 1.7.10.
        if (!Compatibility.isSupported(version, "0.0.1", "1.7.10")) {
            registerEvents(plugin, new BlockExplodeListener());
        }
    }

    public static void registerEvents(Plugin plugin, Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }

    public void setExecutors() {
        getCommand("enderpads").setExecutor(new EnderPadsCommand());
    }

    // TODO: 0.3.6-beta
    // Add destroy option to icon menu
    // Add full entity teleportation
    // Cache player permissions for 15 minutes or until player quits
    // TODO: Add support for passenger entities
    // TODO: Check performance, use caches where able
    // TODO: Begin workaround for 1.13 data value removals (though Spigot aims to retain legacy capability, temporarily)
    // TODO: Clean up code, reformat, keep it consistent.
    // TODO: Discover source of rare memory leak in PlayerListener.onInteract
    // TODO: Add option to disable EnderPad validation on use, use the cache instead
}