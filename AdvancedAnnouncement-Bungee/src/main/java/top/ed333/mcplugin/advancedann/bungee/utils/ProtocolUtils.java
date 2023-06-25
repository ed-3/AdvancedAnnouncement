package top.ed333.mcplugin.advancedann.bungee.utils;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.ProtocolConstants;
import top.ed333.mcplugin.advancedann.bungee.AdvancedAnnouncement;

public class ProtocolUtils {
    // minor version
    public static final short SERVER_VERSION = Short.parseShort(AdvancedAnnouncement.INSTANCE.getProxy().getVersion().split("\\.")[1].split("-")[0]);

    public static boolean canHandleRGB(ProxiedPlayer p) {
        return !(isPlayerLegacyVer(p) && isLegacyServer());
    }

    public static boolean isLegacyServer() {
        return SERVER_VERSION < 16;
    }

    public static boolean isPlayerLegacyVer(ProxiedPlayer player) {
        return player.getPendingConnection().getVersion() < ProtocolConstants.MINECRAFT_1_16;
    }

    public static void sendActionBar(ProxiedPlayer sendTo, BaseComponent[] components) {
        sendTo.sendMessage(ChatMessageType.ACTION_BAR, components);
    }

    public static void sendChat(ProxiedPlayer sendTo, BaseComponent[] components) {
        sendTo.sendMessage(ChatMessageType.CHAT, components);
    }

    public static void sendTitle(ProxiedPlayer player, int fadeIn, int stay, int fadeout, BaseComponent[] contents) {
        Title title = AdvancedAnnouncement.INSTANCE.getProxy().createTitle();
        title.fadeIn(fadeIn);
        title.stay(stay);
        title.fadeOut(fadeout);
        title.title(contents);
        player.sendTitle(title);
    }

    public static void sendSub(ProxiedPlayer player, int fadeIn, int stay, int fadeout, BaseComponent[] contents) {
        Title title = AdvancedAnnouncement.INSTANCE.getProxy().createTitle();
        title.fadeIn(fadeIn);
        title.stay(stay);
        title.fadeOut(fadeout);
        title.subTitle(contents);
        player.sendTitle(title);
    }
}
