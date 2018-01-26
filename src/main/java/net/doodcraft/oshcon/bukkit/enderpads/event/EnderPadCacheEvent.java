package net.doodcraft.oshcon.bukkit.enderpads.event;

import net.doodcraft.oshcon.bukkit.enderpads.enderpad.EnderPad;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EnderPadCacheEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private EnderPad enderPad;
    private boolean adding;
    private boolean effects;
    private Player creator;
    private Player destroyer;

    public EnderPadCacheEvent(EnderPad enderPad, boolean adding) {
        this.enderPad = enderPad;
        this.adding = adding;
        this.effects = true;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public EnderPad getEnderPad() {
        return this.enderPad;
    }

    public boolean isAdding() {
        return this.adding;
    }

    public void setAdding(boolean adding) {
        this.adding = adding;
    }

    public boolean isRemoving() {
        return !this.adding;
    }

    public void setRemoving(boolean removing) {
        this.adding = !removing;
    }

    public boolean hasEffects() {
        return this.effects;
    }

    public void setEffects(boolean effects) {
        this.effects = effects;
    }

    public boolean hasPlayer() {
        return this.creator != null || this.destroyer != null;
    }

    public boolean hasCreator() {
        return this.creator != null;
    }

    public Player getCreator() {
        return this.creator;
    }

    public void setCreator(Player creator) {
        this.creator = creator;
    }

    public boolean hasDestroyer() {
        return this.destroyer != null;
    }

    public Player getDestroyer() {
        return this.destroyer;
    }

    public void setDestroyer(Player destroyer) {
        this.destroyer = destroyer;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}