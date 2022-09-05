package com.gmail.necnionch.myplugin.bungeeportals2.bukkit.utils;

import org.bukkit.block.BlockFace;

public class Util {
    public static BlockFace yawToDirection(float yaw) {
        if (yaw < 0) yaw += 360;
        yaw %= 360;
        int i = (int)((yaw+45) / 90);
        if (i == 1) {
            return BlockFace.WEST;
        } else if (i == 2) {
            return BlockFace.NORTH;
        } else if (i == 3) {
            return BlockFace.EAST;
        } else {
            return BlockFace.SOUTH;
        }
    }

    public static int directionToYaw(BlockFace face) {
        switch (face) {
            case WEST:
                return 90;
            case NORTH:
                return 180;
            case EAST:
                return -90;
            case SOUTH:
            default:
                return 0;
        }
    }
}
