package com.gmail.necnionch.myplugin.bungeeportals2.bukkit.hooks;

import com.gmail.necnionch.myapp.craftswitcherreportmodule.SwitcherServer;
import com.gmail.necnionch.myapp.craftswitcherreportmodule.v1.CraftSwitcherAPI;
import org.bukkit.plugin.Plugin;

public class SwitcherHook {
    private final Plugin plugin;
    private boolean available;

    public SwitcherHook(Plugin plugin) {
        this.plugin = plugin;
    }


    public boolean init() {
        available = false;
        try {
            Class.forName("com.gmail.necnionch.myapp.craftswitcherreportmodule.v1.CraftSwitcherAPI");
        } catch (ClassNotFoundException e) {
            return false;
        }

        available = true;
        return true;
    }

    public boolean isAvailable() {
        return available;
    }

    public boolean checkOnline(String serverName) {
        if (available) {
            SwitcherServer server = CraftSwitcherAPI.getServer(serverName);
            if (server != null) {
                switch (server.getState()) {
                    case RUNNING:
                    case STARTING:
                    case STARTED:
                        return true;
                }
            }
        }
        return false;
    }
}
