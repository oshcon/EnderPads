package net.doodcraft.oshcon.bukkit.enderpads.api;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EnderPadUseEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private Entity entity;
    private EnderPad originPad;
    private EnderPad destPad;
    private boolean cancelled;

    public EnderPadUseEvent(EnderPad originPad, EnderPad destPad, Entity entity) {
        this.entity = entity;
        this.originPad = originPad;
        this.destPad = destPad;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public EnderPad getOriginEnderPad() {
        return this.originPad;
    }

    public EnderPad getDestinationEnderPad() {
        return this.destPad;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}