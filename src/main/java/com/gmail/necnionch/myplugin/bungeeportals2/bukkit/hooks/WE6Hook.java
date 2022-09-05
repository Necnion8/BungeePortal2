package com.gmail.necnionch.myplugin.bungeeportals2.bukkit.hooks;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class WE6Hook implements WorldEditHook {
    private WorldEditPlugin wePl;
    private Method getSelectionMethod;
    private Method getMinimumPointMethod;
    private Method getMaximumPointMethod;
    private Class<?> cuboidSelectionClass;

    public WE6Hook(Plugin plugin) {
        Plugin tmp = Bukkit.getPluginManager().getPlugin("WorldEdit");
        this.wePl = (WorldEditPlugin) tmp;


    }

    @Override
    public WE6Hook setup() throws Exception, NoClassDefFoundError {
        Class<?> selectionClass = Class.forName("com.sk89q.worldedit.bukkit.selections.Selection");
        getSelectionMethod = wePl.getClass().getMethod("getSelection", Player.class);
        getMinimumPointMethod = selectionClass.getMethod("getMinimumPoint");
        getMaximumPointMethod = selectionClass.getMethod("getMaximumPoint");
        cuboidSelectionClass = Class.forName("com.sk89q.worldedit.bukkit.selections.Selection");

        return this;
    }

    @Override
    public Location[] getSelections(Player player) {
        try {
            Object selection = getSelectionMethod.invoke(wePl, player);

            if (cuboidSelectionClass.isInstance(selection)) {
                Location minLoc = (Location) getMinimumPointMethod.invoke(selection);
                Location maxLoc = (Location) getMaximumPointMethod.invoke(selection);
                return new Location[] {minLoc, maxLoc};
            }

        } catch (IllegalAccessException | InvocationTargetException | ClassCastException ignored) {
        }

        return null;
    }

    @Override
    public void setSelections(Player player, Location minLoc, Location maxLoc) {
//        BukkitPlayer bPlayer = BukkitAdapter.adapt(player);
//        LocalSession session = WorldEdit.getInstance().getSessionManager().get(bPlayer);
//
//        new CuboidRegionSelector(
//                bPlayer.getWorld(), BukkitAdapter.adapt(minLoc).toVector(), BukkitAdapter.adapt(maxLoc).toVector()
//        ).

    }


}
