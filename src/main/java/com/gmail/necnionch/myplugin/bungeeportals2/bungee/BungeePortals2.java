package com.gmail.necnionch.myplugin.bungeeportals2.bungee;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.io.*;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class BungeePortals2 extends Plugin implements Listener {
    public static final String PLUGIN_CHANNEL = "bportals:bportals";
    private HashMap<ProxiedPlayer, PortalRequest> cachedPortalName = new HashMap<>();

    @Override
    public void onEnable() {
        getProxy().registerChannel(PLUGIN_CHANNEL);
        getProxy().getPluginManager().registerListener(this, this);
    }


    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (!PLUGIN_CHANNEL.equals(event.getTag()))
            return;

        ProxiedPlayer player = (ProxiedPlayer) event.getReceiver();

        try (ByteArrayInputStream is = new ByteArrayInputStream(event.getData());
             DataInputStream iso = new DataInputStream(is)) {
            String request = iso.readUTF();

            switch (request) {
                case "TeleportPortal":
                    String serverName = iso.readUTF();
                    String portalName = iso.readUTF();
                    cachedPortalName.put(player, new PortalRequest(portalName, serverName));
                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @EventHandler
    public void onServerConnected(ServerConnectedEvent event) {
        PortalRequest request = cachedPortalName.remove(event.getPlayer());
        if (request != null) {
            if (!request.getTargetServerName().equals(event.getServer().getInfo().getName()))
                return;

            if (System.currentTimeMillis() - request.getRequestedTime() > 15*1000)  // 15秒以上経過していたら放棄
                return;

            getProxy().getScheduler().schedule(this, () -> {
                try (ByteArrayOutputStream os = new ByteArrayOutputStream();
                     DataOutputStream dos = new DataOutputStream(os)) {
                    dos.writeUTF("TeleportPortal");
                    dos.writeUTF(event.getPlayer().getUniqueId().toString());
                    dos.writeUTF(request.getPortalName());
//                    event.getPlayer().sendData(PLUGIN_CHANNEL, os.toByteArray());
                    event.getServer().getInfo().sendData(PLUGIN_CHANNEL, os.toByteArray());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }, 1/20, TimeUnit.SECONDS);

        }
    }

    @EventHandler
    public void onDisconnected(PlayerDisconnectEvent event) {
        cachedPortalName.remove(event.getPlayer());
    }


}
