package me.ed333.plugin.advancedannouncement.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

public class GlobalConsoleSender {
    private static boolean useDebug = false;
    private static final ConsoleCommandSender consoleSender = Bukkit.getConsoleSender();

    public static void warn(String msg) {
        consoleSender.sendMessage("§2[§3AdvancedAnnouncement§2] §eWARN§f: §f" + msg);
    }

    public static void info(String msg) {
        consoleSender.sendMessage("§2[§3AdvancedAnnouncement§2] §aINFO§f: §f" + msg);
    }

    public static void err(String msg) {
        consoleSender.sendMessage("§2[§3AdvancedAnnouncement§2] §cERROR§f: §f" + msg);
    }

    public static void debugInfo(String msg) {
        if (useDebug) {
            consoleSender.sendMessage("§2[§3AdvancedAnnouncement§2] §bDEBUG §f| §2INFO: §f" + msg);
        }
    }

    public static void setDEBUG(boolean state) {
        useDebug = state;
    }
}
