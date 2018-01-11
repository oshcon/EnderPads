package net.doodcraft.oshcon.bukkit.enderpads.util;

import net.doodcraft.oshcon.bukkit.enderpads.EnderPadsPlugin;
import net.doodcraft.oshcon.bukkit.enderpads.config.Settings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;

public class ReflectionUtil {

    public static boolean SUPPORT183_1112 = false;
    public static boolean SUPPORT112_1122 = false;

    public static void sendActionBar(Player player, String message) {

        // This should prevent

        if (!SUPPORT183_1112) {
            if (Compatibility.isSupported(EnderPadsPlugin.version, "1.8.3", "1.11.2")) {
                SUPPORT183_1112 = true;
            }
        }

        if (!SUPPORT112_1122) {
            if (Compatibility.isSupported(EnderPadsPlugin.version, "1.12", "1.12.2")) {
                SUPPORT112_1122 = true;
            }
        }

        if (SUPPORT183_1112) {
            ReflectionUtil.send18Actionbar(player, message);
            return;
        }

        if (SUPPORT112_1122) {
            ReflectionUtil.send112Actionbar(player, message);
            return;
        }

        // todo: pre 1.8.3 module to fix (add) cooldown messages
    }

    private static void send18Actionbar(Player player, String message) {

        try {
            Constructor<?> constructor = getNMSClass("PacketPlayOutChat").getConstructor(getNMSClass("IChatBaseComponent"), byte.class);
            Object icbc = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + message + "\"}");
            Object packet = constructor.newInstance(icbc, (byte) 2);
            Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (Exception ex) {
            player.sendMessage(StaticMethods.addColor(message));
            if (Settings.debug) {
                ex.printStackTrace();
            }
        }
    }

    private static void send112Actionbar(Player player, String message) {

        try {
            Constructor<?> constructor = getNMSClass("PacketPlayOutChat").getConstructor(getNMSClass("IChatBaseComponent"), getNMSClass("ChatMessageType"));
            Object icbc = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + message + "\"}");
            Object packet = constructor.newInstance(icbc, getNMSClass("ChatMessageType").getEnumConstants()[2]);
            Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (Exception ex) {
            player.sendMessage(StaticMethods.addColor(message));
            if (Settings.debug) {
                ex.printStackTrace();
            }
        }
    }

    private static Class<?> getNMSClass(String name) {

        try {
            return Class.forName("net.minecraft.server." + getVersion() + "." + name);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }
}