package net.doodcraft.oshcon.bukkit.enderpads;

import de.slikey.effectlib.EffectManager;
import net.doodcraft.oshcon.bukkit.enderpads.api.EnderPad;
import net.doodcraft.oshcon.bukkit.enderpads.api.EnderPadAPI;
import net.doodcraft.oshcon.bukkit.enderpads.config.Configuration;
import net.doodcraft.oshcon.bukkit.enderpads.config.Settings;
import net.doodcraft.oshcon.bukkit.enderpads.listeners.*;
import net.doodcraft.oshcon.bukkit.enderpads.util.Compatibility;
import net.doodcraft.oshcon.bukkit.enderpads.util.IconMenu;
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
    public static BlockFace faces[] = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
    public static Map<String, Long> playerCooldowns = new HashMap<>();
    public static Map<String, EnderPad> enderPads = new HashMap<>();

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();

        version = Bukkit.getBukkitVersion().split("-")[0];
        plugin = this;
        random = new Random();

        if (!Compatibility.isSupported(version, "1.7.10", "1.12.1")) {
            StaticMethods.log("&cThis version of Minecraft has not been tested with this version of EnderPads. Support cannot be given if there are errors. Avoid using this in production. An update is already likely underway and will release soon.");
        }

        if (version.equals("1.12")) {
            StaticMethods.log("&c[PSA]: &eThere is a game-breaking bug in 1.12 with the crafting guide. Players can DUPLICATE items effortlessly. Consider updating your server NOW.");
        }

        Settings.setupDefaults();
        Compatibility.checkHooks();

        registerListeners();
        setExecutors();

        Effects.effectManager = new EffectManager(plugin);
        Effects.addAll();

        long finish = System.currentTimeMillis();

        StaticMethods.log("&aEnderPads v" + plugin.getDescription().getVersion() + " is now loaded. &e(" + (finish - start) + "ms)");

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

        // BlockExplodeEvent was added in 1.8. We still want to support 1.7.10.
        if (Compatibility.isSupported(version, "1.8", "2.0")) {
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
}