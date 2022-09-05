package com.gmail.necnionch.myplugin.bungeeportals2.bukkit.portal;

import com.gmail.necnionch.myplugin.bungeeportals2.bukkit.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;


public class Portal {
    private String name;
    private String worldName;
    private int x, y, z;
    private int x2, y2, z2;
    private BlockFace direction = null;
    private String destinationServer = null;
    private String destinationPortal = null;

    public Portal(String name, String worldName, int x, int y, int z, int x2, int y2, int z2) {
        this.name = Objects.requireNonNull(name);
        this.worldName = Objects.requireNonNull(worldName);
        this.x = x;
        this.y = y;
        this.z = z;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
    }

    public Portal(String name, World world, Location minLoc, Location maxLoc) {
        this.name = name;
        this.worldName = world.getName();
        this.x = minLoc.getBlockX();
        this.y = minLoc.getBlockY();
        this.z = minLoc.getBlockZ();
        this.x2 = maxLoc.getBlockX();
        this.y2 = maxLoc.getBlockY();
        this.z2 = maxLoc.getBlockZ();
    }

    public HashMap<String, Object> serialize() {
        HashMap<String, Object> c = new HashMap<>();

//        c.put("name", name);
        c.put("world", worldName);
        c.put("location", String.format("%d,%d,%d:%d,%d,%d", x, y, z, x2, y2, z2));
        c.put("direction", (direction != null) ? direction.name() : null);
        c.put("dest-server", destinationServer);
        c.put("dest-portal", destinationPortal);

        return c;
    }

    public static Portal deserialize(String name, ConfigurationSection config) throws NullPointerException {
        Integer[] tmp;
        try {
            tmp = Stream.of(Objects.requireNonNull(config.getString("location", null)).split("[,:]"))
                    .map(Integer::parseInt)
                    .toArray(Integer[]::new);
        } catch (NumberFormatException ignored) {
            throw new NullPointerException();
        }

        Portal p;
        try {
            p = new Portal(name, config.getString("world"),
                    tmp[0], tmp[1], tmp[2], tmp[3], tmp[4], tmp[5]);
        } catch (IndexOutOfBoundsException ignored) {
            throw new NullPointerException();
        }

        try {
            p.setDirection(BlockFace.valueOf(config.getString("direction")));
        } catch (NullPointerException | IllegalArgumentException ignored) {}

        p.setDestinationServer(config.getString("dest-server", null));
        p.setDestinationPortal(config.getString("dest-portal", null));
        return p;
    }

    public static String findStringFromLocation(Location location) {
        World w = Objects.requireNonNull(location.getWorld());
        return location.getBlockX() + "#" + location.getBlockY() + "#" + location.getBlockZ() + "#" + w.getName();
    }


    public List<String> findStrings() {
        int minX = Math.min(x, x2); int maxX = Math.max(x, x2);
        int minY = Math.min(y, y2); int maxY = Math.max(y, y2);
        int minZ = Math.min(z, z2); int maxZ = Math.max(z, z2);
        List<String> strings = new ArrayList<>();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    strings.add(String.format("%d#%d#%d#%s", x, y, z, worldName));
                }
            }
        }
        return strings;
    }

    public String getName() {
        return name;
    }

    public String getWorldName() {
        return worldName;
    }

    public String getDestinationServer() {
        return destinationServer;
    }

    public String getDestinationPortal() {
        return destinationPortal;
    }

    public BlockFace getDirection() {
        return direction;
    }

    public void setDestinationServer(String destinationServer) {
        this.destinationServer = destinationServer;
    }

    public void setDestinationPortal(String destinationPortal) {
        this.destinationPortal = destinationPortal;
    }

    public void setDirection(BlockFace direction) {
        this.direction = direction;
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getX2() {
        return x2;
    }

    public int getY2() {
        return y2;
    }

    public int getZ2() {
        return z2;
    }


    public Location getSafeLocation() {
        World world = Objects.requireNonNull(Bukkit.getWorld(getWorldName()));

        double x = ((0.5 + this.x) + (0.5 + x2)) / 2d;
        int y = Math.min(this.y, y2);
        double z = ((0.5 + this.z) + (0.5 + z2)) / 2d;
        int limitY = Math.max(this.y, y2);

        Location loc = new Location(world, x, y, z);
        loc.setYaw(Util.directionToYaw(getDirection()));
        while (!Material.AIR.equals(loc.getBlock().getType()) && loc.getY() <= limitY) {
            loc.add(0, 1, 0);
        }

        return loc;
    }

}
