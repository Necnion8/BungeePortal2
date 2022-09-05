package com.gmail.necnionch.myplugin.bungeeportals2.bukkit.portal;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class PortalManager {
    private HashMap<String, Portal> portals = new HashMap<>();
    private HashSet<UUID> insidePlayers = new HashSet<>();


    public void loadPortals(List<Portal> portals) {
        this.portals.clear();
        portals.forEach(p -> this.portals.putAll(p.findStrings().stream()
                .collect(Collectors.toMap((s) -> s, (s) -> p))));
    }

    public Portal[] getPortals() {
        HashSet<Portal> portals = new HashSet<>(this.portals.values());
        return portals.toArray(new Portal[0]);
    }

    public void addPortal(Portal portal) {
        portals.putAll(
                portal.findStrings().stream()
                        .collect(Collectors.toMap((s) -> s, (s) -> portal))
        );
    }

    public void removePortal(Portal portal) {
        for (Map.Entry<String, Portal> e : new ArrayList<>(portals.entrySet())) {
            if (e.getValue().equals(portal)) {
                portals.remove(e.getKey(), e.getValue());
            }
        }
    }

    public Portal getPortal(String name) {
        Portal portal = null;
        for (Portal p : portals.values()) {
            if (p.getName().equals(name)) {
                portal = p;
                break;
            }
        }
        return portal;
    }

    public Portal findPortal(Location location) {
        return portals.get(Portal.findStringFromLocation(location));
    }


    public boolean checkPortalInside(Player player) {
        return insidePlayers.contains(player.getUniqueId());
    }

    public void putPortalInside(Player player) {
        insidePlayers.add(player.getUniqueId());
    }

    public void removePortalInside(Player player) {
        insidePlayers.remove(player.getUniqueId());
    }

}
