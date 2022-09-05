package com.gmail.necnionch.myplugin.bungeeportals2.bukkit;

import com.gmail.necnionch.myplugin.bungeeportals2.bukkit.commands.MainCommand;
import com.gmail.necnionch.myplugin.bungeeportals2.bukkit.config.MainConfig;
import com.gmail.necnionch.myplugin.bungeeportals2.bukkit.config.PortalConfig;
import com.gmail.necnionch.myplugin.bungeeportals2.bukkit.hooks.SwitcherHook;
import com.gmail.necnionch.myplugin.bungeeportals2.bukkit.hooks.WE6Hook;
import com.gmail.necnionch.myplugin.bungeeportals2.bukkit.hooks.WE7Hook;
import com.gmail.necnionch.myplugin.bungeeportals2.bukkit.hooks.WorldEditHook;
import com.gmail.necnionch.myplugin.bungeeportals2.bukkit.listeners.PlayerEventListener;
import com.gmail.necnionch.myplugin.bungeeportals2.bukkit.portal.Portal;
import com.gmail.necnionch.myplugin.bungeeportals2.bukkit.portal.PortalManager;
import com.gmail.necnionch.myplugin.bungeeportals2.bukkit.utils.MetricsLite;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.UUID;

public final class BungeePortals2 extends JavaPlugin {
    public static final String CMD_PREFIX = ChatColor.DARK_AQUA + "[BP2] " + ChatColor.WHITE;
    public static final String PLUGIN_CHANNEL = "bportals:bportals";
    private MainConfig mainConfig;
    private PortalConfig portalConfig;
    private PortalManager portalManager = new PortalManager();
    private WorldEditHook weHook;
    private SwitcherHook swiHook;
    private String[] cachedBungeeServers;

    @Override
    public void onEnable() {
        mainConfig = new MainConfig(this);
        portalConfig = new PortalConfig(this);

        try {
            weHook = new WE7Hook(this).setup();
        } catch (Exception | NoClassDefFoundError ignored) {
            try {
                weHook = new WE6Hook(this).setup();
            } catch (Exception | NoClassDefFoundError e) {
                e.printStackTrace();
                getLogger().severe("Unable to hook to WorldEdit v7.2 - v6.1");
                setEnabled(false);
                return;
            }
        }

        swiHook = new SwitcherHook(this);
        try {
            swiHook.init();
        } catch (Exception | NoClassDefFoundError e) {
            e.printStackTrace();
        }

        mainConfig.load();
        portalConfig.load();

        new MainCommand(this).register("bungeeportals");
        new PlayerEventListener(this).register();
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerOutgoingPluginChannel(this, PLUGIN_CHANNEL);
        getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this::onPluginMessageReceived);
        getServer().getMessenger().registerIncomingPluginChannel(this, PLUGIN_CHANNEL, this::onPluginMessageReceived);

        new MetricsLite(this, 9632);
    }

    @Override
    public void onDisable() {
//        portalConfig.save();

    }


    public MainConfig getMainConfig() {
        return mainConfig;
    }

    public PortalConfig getPortalConfig() {
        return portalConfig;
    }

    public PortalManager getPortalManager() {
        return portalManager;
    }

    public WorldEditHook getWorldEditHook() {
        return weHook;
    }

    public SwitcherHook getSwitcher() {
        return swiHook;
    }

    public String[] getCachedBungeeServers() {
        return cachedBungeeServers;
    }

    public void sendToServer(Player player, String serverName) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(os)) {
            dos.writeUTF("Connect");
            dos.writeUTF(serverName);

            player.sendPluginMessage(this, "BungeeCord", os.toByteArray());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendToPortal(Player player, String serverName, String portalName) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(os)) {
            dos.writeUTF("TeleportPortal");
            dos.writeUTF(serverName);
            dos.writeUTF(portalName);

            player.sendPluginMessage(this, PLUGIN_CHANNEL, os.toByteArray());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendGetServersRequest() {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(os)) {
            dos.writeUTF("GetServers");
            getServer().sendPluginMessage(this, "BungeeCord", os.toByteArray());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void onPluginMessageReceived(String channel, Player player0, byte[] message) {
        if (!(PLUGIN_CHANNEL.equals(channel) || "BungeeCord".equals(channel)))
            return;

        try (ByteArrayInputStream is = new ByteArrayInputStream(message);
             DataInputStream iso = new DataInputStream(is)) {
            String request = iso.readUTF();

            switch (request) {
                case "GetServers":
                    cachedBungeeServers = iso.readUTF().split(", ");
                    break;

                case "TeleportPortal":
                    Player player;
                    try {
                        player = Bukkit.getPlayer(UUID.fromString(iso.readUTF()));
                        if (player == null)
                            return;
                    } catch (IllegalArgumentException ignored) {
                        return;
                    }
                    String portalName = iso.readUTF();

                    Portal portal = portalManager.getPortal(portalName);
                    if (portal != null) {
                        Location loc;
                        try {
                            loc = portal.getSafeLocation();
                        } catch (NullPointerException ignored) {  // not found world?
                            return;
                        }
                        player.teleport(loc);
                    } else {
                        MainCommand.sendError(player, mainConfig.getNotFoundPortalMessage(portalName));
                    }
                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
