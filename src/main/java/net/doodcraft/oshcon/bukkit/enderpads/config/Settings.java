package net.doodcraft.oshcon.bukkit.enderpads.config;

import net.doodcraft.oshcon.bukkit.enderpads.EnderPadsPlugin;
import net.doodcraft.oshcon.bukkit.enderpads.api.EnderPadAPI;
import net.doodcraft.oshcon.bukkit.enderpads.util.StaticMethods;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Settings
{
    public static String version;
    public static Boolean colorfulLogging;
    public static Boolean debug;
    public static Boolean logUse;
    public static int defaultMax;
    public static String centerMaterial;
    public static List<String> blackListedBlocks;
    public static List<String> blackListedWorlds;
    public static Boolean safeTeleport;
    public static int playerCooldown;
    public static Boolean lightningCreate;
    public static Boolean lightningDestroy;
    public static Boolean lightningUse;

    public static String pluginPrefix;
    public static String noPermission;
    public static String reloadSuccess;
    public static String reloadFailed;
    public static String atMaximum;
    public static String cooldownMessage;
    public static String enderPad;
    public static String enderPadCreated;
    public static String enderPadRemoved;
    public static String created;
    public static String destroyed;
    public static String destroyedMisc;
    public static String destroyedPlayer;
    public static String links;
    public static String usage;
    public static String owner;
    public static String online;
    public static String offline;

    public static void setupDefaults()
    {
        colorfulLogging = true;
        debug = false;
        logUse = true;
        defaultMax = 6;
        centerMaterial = "OBSIDIAN~0";
        blackListedBlocks = new ArrayList<>();
        blackListedBlocks.add("GRASS");
        blackListedBlocks.add("DIRT");
        blackListedWorlds = new ArrayList<>();
        blackListedWorlds.add("disabled_world_name");
        blackListedWorlds.add("another_disabled_world");
        safeTeleport = true;
        playerCooldown = 6;
        lightningCreate = true;
        lightningDestroy = true;
        lightningUse = false;

        pluginPrefix = "&8[&5EnderPads&8]&r";
        noPermission = "<prefix> &cNo permission.";
        reloadSuccess = "<prefix> &aPlugin reloaded!";
        reloadFailed = "<prefix> &cThere was an error.";
        atMaximum = "<prefix> &cYou cannot build any more.";
        cooldownMessage = "&cPlease wait... &e[<remaining>ms]";
        enderPad = "&8:: &3EnderPad: &7[<padid>]";
        enderPadCreated = "&8:: &3EnderPad: &7[<padid>] &a(new)";
        enderPadRemoved = "&8:: &3EnderPad: &7[<padid>] &c(removed)";
        created = "&8:: &aCreated &7[<padid>]";
        destroyed = "&8:: &cDestroyed &7[<padid>]";
        destroyedMisc = "&8:: &cSomething destroyed &7[<padid>]";
        destroyedPlayer = "&8:: &cSomeone destroyed &7[<padid>]";
        links = "&8- &3Links&8: &e<links>";
        usage = "&8- &3Limit: &e<usage>&7/&6<max>";
        owner = "&8- &3Owner&8: &e<owner> <status>";
        online = "&a(online)";
        offline = "&c(offline)";

        Configuration config = new Configuration(EnderPadsPlugin.plugin.getDataFolder() + File.separator + "config.yml");
        Configuration locale = new Configuration(EnderPadsPlugin.plugin.getDataFolder() + File.separator + "locale.yml");

        config.add("General.ColorfulLogging", colorfulLogging);
        config.add("General.DebugMessages", debug);
        config.add("LogUse", logUse);
        config.add("Cooldown", playerCooldown);
        config.add("SkipBlockedPads", safeTeleport);
        config.add("DefaultMax", defaultMax);
        config.add("CenterMaterial", centerMaterial);
        config.add("Blacklist.Materials", blackListedBlocks);
        config.add("Blacklist.Worlds", blackListedWorlds);
        config.add("Effects.Lightning.OnCreate", lightningCreate);
        config.add("Effects.Lightning.OnDestroy", lightningDestroy);
        config.add("Effects.Lightning.OnUse", lightningUse);

        locale.add("General.PluginPrefix", pluginPrefix);
        locale.add("General.NoPermission", noPermission);
        locale.add("General.Reload.Success", reloadSuccess);
        locale.add("General.Reload.Failed", reloadFailed);
        locale.add("AtMaximum", atMaximum);
        locale.add("Cooldown", cooldownMessage);
        locale.add("EnderPad.PadId.Default", enderPad);
        locale.add("EnderPad.PadId.Created", enderPadCreated);
        locale.add("EnderPad.PadId.Removed", enderPadRemoved);
        locale.add("EnderPad.Created", created);
        locale.add("EnderPad.Destroyed.BySelf", destroyed);
        locale.add("EnderPad.Destroyed.ByMisc", destroyedMisc);
        locale.add("EnderPad.Destroyed.ByPlayer", destroyedPlayer);
        locale.add("EnderPad.Info.Links", links);
        locale.add("EnderPad.Info.Usage", usage);
        locale.add("EnderPad.Info.Owner", owner);
        locale.add("Variables.Online", online);
        locale.add("Variables.Offline", offline);

        config.save();
        locale.save();

        setNewConfigValues(config);
        setNewLocaleValues(locale);

        update();
    }

    private static void setNewConfigValues(Configuration config)
    {
        version = config.getString("General.Version");
        colorfulLogging = config.getBoolean("General.ColorfulLogging");
        debug = config.getBoolean("General.DebugMessages");
        logUse = config.getBoolean("LogUse");
        playerCooldown = config.getInteger("Cooldown");
        safeTeleport = config.getBoolean("SkipBlockedPads");
        defaultMax = config.getInteger("DefaultMax");
        centerMaterial = config.getString("CenterMaterial");
        blackListedBlocks = config.getStringList("Blacklist.Materials");
        blackListedWorlds = config.getStringList("Blacklist.Worlds");
        lightningCreate = config.getBoolean("Effects.Lightning.OnCreate");
        lightningDestroy = config.getBoolean("Effects.Lightning.OnDestroy");
        lightningUse = config.getBoolean("Effects.Lightning.OnUse");
    }

    private static void setNewLocaleValues(Configuration locale)
    {
        pluginPrefix = locale.getString("General.PluginPrefix");
        noPermission = locale.getString("General.NoPermission");
        reloadSuccess = locale.getString("General.Reload.Success");
        reloadFailed = locale.getString("General.Reload.Failed");
        atMaximum = locale.getString("AtMaximum");
        cooldownMessage = locale.getString("Cooldown");
        enderPad = locale.getString("EnderPad.PadId.Default");
        enderPadCreated = locale.getString("EnderPad.PadId.Created");
        enderPadRemoved = locale.getString("EnderPad.PadId.Removed");
        created = locale.getString("EnderPad.Created");
        destroyed = locale.getString("EnderPad.Destroyed.BySelf");
        destroyedMisc = locale.getString("EnderPad.Destroyed.ByMisc");
        destroyedPlayer = locale.getString("EnderPad.Destroyed.ByPlayer");
        links = locale.getString("EnderPad.Info.Links");
        usage = locale.getString("EnderPad.Info.Usage");
        owner = locale.getString("EnderPad.Info.Owner");
        online = locale.getString("Variables.Online");
        offline = locale.getString("Variables.Offline");
    }

    public static boolean reload()
    {
        ConfigurationReloadEvent event = new ConfigurationReloadEvent(EnderPadsPlugin.plugin);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled())
        {
            try
            {
                Configuration config = new Configuration(EnderPadsPlugin.plugin.getDataFolder() + File.separator + "config.yml");
                Configuration locale = new Configuration(EnderPadsPlugin.plugin.getDataFolder() + File.separator + "locale.yml");

                setNewConfigValues(config);
                setNewLocaleValues(locale);

                EnderPadAPI.verifyAllTelepads();
                return false;
            } catch (Exception ex)
            {
                ex.printStackTrace();
                return true;
            }
        }
        else
        {
            StaticMethods.debug("ConfigurationReloadEvent was cancelled.");
            return false;
        }
    }

    private static void update()
    {
        String version = EnderPadsPlugin.plugin.getDescription().getVersion();

        Configuration config = new Configuration(EnderPadsPlugin.plugin.getDataFolder() + File.separator + "config.yml");
        Configuration locale = new Configuration(EnderPadsPlugin.plugin.getDataFolder() + File.separator + "locale.yml");

        // 0.3.2-beta, the first config update. Check if General.Version is null to determine if it is needed.
        if (config.getString("General.Version") == null)
        {
            StaticMethods.log("&eLocale changes have been made in 0.3.2-beta requiring an update to locale.yml.");
            StaticMethods.log("&eThese changes will be applied now.");
            try
            {
                noPermission = locale.getString("General.NoPermission");
                atMaximum = locale.getString("AtMaximum");

                if (!locale.getString("General.NoPermission").toLowerCase().contains("<prefix>"))
                {
                    locale.set("General.NoPermission", "<prefix> " + locale.getString("General.NoPermission"));
                }

                if (!locale.getString("AtMaximum").toLowerCase().contains("<prefix>"))
                {
                    locale.set("AtMaximum", "<prefix> " + locale.getString("AtMaximum"));
                }

                locale.save();

                config.add("General.Version", "0.3.2-beta");
                config.save();

                setNewLocaleValues(locale);
                setNewConfigValues(config);
            } catch (Exception ex)
            {
                ex.printStackTrace();
                StaticMethods.log("&cThere was an error updating your locale to reflect the 0.3.2-beta changes.");
                StaticMethods.log("&cIf possible, create backups, delete your locale.yml file, then restart.");
            }
        }

        if (!config.getString("General.Version").equals(version))
        {
            // Post 0.3.2-beta updates will be performed here.
            config.set("General.Version", version);
            config.save();

            setNewConfigValues(config);
        }
    }
}