package net.doodcraft.oshcon.bukkit.enderpads.api;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AddToMemoryEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private EnderPad enderPad;

    public AddToMemoryEvent(EnderPad enderPad) {
        this.enderPad = enderPad;
    }

    public EnderPad getEnderPad() {
        return this.enderPad;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}