package com.gmail.necnionch.myplugin.bungeeportals2.bukkit.listeners;

import com.gmail.necnionch.myplugin.bungeeportals2.bukkit.BungeePortals2;
import com.gmail.necnionch.myplugin.bungeeportals2.bukkit.commands.MainCommand;
import com.gmail.necnionch.myplugin.bungeeportals2.bukkit.config.MainConfig;
import com.gmail.necnionch.myplugin.bungeeportals2.bukkit.events.BungeePortalEvent;
import com.gmail.necnionch.myplugin.bungeeportals2.bukkit.portal.Portal;
import com.gmail.necnionch.myplugin.bungeeportals2.bukkit.portal.PortalManager;
import com.gmail.necnionch.myplugin.bungeeportals2.bukkit.utils.Util;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Objects;

public class PlayerEventListener implements Listener {
    private BungeePortals2 pl;
    private MainConfig cfg;
    private PortalManager manager;
    public PlayerEventListener(BungeePortals2 pl) {
        this.pl = pl;
        this.cfg = pl.getMainConfig();
        this.manager = pl.getPortalManager();
    }
    public void register() {
        pl.getServer().getPluginManager().registerEvents(this, pl);
    }


    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        Location loc = Objects.requireNonNull(event.getTo());

        Portal portal = manager.findPortal(loc);
        if (portal != null && !manager.checkPortalInside(p)) {
            manager.putPortalInside(p);

            BungeePortalEvent mEvent = new BungeePortalEvent(p, portal);
            pl.getServer().getPluginManager().callEvent(mEvent);
            if (mEvent.isCancelled())
                return;

            if (portal.getDestinationServer() != null) {
//                BlockFace dir = portal.getDirection();
//                if (dir != null) {
//                    Location loc2 = loc.clone();
//                    loc2.setYaw(Util.directionToYaw(dir));
//                    p.teleport(loc2);
//                }

                if (portal.getDestinationPortal() != null) {
                    pl.sendToPortal(p, portal.getDestinationServer(), portal.getDestinationPortal());
                }

                if (!pl.getSwitcher().isAvailable() || pl.getSwitcher().checkOnline(portal.getDestinationServer())) {
                    MainCommand.send(p, cfg.getConnectingMessage(portal.getDestinationServer()));
                }
                pl.sendToServer(p, portal.getDestinationServer());
            }

        } else if (portal == null && manager.checkPortalInside(p)) {
            manager.removePortalInside(p);

//            p.sendMessage("Portal OUT");

        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        Portal portal = manager.findPortal(p.getLocation());

        if (portal != null) {
            manager.putPortalInside(p);

            BlockFace dir = portal.getDirection();
            if (dir != null) {
                Location loc = p.getLocation().clone();
                loc.setYaw(Util.directionToYaw(dir));
                p.teleport(loc);
            }
        }

        pl.getServer().getScheduler().runTaskLater(pl, () -> pl.sendGetServersRequest(), 2);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        manager.removePortalInside(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        if (manager.findPortal(event.getTo()) != null) {
            manager.putPortalInside(event.getPlayer());
        }
    }


}
