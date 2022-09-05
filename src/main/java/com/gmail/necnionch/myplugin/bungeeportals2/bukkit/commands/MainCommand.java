package com.gmail.necnionch.myplugin.bungeeportals2.bukkit.commands;

import com.gmail.necnionch.myplugin.bungeeportals2.bukkit.BungeePortals2;
import com.gmail.necnionch.myplugin.bungeeportals2.bukkit.portal.Portal;
import com.gmail.necnionch.myplugin.bungeeportals2.bukkit.portal.PortalManager;
import com.gmail.necnionch.myplugin.bungeeportals2.bukkit.utils.ArgumentParser;
import com.gmail.necnionch.myplugin.bungeeportals2.bukkit.utils.Util;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainCommand {
    private BungeePortals2 pl;
    private PortalManager mgr;
    public MainCommand(BungeePortals2 pl) {
        this.pl = pl;
        this.mgr = pl.getPortalManager();
    }
    public void register(String command) {
        pl.getCommand(command).setExecutor(this::onCommand);
        pl.getCommand(command).setTabCompleter(this::onTabComplete);
    }


    public boolean onCommand(CommandSender s, Command command, String label, String[] arrayArgs) {
        Player player = (s instanceof Player) ? (Player) s : null;
        ArgumentParser args = new ArgumentParser(arrayArgs, s);

        if (args.check("create", true, true)) {
            String name, destinationServer, destinationPortal;
            try {
                name = args.get(0);
                destinationServer = args.get(1, null);
                destinationPortal = args.get(2, null);
            } catch (IndexOutOfBoundsException e) {
                send(s, ChatColor.RED + "/BPortals create (name) [server] [dest-portal]");
                return true;
            }

            Location[] locations = pl.getWorldEditHook().getSelections(player);
            if (locations == null) {
                send(s, ChatColor.RED + "ポータルの範囲をWorldEditの直方体モードで選択してください。");
                return true;
            }

            if (mgr.getPortal(name) != null) {
                send(s, ChatColor.RED + "既に存在するポータル名です。");
                return true;
            }

            Portal portal = new Portal(name, player.getWorld(), locations[0], locations[1]);
            portal.setDestinationServer(destinationServer);
            portal.setDestinationPortal(destinationPortal);
            portal.setDirection(Util.yawToDirection(player.getLocation().getYaw() + 180));

            mgr.addPortal(portal);
            pl.getPortalConfig().save();
            send(s, ChatColor.GREEN + "ポータルを作成しました。");

        } else if (args.check("remove", true)) {
            if (args.isEmpty()) {
                send(s, ChatColor.RED + "/BPortals remove (name)");
                return true;
            }

            Portal portal = mgr.getPortal(args.get(0));
            if (portal != null) {
                mgr.removePortal(portal);
                pl.getPortalConfig().save();
                send(s, "指定されたポータルを削除しました。");
            } else {
                sendError(s, "そのポータルは存在しません。");
            }

        } else if (args.check("list", true)) {
            send(s, ChatColor.GRAY + "--- " + ChatColor.WHITE + "ポータル一覧 " + ChatColor.GRAY + "---");
            send(s, ChatColor.YELLOW + Stream.of(mgr.getPortals())
                    .map(Portal::getName)
                    .collect(Collectors.joining(ChatColor.GRAY + ", " + ChatColor.YELLOW)));

        } else if (args.check("info", true)) {
            if (args.isEmpty()) {
                sendError(s, "/BPortals info (portal)");
                return true;
            }
            Portal portal = mgr.getPortal(args.get(0));
            if (portal == null) {
                sendError(s, "そのポータルは存在しません。");
                return true;
            }

            send(s, ChatColor.GRAY + "--- " + ChatColor.WHITE + "ポータル: " + ChatColor.YELLOW + portal.getName() + ChatColor.GRAY + " ---");
            send(s, ChatColor.translateAlternateColorCodes('&', String.format(
                    "位置: &e%s &e[&f%d&7,&f%d&7,&f%d &6>> &f%d&7,&f%d&7,&f%d&e]",
                            portal.getWorldName(), portal.getX(), portal.getY(), portal.getZ(), portal.getX2(), portal.getY2(), portal.getZ2()
            )));
            send(s, "行き先: " + ChatColor.YELLOW + portal.getDestinationServer() + ((portal.getDestinationPortal() != null) ?
                    " " + ChatColor.GRAY + "-> " + ChatColor.WHITE + portal.getDestinationPortal(): ""));

        } else {
            return false;
        }

        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] argsArray) {
        ArgumentParser args = new ArgumentParser(argsArray, sender);

        if (args.size() == 3 && args.check("create", false)) {
            args.remove(0);
            String[] servers = pl.getCachedBungeeServers();
            return generateTabComplete(args, (servers != null) ? servers : new String[0]);

        } else if (args.size() == 2 && (args.check("remove", false) || args.check("info", false))) {
            return generateTabComplete(args, Stream.of(mgr.getPortals()).map(Portal::getName).toArray(String[]::new));

        } else if (args.size() <= 1) {
            return generateTabComplete(args, "create", "remove", "list", "info");
        }
        return Collections.emptyList();
    }

    public static void send(CommandSender s, String m) {
        if (m.isEmpty())
            return;
        s.sendMessage(BungeePortals2.CMD_PREFIX + m);
    }

    public static void sendError(CommandSender s, String m) {
        if (m.isEmpty())
            return;
        s.sendMessage(BungeePortals2.CMD_PREFIX + ChatColor.RED + m);
    }


    private List<String> generateTabComplete(List<String> args, String... list) {
        String input = (args.isEmpty()) ? "" : args.get(0);
        return Stream.of(list)
                .map(String::toLowerCase)
                .filter(e -> e.startsWith(input.toLowerCase()))
                .collect(Collectors.toList());
    }

}
