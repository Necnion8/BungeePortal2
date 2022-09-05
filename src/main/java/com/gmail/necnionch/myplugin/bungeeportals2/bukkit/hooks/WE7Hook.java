package com.gmail.necnionch.myplugin.bungeeportals2.bukkit.hooks;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


public class WE7Hook implements WorldEditHook {
    public WE7Hook(Plugin plugin) {

    }

    @Override
    public WE7Hook setup() throws Exception, NoClassDefFoundError {
        Class.forName("com.sk89q.worldedit.math.BlockVector3");
        return this;
    }

    @Override
    public Location[] getSelections(Player player) {
        BukkitPlayer bPlayer = BukkitAdapter.adapt(player);
        LocalSession session = WorldEdit.getInstance().getSessionManager().get(bPlayer);

        Region selection;
        try {
            selection = session.getSelection(bPlayer.getWorld());
        } catch (IncompleteRegionException e) {
            return null;
        }
        if (selection instanceof CuboidRegion) {
            Location minLoc = BukkitAdapter.adapt(player.getWorld(), selection.getMinimumPoint());
            Location maxLoc = BukkitAdapter.adapt(player.getWorld(), selection.getMaximumPoint());
            return new Location[] {minLoc, maxLoc};
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


//    private List<Location> getLocationsFromCuboid(CuboidRegion cuboid, World world) {
//        List<Location> locations = new ArrayList<>();
//        BlockVector3 minLocation = cuboid.getMinimumPoint();
//        BlockVector3 maxLocation = cuboid.getMaximumPoint();
//        for (int i1 = minLocation.getBlockX(); i1 <= maxLocation.getBlockX(); i1++) {
//            for (int i2 = minLocation.getBlockY(); i2 <= maxLocation.getBlockY(); i2++) {
//                for (int i3 = minLocation.getBlockZ(); i3 <= maxLocation.getBlockZ(); i3++) {
//                    locations.add(new Location(world, i1, i2, i3));
//                }
//            }
//        }
//        return locations;
//    }

}
