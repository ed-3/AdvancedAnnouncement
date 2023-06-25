package top.ed333.mcplugin.advancedann.bukkit.utils;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.PacketType.Play;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ProtocolUtils {

    private static final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
    private static final short SERVER_VERSION = Short.parseShort(Bukkit.getServer().getBukkitVersion().split("\\.")[1].split("-")[0]);

    public static int getPlayerClientVersion(Player p) {
        return protocolManager.getProtocolVersion(p);
    }

    public static boolean canHandleRGB(Player p) {
        return !(isPlayerLegacyVer(p) && isLegacyServer());
    }

    public static boolean isPlayerLegacyVer(Player p) {
        return getPlayerClientVersion(p) < 735;
    }

    public static boolean isLegacyServer() {
        return SERVER_VERSION <= 16;
    }

    public static void sendChat(Player sendTo, String json) {
        WrappedChatComponent chatComponent = WrappedChatComponent.fromJson(json);
        sendChat(sendTo, chatComponent);
    }

    public static void sendChat(Player sendTo, WrappedChatComponent component) {
        PacketContainer packet;
        if (SERVER_VERSION >= 19) {
            packet = protocolManager.createPacket(Play.Server.SYSTEM_CHAT);
            packet.getStrings().write(0, component.getJson());
        } else {
            packet = protocolManager.createPacket(Play.Server.CHAT);
            packet.getChatTypes().write(0, EnumWrappers.ChatType.SYSTEM);
            packet.getChatComponents().write(0, component);
        }
        protocolManager.sendServerPacket(sendTo, packet);
    }

    public static void sendTitle(Player sendTo, int fadeIn, int stay, int fadeOut, String json) {
        WrappedChatComponent component = WrappedChatComponent.fromJson(json);
        sendTitle(sendTo, fadeIn, stay, fadeOut, component);
    }

    public static void sendTitle(Player sendTo, int fadeIn, int stay, int fadeOut, WrappedChatComponent titleComponent) {
        
        PacketContainer packet;

        if (SERVER_VERSION >= 17) {
            packet = protocolManager.createPacket(Play.Server.SET_TITLE_TEXT);
            sendTimePacket(sendTo, fadeIn, stay, fadeOut);
        } else {
            packet = protocolManager.createPacket(Play.Server.TITLE);
            packet.getTitleActions().write(0, EnumWrappers.TitleAction.TITLE);
            packet.getIntegers().write(0, fadeIn);
            packet.getIntegers().write(1, stay);
            packet.getIntegers().write(2, fadeOut);
        }
        packet.getChatComponents().write(0, titleComponent);
        protocolManager.sendServerPacket(sendTo, packet);
    }

    public static void sendSubtitle(Player sendTo, int fadeIn, int stay, int fadeOut, String json) {
        WrappedChatComponent component = WrappedChatComponent.fromJson(json);
        sendSubtitle(sendTo, fadeIn, stay, fadeOut, component);
    }

    public static void sendSubtitle(Player sendTo, int fadeIn, int stay, int fadeOut, WrappedChatComponent subComponent) {
        PacketContainer packet;
        if (SERVER_VERSION >= 17) {
            packet = protocolManager.createPacket(Play.Server.SET_SUBTITLE_TEXT);
            sendTimePacket(sendTo, fadeIn, stay, fadeOut);
        } else {
            packet = protocolManager.createPacket(Play.Server.TITLE);
            packet.getTitleActions().write(0, EnumWrappers.TitleAction.SUBTITLE);
            packet.getIntegers().write(0, fadeIn);
            packet.getIntegers().write(1, stay);
            packet.getIntegers().write(2, fadeOut);
        }
        packet.getChatComponents().write(0, subComponent);
        protocolManager.sendServerPacket(sendTo, packet);
    }

    public static void sendActionBar(Player sendTo, String json) {
        WrappedChatComponent component = WrappedChatComponent.fromJson(json);
        sendActionBar(sendTo, component);
    }

    public static void sendActionBar(Player sendTo, WrappedChatComponent content) {
        PacketContainer packet;
        
        if (SERVER_VERSION >= 17) {
            packet = protocolManager.createPacket(Play.Server.SET_ACTION_BAR_TEXT);
            packet.getChatComponents().write(0, content);
        } else {
            packet = protocolManager.createPacket(Play.Server.TITLE);
            packet.getTitleActions().write(0, EnumWrappers.TitleAction.ACTIONBAR);
            packet.getChatComponents().write(0, content);
        }
        
        protocolManager.sendServerPacket(sendTo, packet);
    }

    private static void sendTimePacket(Player sendTo, int fadeIn, int stay, int fadeOut) {
            PacketContainer timePacket = protocolManager.createPacket(Play.Server.SET_TITLES_ANIMATION);
            timePacket.getIntegers().write(0, fadeIn);
            timePacket.getIntegers().write(1, stay);
            timePacket.getIntegers().write(2, fadeOut);
            protocolManager.sendServerPacket(sendTo, timePacket);
    }
}
