package net.doodcraft.oshcon.bukkit.enderpads.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EnderPadClickEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private EnderPad enderPad;
    private boolean cancelled;

    public EnderPadClickEvent(Player player, EnderPad enderPad)
    {
        this.player = player;
        this.enderPad = enderPad;
    }

    public Player getPlayer() {
        return this.player;
    }

    public EnderPad getEnderPad()
    {
        return this.enderPad;
    }

    public boolean isCancelled()
    {
        return cancelled;
    }

    public void setCancelled(boolean cancelled)
    {
        this.cancelled = cancelled;
    }

    public HandlerList getHandlers()
    {
        return handlers;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }
}