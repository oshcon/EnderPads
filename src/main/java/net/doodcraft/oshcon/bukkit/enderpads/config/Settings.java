package net.doodcraft.oshcon.bukkit.enderpads.config;

import net.doodcraft.oshcon.bukkit.enderpads.EnderPadsPlugin;
import net.doodcraft.oshcon.bukkit.enderpads.api.EnderPadAPI;
import net.doodcraft.oshcon.bukkit.enderpads.util.Compatibility;
import net.doodcraft.oshcon.bukkit.enderpads.util.StaticMethods;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Settings {

    public static String version;
    public static Boolean colorfulLogging;
    public static Boolean debug;
    public static boolean enablePsas;
    public static Boolean logUse;
    public static int defaultMax;
    public static String centerMaterial;
    public static List<String> blackListedBlocks;
    public static List<String> blackListedWorlds;
    public static Boolean safeTeleport;
    public static int playerCooldown;
    public static boolean teleportMobs;
    public static boolean teleportItems;

    public static Boolean lightningCreate;
    public static Boolean lightningDestroy;
    public static Boolean lightningUse;
    public static Boolean idleParticles;
    public static Boolean potionEffectsEnabled;
    public static List<String> potionEffects;
    public static Boolean warpParticlesOrigin;
    public static Boolean warpParticlesDestination;
    public static Boolean soundsFrom;
    public static Boolean soundsTo;
    public static String soundFrom;
    public static String soundTo;

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

    public static String numbersNegative;
    public static String numbersHundred;
    public static String numbersThousand;
    public static String numbersMillion;
    public static String numbersBillion;
    public static String numbersZero;
    public static String numbersOne;
    public static String numbersTwo;
    public static String numbersThree;
    public static String numbersFour;
    public static String numbersFive;
    public static String numbersSix;
    public static String numbersSeven;
    public static String numbersEight;
    public static String numbersNine;
    public static String numbersTen;
    public static String numbersEleven;
    public static String numbersTwelve;
    public static String numbersThirteen;
    public static String numbersFourteen;
    public static String numbersFifteen;
    public static String numbersSixteen;
    public static String numbersSeventeen;
    public static String numbersEighteen;
    public static String numbersNineteen;
    public static String numbersTwenty;
    public static String numbersThirty;
    public static String numbersForty;
    public static String numbersFifty;
    public static String numbersSixty;
    public static String numbersSeventy;
    public static String numbersEighty;
    public static String numbersNinety;

    public static void setupDefaults() {
        colorfulLogging = true;
        debug = false;
        logUse = true;
        enablePsas = true;
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
        teleportMobs = true;
        teleportItems = true;

        lightningCreate = true;
        lightningDestroy = true;
        lightningUse = false;
        idleParticles = true;
        potionEffectsEnabled = true;
        potionEffects = new ArrayList<>();
        potionEffects.add("CONFUSION-6-1-false-false");
        warpParticlesOrigin = true;
        warpParticlesDestination = true;
        soundsFrom = true;
        soundsTo = true;

        if (Compatibility.isSupported(EnderPadsPlugin.version, "1.9", "2.0")) {
            soundFrom = "ENTITY_ENDERMEN_TELEPORT-1-1.35";
            soundTo = "ENTITY_ENDERMEN_TELEPORT-1-1.45";
        } else {
            soundFrom = "ENDERMAN_TELEPORT-1-1.35";
            soundTo = "ENDERMAN_TELEPORT-1-1.45";
        }

        pluginPrefix = "&8[&5EnderPads&8]&r";
        noPermission = "<prefix> &cNo permission.";
        reloadSuccess = "<prefix> &aPlugin reloaded!";
        reloadFailed = "<prefix> &cThere was an error.";
        atMaximum = "<prefix> &cYou cannot build any more.";
        cooldownMessage = "&cPlease wait... &8[&e<remaining>ms&8]";
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

        numbersNegative = "negative";
        numbersHundred = "hundred";
        numbersThousand = "thousand";
        numbersMillion = "million";
        numbersBillion = "billion";
        numbersZero = "zero";
        numbersOne = "one";
        numbersTwo = "two";
        numbersThree = "three";
        numbersFour = "four";
        numbersFive = "five";
        numbersSix = "six";
        numbersSeven = "seven";
        numbersEight = "eight";
        numbersNine = "nine";
        numbersTen = "ten";
        numbersEleven = "eleven";
        numbersTwelve = "twelve";
        numbersThirteen = "thirteen";
        numbersFourteen = "fourteen";
        numbersFifteen = "fifteen";
        numbersSixteen = "sixteen";
        numbersSeventeen = "seventeen";
        numbersEighteen = "eighteen";
        numbersNineteen = "nineteen";
        numbersTwenty = "twenty";
        numbersThirty = "thirty";
        numbersForty = "forty";
        numbersFifty = "fifty";
        numbersSixty = "sixty";
        numbersSeventy = "seventy";
        numbersEighty = "eighty";
        numbersNinety = "ninety";

        Configuration config = new Configuration(EnderPadsPlugin.plugin.getDataFolder() + File.separator + "config.yml");
        Configuration locale = new Configuration(EnderPadsPlugin.plugin.getDataFolder() + File.separator + "locale.yml");

        config.add("General.ColorfulLogging", colorfulLogging);
        config.add("General.DebugMessages", debug);
        config.add("General.PublicServiceAnnouncements", enablePsas);
        config.add("LogUse", logUse);
        config.add("Cooldown", playerCooldown);
        config.add("SkipBlockedPads", safeTeleport);
        config.add("DefaultMax", defaultMax);
        config.add("CenterMaterial", centerMaterial);
        config.add("Blacklist.Materials", blackListedBlocks);
        config.add("Blacklist.Worlds", blackListedWorlds);
        config.add("AllowEntities.Mobs", teleportMobs);
        config.add("AllowEntities.Items", teleportItems);

        config.add("Effects.Lightning.OnCreate", lightningCreate);
        config.add("Effects.Lightning.OnDestroy", lightningDestroy);
        config.add("Effects.Lightning.OnUse", lightningUse);
        config.add("Effects.IdleParticles", idleParticles);
        config.add("Effects.PotionEffects.Enabled", potionEffectsEnabled);
        config.add("Effects.PotionEffects.List", potionEffects);
        config.add("Effects.OnUse.Origin", warpParticlesOrigin);
        config.add("Effects.OnUse.Destination", warpParticlesDestination);
        config.add("Effects.Sounds.From.Enabled", soundsFrom);
        config.add("Effects.Sounds.From.Sound", soundFrom);
        config.add("Effects.Sounds.To.Enabled", soundsTo);
        config.add("Effects.Sounds.To.Sound", soundTo);

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

        locale.add("Numbers.Negative", numbersNegative);
        locale.add("Numbers.Hundred", numbersHundred);
        locale.add("Numbers.Thousand", numbersThousand);
        locale.add("Numbers.Million", numbersMillion);
        locale.add("Numbers.Billion", numbersBillion);
        locale.add("Numbers.Zero", numbersZero);
        locale.add("Numbers.One", numbersOne);
        locale.add("Numbers.Two", numbersTwo);
        locale.add("Numbers.Three", numbersThree);
        locale.add("Numbers.Four", numbersFour);
        locale.add("Numbers.Five", numbersFive);
        locale.add("Numbers.Six", numbersSix);
        locale.add("Numbers.Seven", numbersSeven);
        locale.add("Numbers.Eight", numbersEight);
        locale.add("Numbers.Nine", numbersNine);
        locale.add("Numbers.Ten", numbersTen);
        locale.add("Numbers.Eleven", numbersEleven);
        locale.add("Numbers.Twelve", numbersTwelve);
        locale.add("Numbers.Thirteen", numbersThirteen);
        locale.add("Numbers.Fourteen", numbersFourteen);
        locale.add("Numbers.Fifteen", numbersFifteen);
        locale.add("Numbers.Sixteen", numbersSixteen);
        locale.add("Numbers.Seventeen", numbersSeventeen);
        locale.add("Numbers.Eighteen", numbersEighteen);
        locale.add("Numbers.Nineteen", numbersNineteen);
        locale.add("Numbers.Twenty", numbersTwenty);
        locale.add("Numbers.Thirty", numbersThirty);
        locale.add("Numbers.Forty", numbersForty);
        locale.add("Numbers.Fifty", numbersFifty);
        locale.add("Numbers.Sixty", numbersSixty);
        locale.add("Numbers.Seventeen", numbersSeventeen);
        locale.add("Numbers.Eighteen", numbersEighteen);
        locale.add("Numbers.Nineteen", numbersNineteen);

        config.save();
        locale.save();

        setNewConfigValues(config);
        setNewLocaleValues(locale);

        update();
    }

    private static void setNewConfigValues(Configuration config) {
        version = config.getString("General.Version");
        colorfulLogging = config.getBoolean("General.ColorfulLogging");
        debug = config.getBoolean("General.DebugMessages");
        enablePsas = config.getBoolean("General.PublicServiceAnnouncements");
        logUse = config.getBoolean("LogUse");
        playerCooldown = config.getInteger("Cooldown");
        safeTeleport = config.getBoolean("SkipBlockedPads");
        defaultMax = config.getInteger("DefaultMax");
        centerMaterial = config.getString("CenterMaterial");
        blackListedBlocks = config.getStringList("Blacklist.Materials");
        blackListedWorlds = config.getStringList("Blacklist.Worlds");
        teleportMobs = config.getBoolean("AllowEntities.Mobs");
        teleportItems = config.getBoolean("AllowEntities.Items");

        lightningCreate = config.getBoolean("Effects.Lightning.OnCreate");
        lightningDestroy = config.getBoolean("Effects.Lightning.OnDestroy");
        lightningUse = config.getBoolean("Effects.Lightning.OnUse");
        idleParticles = config.getBoolean("Effects.IdleParticles");
        potionEffectsEnabled = config.getBoolean("Effects.PotionEffects.Enabled");
        potionEffects = config.getStringList("Effects.PotionEffects.List");
        warpParticlesOrigin = config.getBoolean("Effects.OnUse.Origin");
        warpParticlesDestination = config.getBoolean("Effects.OnUse.Destination");
        soundsFrom = config.getBoolean("Effects.Sounds.From.Enabled");
        soundFrom = config.getString("Effects.Sounds.From.Sound");
        soundsTo = config.getBoolean("Effects.Sounds.To.Enabled");
        soundTo = config.getString("Effects.Sounds.To.Sound");
    }

    private static void setNewLocaleValues(Configuration locale) {
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

        numbersNegative = locale.getString("Numbers.Negative");
        numbersHundred = locale.getString("Numbers.Hundred");
        numbersThousand = locale.getString("Numbers.Thousand");
        numbersMillion = locale.getString("Numbers.Million");
        numbersBillion = locale.getString("Numbers.Billion");
        numbersZero = locale.getString("Numbers.Zero");
        numbersOne = locale.getString("Numbers.One");
        numbersTwo = locale.getString("Numbers.Two");
        numbersThree = locale.getString("Numbers.Three");
        numbersFour = locale.getString("Numbers.Four");
        numbersFive = locale.getString("Numbers.Five");
        numbersSix = locale.getString("Numbers.Six");
        numbersSeven = locale.getString("Numbers.Seven");
        numbersEight = locale.getString("Numbers.Eight");
        numbersNine = locale.getString("Numbers.Nine");
        numbersTen = locale.getString("Numbers.Ten");
        numbersEleven = locale.getString("Numbers.Eleven");
        numbersTwelve = locale.getString("Numbers.Twelve");
        numbersThirteen = locale.getString("Numbers.Thirteen");
        numbersFourteen = locale.getString("Numbers.Fourteen");
        numbersFifteen = locale.getString("Numbers.Fifteen");
        numbersSixteen = locale.getString("Numbers.Sixteen");
        numbersEighteen = locale.getString("Numbers.Eighteen");
        numbersNineteen = locale.getString("Numbers.Nineteen");
        numbersTwenty = locale.getString("Numbers.Twenty");
        numbersThirty = locale.getString("Numbers.Thirty");
        numbersForty = locale.getString("Numbers.Forty");
        numbersFifty = locale.getString("Numbers.Fifty");
        numbersSixty = locale.getString("Numbers.Sixty");
        numbersSeventy = locale.getString("Numbers.Seventy");
        numbersEighty = locale.getString("Numbers.Eighty");
        numbersNinety = locale.getString("Numbers.Ninety");
    }

    public static boolean reload() {

        ConfigurationReloadEvent event = new ConfigurationReloadEvent(EnderPadsPlugin.plugin);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {

            try {
                Configuration config = new Configuration(EnderPadsPlugin.plugin.getDataFolder() + File.separator + "config.yml");
                Configuration locale = new Configuration(EnderPadsPlugin.plugin.getDataFolder() + File.separator + "locale.yml");

                setNewConfigValues(config);
                setNewLocaleValues(locale);

                EnderPadAPI.verifyAllTelepads();
                return false;
            } catch (Exception ex) {
                ex.printStackTrace();
                return true;
            }

        } else {

            StaticMethods.debug("ConfigurationReloadEvent was cancelled.");
            return false;
        }
    }

    private static void update() {

        String version = EnderPadsPlugin.plugin.getDescription().getVersion();

        Configuration config = new Configuration(EnderPadsPlugin.plugin.getDataFolder() + File.separator + "config.yml");
        Configuration locale = new Configuration(EnderPadsPlugin.plugin.getDataFolder() + File.separator + "locale.yml");

        // 0.3.2-beta, the first config update. Check if General.Version is null to determine if it is needed.
        if (config.getString("General.Version") == null) {

            try {
                noPermission = locale.getString("General.NoPermission");
                atMaximum = locale.getString("AtMaximum");

                if (!locale.getString("General.NoPermission").toLowerCase().contains("<prefix>")) {
                    locale.set("General.NoPermission", "<prefix> " + locale.getString("General.NoPermission"));
                }

                if (!locale.getString("AtMaximum").toLowerCase().contains("<prefix>")) {
                    locale.set("AtMaximum", "<prefix> " + locale.getString("AtMaximum"));
                }

                locale.save();

                config.add("General.Version", version);
                config.save();

                setNewLocaleValues(locale);
                setNewConfigValues(config);
            } catch (Exception ex) {
                ex.printStackTrace();
                StaticMethods.log("&cThere was an error updating your locale to reflect the 0.3.2-beta changes.");
                StaticMethods.log("&cIf possible, create backups, delete your locale.yml file, then restart.");
            }
        }

        if (!config.getString("General.Version").equals(version)) {

            // Post 0.3.2-beta updates will be performed here.
            // REMINDER:
            // Config/Locale updates changes should be kept to a minimum to decrease general confusion for users.
            config.set("General.Version", version);
            config.save();

            setNewConfigValues(config);
        }
    }
}