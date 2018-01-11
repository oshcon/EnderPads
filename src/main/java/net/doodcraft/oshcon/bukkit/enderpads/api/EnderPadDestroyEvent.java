package net.doodcraft.oshcon.bukkit.enderpads.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EnderPadDestroyEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private EnderPad enderPad;

    public EnderPadDestroyEvent(EnderPad enderPad, Player player) {
        this.player = player;
        this.enderPad = enderPad;
    }

    public Player getPlayer() {
        return this.player;
    }

    public EnderPad getEnderPad() {
        return this.enderPad;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public boolean hasPlayer() {
        return this.player != null;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}