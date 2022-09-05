package com.gmail.necnionch.myplugin.bungeeportals2.bukkit.config;

import com.gmail.necnionch.myplugin.bungeeportals2.bukkit.BungeePortals2;
import com.gmail.necnionch.myplugin.bungeeportals2.bukkit.portal.Portal;
import com.gmail.necnionch.myplugin.bungeeportals2.common.BukkitConfigDriver;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;

public class PortalConfig extends BukkitConfigDriver {
    private BungeePortals2 pl;
    public PortalConfig(BungeePortals2 plugin) {
        super(plugin, "portals.yml", "empty.yml");
        this.pl = plugin;
    }


    @Override
    public boolean onLoaded(FileConfiguration config) {
        ArrayList<Portal> portals = new ArrayList<>();

        Portal p;
        ConfigurationSection s, pSection;
        pSection = config.getConfigurationSection("portals");
        if (pSection != null) {
            for (String name : pSection.getKeys(false)) {
                try {
                    //noinspection ConstantConditions
                    p = Portal.deserialize(name, pSection.getConfigurationSection(name));
                } catch (NullPointerException ignored) {
                    continue;
                }
                portals.add(p);
            }
        }
        pl.getPortalManager().loadPortals(portals);

        getLogger().info("Loaded " + portals.size() + " portals!");
        return super.onLoaded(config);
    }


    @Override
    public boolean save() {
        //  clean all
        config.getKeys(false).forEach(k -> config.set(k, null));

        for (Portal portal : pl.getPortalManager().getPortals()) {
            config.set("portals." + portal.getName(), portal.serialize());
        }

        return super.save();
    }
}
