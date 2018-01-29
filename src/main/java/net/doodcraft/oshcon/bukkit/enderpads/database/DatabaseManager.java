package net.doodcraft.oshcon.bukkit.enderpads.database;

import net.doodcraft.oshcon.bukkit.enderpads.PadsPlugin;
import net.doodcraft.oshcon.bukkit.enderpads.config.Configuration;
import net.doodcraft.oshcon.bukkit.enderpads.enderpad.EnderPad;
import net.doodcraft.oshcon.bukkit.enderpads.enderpad.SmallLocation;
import net.doodcraft.oshcon.bukkit.enderpads.util.NumberConverter;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

public class DatabaseManager {

    private DatabaseType currentType;
    private Configuration flat;

    public DatabaseManager(DatabaseType type) {
        this.currentType = type;
        if (type == DatabaseType.FLATFILE) {
            flat = new Configuration(PadsPlugin.plugin.getDataFolder() + File.separator + "database" + File.separator + "flat.yml");
        }
    }

    public void save(EnderPad pad) {
        if (this.currentType == DatabaseType.FLATFILE) {
            String loc = pad.getSmallLocation().toString();
            flat.add(loc + ".Link", pad.getCurrentLink().toString());
            flat.add(loc + ".Owner", pad.getOwnerUUID().toString());
            flat.save();
        }
    }

    public void delete(EnderPad pad) {
        if (this.currentType == DatabaseType.FLATFILE) {
            flat.remove(pad.getSmallLocation().toString());
            flat.save();
        }
    }

    public boolean isSaved(EnderPad pad) {
        if (this.currentType == DatabaseType.FLATFILE) {
            if (flat.contains(pad.getSmallLocation().toString())) {
                return true;
            }
        }
        return false;
    }

    public boolean migrate(DatabaseType type) {
        // todo
        if (this.currentType == DatabaseType.FLATFILE) {

        }
        return false;
    }

    public ArrayList<EnderPad> getStoredPads() {
        if (this.currentType == DatabaseType.FLATFILE) {
            ArrayList<EnderPad> pads = new ArrayList<>();
            for (String l : flat.getKeys(false)) {
                EnderPad pad = new EnderPad(SmallLocation.fromString(l, ", "), UUID.fromString(flat.getString(l + ".Owner")));
                pads.add(pad);
            }
            return pads;
        }
        return null;
    }

    public static void updateFrom030() {
        PadsPlugin.logger.log("Importing old EnderPad data...");
        PadsPlugin.database = new DatabaseManager(DatabaseType.FLATFILE);
        Configuration old = new Configuration(PadsPlugin.plugin.getDataFolder() + File.separator + "data" + File.separator + "pads.yml");
        int count = 0;
        for (String k : old.getKeys(false)) {
            String loc = k.replaceAll(" ", ", ");
            EnderPad pad = new EnderPad(SmallLocation.fromString(loc, ", "), UUID.fromString(old.getString(k + "Owner.UUID")));
            pad.save(null);
            count++;
        }
        if (count > 0) {
            PadsPlugin.logger.log("Successfully imported " + NumberConverter.convert(count) + " EnderPads to the new database!");
        } else {
            PadsPlugin.logger.log("Couldn't find any EnderPads to import!");
        }
    }
}