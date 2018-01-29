package net.doodcraft.oshcon.bukkit.enderpads;

import de.slikey.effectlib.EffectManager;
import net.doodcraft.oshcon.bukkit.enderpads.cache.EnderPadCache;
import net.doodcraft.oshcon.bukkit.enderpads.cache.NameCache;
import net.doodcraft.oshcon.bukkit.enderpads.cache.PermissionCache;
import net.doodcraft.oshcon.bukkit.enderpads.cache.UUIDCache;
import net.doodcraft.oshcon.bukkit.enderpads.command.EnderPadsCommand;
import net.doodcraft.oshcon.bukkit.enderpads.config.Settings;
import net.doodcraft.oshcon.bukkit.enderpads.database.DatabaseManager;
import net.doodcraft.oshcon.bukkit.enderpads.database.DatabaseType;
import net.doodcraft.oshcon.bukkit.enderpads.enderpad.EnderPadMethods;
import net.doodcraft.oshcon.bukkit.enderpads.listener.*;
import net.doodcraft.oshcon.bukkit.enderpads.util.Compatibility;
import net.doodcraft.oshcon.bukkit.enderpads.util.Effects;
import net.doodcraft.oshcon.bukkit.enderpads.util.Logger;
import org.bstats.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PadsPlugin extends JavaPlugin {

    public static Plugin plugin;
    public static String version;
    public static Random random;
    public static Logger logger;
    public static EnderPadCache padCache;
    public static NameCache nameCache;
    public static PermissionCache permissionCache;
    public static UUIDCache uuidCache;
    public static DatabaseManager database;
    public static Metrics metrics;

    public static Map<String, Long> playerCooldowns = new HashMap<>();
    public static Map<Integer, Long> entityCooldowns = new HashMap<>();

    public static BlockFace faces[] = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

    @Override
    public void onEnable() {
        plugin = this;
        version = Bukkit.getBukkitVersion().split("-")[0];
        random = new Random();
        logger = new Logger();
        padCache = new EnderPadCache();
        nameCache = new NameCache();
        permissionCache = new PermissionCache();
        uuidCache = new UUIDCache();
        Effects.effectManager = new EffectManager(plugin);
        Settings.setupDefaults();
        if (database == null) {
            database = new DatabaseManager(DatabaseType.valueOf((Settings.database)));
        }
        if (Settings.enablePsas) {
            boolean announced = false;
            if (!Compatibility.isSupported(version, "1.7.10", "1.12.2")) {
                logger.log("&c[PSA]: \n" +
                        "&c[PSA]: This version of Minecraft is untested with this version of EnderPads!\n" +
                        "&c[PSA]:   There are a few possibilities:\n" +
                        "&c[PSA]:     1.] There may be an error. Or two. Or thirty. Or none.\n" +
                        "&c[PSA]:     2.] The author has abandoned EnderPads! Oh no! O:\n" +
                        "&c[PSA]:     3.] Nuclear armageddon is upon us. :(\n" +
                        "&c[PSA]:     4.] You are using an outdated/incompatible version of Bukkit/Spigot/etc\n" +
                        "&c[PSA]:     5.] If you are using a newer version, the author is likely already working on an update!\n" +
                        "&c[PSA]: "
                );
                announced = true;
            }
            if (version.equals("1.12")) {
                logger.log("&c[PSA]: &eThere is a game-breaking bug in 1.12 with the crafting guide. Players can DUPLICATE items without being detected. Consider updating your server NOW.");
                announced = true;
            }
            if (announced) {
                logger.log("&c[PSA]: You can silence these messages by setting 'General.PublicServiceAnnouncements' in your config to 'false'");
            }
        }
        Compatibility.checkHooks();
        registerListeners();
        setExecutors();
        logger.log("&aEnderPads v" + plugin.getDescription().getVersion() + " is now loaded.");
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                EnderPadMethods.verifyAll();
            }
        }, 1L);
        metrics();
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(plugin);
        Effects.idleTasks.clear();
    }

    private void registerListeners() {
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

    private static void registerEvents(Plugin plugin, Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }

    private void setExecutors() {
        getCommand("enderpads").setExecutor(new EnderPadsCommand());
    }

    private void metrics() {
        try {
            metrics = new Metrics(this);
            metrics.addCustomChart(new Metrics.SingleLineChart("total_enderpads") {
                @Override
                public int getValue() {
                    return padCache.getCache().size();
                }
            });
            metrics.addCustomChart(new Metrics.SingleLineChart("total_combinations") {
                @Override
                public int getValue() {
                    return padCache.getLinks().size();
                }
            });
        } catch (Exception ex) {
            logger.log("&a[METRICS] &cThere was an error sending metrics to bStats.");
        }
    }
}