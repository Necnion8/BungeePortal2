package com.gmail.necnionch.myplugin.bungeeportals2.bukkit.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

public class ArgumentParser extends ArrayList<String> {
    private CommandSender sender;

    public ArgumentParser(String[] args, CommandSender sender) {
        super(Arrays.asList(args));
        this.sender = sender;
    }

    public boolean check(String cmd, boolean emptyArg, boolean reqPlayer) {
        if (reqPlayer && !(sender instanceof Player))
            return false;
        if (isEmpty())
            return false;
        if (!emptyArg && size() == 1)
            return false;
        if (get(0).equalsIgnoreCase(cmd)) {
            remove(0);
            return true;
        }
        return false;
    }

    public boolean check(String cmd, boolean emptyArg) {
        return check(cmd, emptyArg, false);
    }

    public String get(int index, String def) {
        try {
            return this.get(index);
        } catch (IndexOutOfBoundsException e) {
            return def;
        }
    }

    public Integer getInteger(int index) {
        try {
            return Integer.parseInt(get(index));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
