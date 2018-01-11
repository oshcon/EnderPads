package net.doodcraft.oshcon.bukkit.enderpads.util;

import net.doodcraft.oshcon.bukkit.enderpads.api.EnderPad;
import net.doodcraft.oshcon.bukkit.enderpads.config.Settings;
import org.bukkit.entity.Player;

public class StringParser {

    public static String parse(String string, Player player, EnderPad enderPad, String num, Boolean spelledOut, Boolean online) {

        if (player != null) {
            string = string.replaceAll("<owner>", player.getName());
        }

        if (enderPad != null) {
            string = string.replaceAll("<padid>", enderPad.getPadId());
            string = string.replaceAll("<linkid>", enderPad.getLinkId());
        }

        if (!spelledOut) {
            string = string.replaceAll("<usage>", num);
            string = string.replaceAll("<links>", num);
        } else {
            string = string.replaceAll("<usage>", NumberConverter.convert(Integer.valueOf(num)));
            string = string.replaceAll("<links>", NumberConverter.convert(Integer.valueOf(num)));
        }

        if (online) {
            string = string.replaceAll("<status>", Settings.online);
        } else {
            string = string.replaceAll("<status>", Settings.offline);
        }

        string = string.replaceAll("<owner>", num);
        string = string.replaceAll("<prefix>", Settings.pluginPrefix);

        return StaticMethods.addColor(string);
    }
}