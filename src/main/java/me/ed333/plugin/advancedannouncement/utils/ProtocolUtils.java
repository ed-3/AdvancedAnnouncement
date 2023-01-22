package me.ed333.plugin.advancedannouncement.utils;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.utility.MinecraftVersion;
import org.bukkit.entity.Player;

public class ProtocolUtils {
    private static final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

    public static int getPlayerClientVersion(Player p) {
        return protocolManager.getProtocolVersion(p);
    }

    public static boolean isPlayerLegacyVer(Player p) {
        return getPlayerClientVersion(p) < 735;
    }

    public static boolean isLegacyServer() {
        return protocolManager.getMinecraftVersion().compareTo(new MinecraftVersion("1.16")) < 0;
    }
}
