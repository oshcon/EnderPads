package net.doodcraft.oshcon.bukkit.enderpads.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

public class ConfigurationReloadEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Plugin plugin;
    private boolean cancelled;

    public ConfigurationReloadEvent(Plugin plugin) {
        this.plugin = plugin;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}