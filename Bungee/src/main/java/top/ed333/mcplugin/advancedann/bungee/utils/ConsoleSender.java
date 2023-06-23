package top.ed333.mcplugin.advancedann.bungee.utils;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;

public class ConsoleSender {
    private static boolean useDebug = false;
    private static final ProxyServer server = ProxyServer.getInstance();

    public static void info(String message) {
        TextComponent component = new TextComponent();
        component.addExtra("§2[§3AdvancedAnnouncement§2] §aINFO§f: §f");
        component.addExtra(message);
        server.getConsole().sendMessage(component);
    }

    public static void warn(String message) {
        TextComponent component = new TextComponent();
        component.addExtra("§2[§3AdvancedAnnouncement§2] §eWARN§f: §f");
        component.addExtra(message);
        server.getConsole().sendMessage(component);
    }

    public static void err(String message) {
        TextComponent component = new TextComponent();
        component.addExtra("§2[§3AdvancedAnnouncement§2] §cERROR§f: §f");
        component.addExtra(message);
        server.getConsole().sendMessage(component);
    }

    public static void debugInfo(String message) {
        if (useDebug) {
            TextComponent component = new TextComponent();
            component.addExtra("§2[§3AdvancedAnnouncement§2] §bDEBUG §f| §2INFO: §f");
            component.addExtra(message);
            server.getConsole().sendMessage(component);
        }
    }

    public static void setDEBUG(boolean state) {
        useDebug = state;
    }
}
