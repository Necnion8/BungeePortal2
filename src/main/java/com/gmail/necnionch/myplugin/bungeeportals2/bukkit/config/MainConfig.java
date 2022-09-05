package com.gmail.necnionch.myplugin.bungeeportals2.bukkit.config;

import com.gmail.necnionch.myplugin.bungeeportals2.common.BukkitConfigDriver;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class MainConfig extends BukkitConfigDriver {
    public MainConfig(JavaPlugin plugin) {
        super(plugin);
    }


    public String getConnectingMessage(String serverName) {
        return ChatColor.translateAlternateColorCodes('&',
                config.getString("messages.connecting", "").replaceAll("\\{server}", serverName));
    }

    public String getNotFoundPortalMessage(String portalName) {
        return ChatColor.translateAlternateColorCodes('&',
                config.getString("messages.not-found-portal", "").replaceAll("\\{portal}", portalName));
    }


}
