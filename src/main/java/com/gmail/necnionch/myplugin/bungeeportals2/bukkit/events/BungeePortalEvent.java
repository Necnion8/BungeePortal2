package com.gmail.necnionch.myplugin.bungeeportals2.bukkit.events;

import com.gmail.necnionch.myplugin.bungeeportals2.bukkit.portal.Portal;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BungeePortalEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private Player player;
    private Portal portal;

    public BungeePortalEvent(Player player, Portal portal) {
        this.player = player;
        this.portal = portal;
    }

    public Player getPlayer() {
        return player;
    }

    public Portal getPortal() {
        return portal;
    }


    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
