package net.doodcraft.oshcon.bukkit.enderpads;

import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.effect.WarpEffect;
import de.slikey.effectlib.util.ParticleEffect;
import net.doodcraft.oshcon.bukkit.enderpads.api.*;
import net.doodcraft.oshcon.bukkit.enderpads.config.Settings;
import net.doodcraft.oshcon.bukkit.enderpads.util.Compatibility;
import net.doodcraft.oshcon.bukkit.enderpads.util.StaticMethods;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class Effects implements Listener {

    public static EffectManager effectManager;
    public static Map<String, Integer> idleTasks = new HashMap<>();

    public static boolean NOFIX_1710 = true;

    public static void addAll() {

        for (EnderPad enderPad : EnderPadsPlugin.enderPads.values()) {
            startPoofEffect(enderPad);
        }
    }

    public static void startPoofEffect(EnderPad enderPad) {

        if (!idleTasks.containsKey(enderPad.getPadId())) {

            final Location loc = enderPad.getLocation().add(0.5, 1.15, 0.5);

            int task = Bukkit.getScheduler().scheduleSyncRepeatingTask(EnderPadsPlugin.plugin, new Runnable() {
                @Override
                public void run() {
                    ParticleEffect.PORTAL.display(0, 0, 0, 0.5F, 1, loc, 64);
                }
            }, 10L, 10L);

            idleTasks.put(enderPad.getPadId(), task);
        }
    }

    public static void stopPoofEffect(EnderPad enderPad) {

        if (idleTasks.containsKey(enderPad.getPadId())) {

            Bukkit.getScheduler().cancelTask(idleTasks.get(enderPad.getPadId()));
            idleTasks.remove(enderPad.getPadId());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEffectUse(final EnderPadUseEvent event) {

        if (event.getEntity() instanceof Player) {

            Player player = (Player) event.getEntity();

            if (net.doodcraft.oshcon.bukkit.enderpads.util.StaticMethods.isVanished(player)) {
                return;
            }

            if (Settings.lightningUse) {

                if (!StaticMethods.isVanished(player)) {
                    Location loc = event.getDestinationEnderPad().getLocation();
                    loc.getWorld().strikeLightningEffect(loc);
                }
            }

            if (Settings.potionEffectsEnabled) {

                if (Compatibility.isSupported(EnderPadsPlugin.version, "1.8", "2.0")) {

                    for (String string : Settings.potionEffects) {

                        String args[] = string.split("-");

                        try {
                            PotionEffect effect = new PotionEffect(PotionEffectType.getByName(args[0].toUpperCase()), Integer.valueOf(args[1]) * 20, Integer.valueOf(args[2]), Boolean.valueOf(args[3]), Boolean.valueOf(args[4]));
                            player.addPotionEffect(effect);
                        } catch (Exception ex) {
                            StaticMethods.log("Error applying potion effect: " + args[0]);
                            StaticMethods.log("Check your configuration for errors.");
                        }
                    }

                } else {

                    // todo: Create 1.7.10 add-on addressing incompatibilities
                    // When this problem is addressed, simply set NOFIX_1710 to false
                    if (NOFIX_1710) {
                        StaticMethods.log("Sorry, PotionEffects are disabled for your Minecraft version. [Compatible: 1.8 and onward]");
                        Settings.potionEffectsEnabled = false;
                    }
                }
            }

            if (Settings.warpParticlesOrigin) {
                WarpEffect warpEffect = new WarpEffect(effectManager);
                warpEffect.particle = ParticleEffect.SPELL_WITCH;
                warpEffect.radius = 0.72F;
                warpEffect.particles = 8;
                warpEffect.rings = 1;
                warpEffect.setLocation(event.getOriginEnderPad().getLocation().add(0.5, 1, 0.5));
                warpEffect.start();
            }

            if (Settings.warpParticlesDestination) {
                WarpEffect warpEffect = new WarpEffect(effectManager);
                warpEffect.particle = ParticleEffect.SPELL_WITCH;
                warpEffect.radius = 0.72F;
                warpEffect.particles = 8;
                warpEffect.rings = 1;
                warpEffect.setLocation(event.getDestinationEnderPad().getLocation().add(0.5, 1, 0.5));
                warpEffect.start();
            }

            final Location from = event.getOriginEnderPad().getLocation();
            final Location to = event.getDestinationEnderPad().getLocation();

            if (Settings.soundsFrom) {

                final String sound[] = Settings.soundFrom.split("-");

                try {
                    Bukkit.getScheduler().runTaskLater(EnderPadsPlugin.plugin, new Runnable() {
                        @Override
                        public void run() {
                            from.getWorld().playSound(from, Sound.valueOf(sound[0]), Float.valueOf(sound[1]), Float.valueOf(sound[2]));
                        }
                    }, 1L);
                } catch (Exception ex) {
                    StaticMethods.log("&cThere was an error getting the from sound in your config.");
                    StaticMethods.log(ex.getLocalizedMessage());
                }
            }

            if (Settings.soundsTo) {

                final String sound[] = Settings.soundTo.split("-");

                try {
                    Bukkit.getScheduler().runTaskLater(EnderPadsPlugin.plugin, new Runnable() {
                        @Override
                        public void run() {
                            to.getWorld().playSound(to, Sound.valueOf(sound[0]), Float.valueOf(sound[1]), Float.valueOf(sound[2]));
                        }
                    }, 5L);
                } catch (Exception ex) {
                    StaticMethods.log("&cThere was an error getting the to sound in your config.");
                    StaticMethods.log(ex.getLocalizedMessage());
                }
            }
        } else {
            // todo: effects for entities other than players
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onAdd(AddToMemoryEvent event) {

        EnderPad enderPad = event.getEnderPad();
        startPoofEffect(enderPad);

         if (enderPad.isValid() && enderPad.isSaved()) {

            if (Settings.lightningCreate) {

                if (!StaticMethods.isVanished(Bukkit.getPlayer(event.getEnderPad().getOwnerUUID()))) {
                    Location loc = event.getEnderPad().getLocation();
                    loc.getWorld().strikeLightningEffect(loc);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onRemove(RemoveFromMemoryEvent event) {

        EnderPad enderPad = event.getEnderPad();
        stopPoofEffect(enderPad);
    }

    @EventHandler(ignoreCancelled = true)
    public void onDestroy(EnderPadDestroyEvent event) {

        if (event.getEnderPad().isValid()) {

            if (Settings.lightningDestroy) {

                if (event.hasPlayer()) {

                    if (StaticMethods.isVanished(event.getPlayer())) {
                        return;
                    }
                }

                Location loc = event.getEnderPad().getLocation();
                loc.getWorld().strikeLightningEffect(loc);
            }
        }
    }
}