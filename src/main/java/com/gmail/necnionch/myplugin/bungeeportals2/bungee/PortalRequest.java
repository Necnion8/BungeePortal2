package com.gmail.necnionch.myplugin.bungeeportals2.bungee;

public class PortalRequest {
    private String portal;
    private String targetServer;
    private long requestedTime;

    public PortalRequest(String portalName, String serverName) {
        this.portal = portalName;
        this.targetServer = serverName;
        this.requestedTime = System.currentTimeMillis();
    }

    public String getPortalName() {
        return portal;
    }

    public long getRequestedTime() {
        return requestedTime;
    }

    public String getTargetServerName() {
        return targetServer;
    }
}
