package com.gmail.necnionch.myplugin.bungeeportals2.bukkit.hooks;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface WorldEditHook {
    WorldEditHook setup() throws Exception, NoClassDefFoundError;

    Location[] getSelections(Player player);
    void setSelections(Player player, Location startLoc, Location endLoc);

}
