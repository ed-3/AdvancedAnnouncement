package me.ed333.plugin.advancedannouncement.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftVersion;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
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

    public static void sendChat(Player sendTo, String json) {
        WrappedChatComponent chatComponent = WrappedChatComponent.fromJson(json);
        sendChat(sendTo, chatComponent);
    }

    public static void sendChat(Player sendTo, WrappedChatComponent component) {
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.CHAT);
        packet.getChatTypes().write(0, EnumWrappers.ChatType.SYSTEM);
        packet.getChatComponents().write(0, component);
        protocolManager.sendServerPacket(sendTo, packet);
    }

    public static void sendTitle(Player sendTo, int fadeIn, int stay, int fadeOut, String json) {
        WrappedChatComponent component = WrappedChatComponent.fromJson(json);
        sendTitle(sendTo, fadeIn, stay, fadeOut, component);
    }
    public static void sendTitle(Player sendTo, int fadeIn, int stay, int fadeOut, WrappedChatComponent titleComponent) {
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.TITLE);
        packet.getTitleActions().write(0, EnumWrappers.TitleAction.TITLE);
        packet.getIntegers().write(0, fadeIn);
        packet.getIntegers().write(1, stay);
        packet.getIntegers().write(2, fadeOut);
        packet.getChatComponents().write(0, titleComponent);
        protocolManager.sendServerPacket(sendTo, packet);
    }

    public static void sendSubtitle(Player sendTo, int fadeIn, int stay, int fadeOut, String json) {
        WrappedChatComponent component = WrappedChatComponent.fromJson(json);
        sendSubtitle(sendTo, fadeIn, stay, fadeOut, component);
    }

    public static void sendSubtitle(Player sendTo, int fadeIn, int stay, int fadeOut, WrappedChatComponent subComponent) {
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.TITLE);
        packet.getTitleActions().write(0, EnumWrappers.TitleAction.SUBTITLE);
        packet.getIntegers().write(0, fadeIn);
        packet.getIntegers().write(1, stay);
        packet.getIntegers().write(2, fadeOut);
        packet.getChatComponents().write(0, subComponent);
        protocolManager.sendServerPacket(sendTo, packet);
    }

    public static void sendActionBar(Player sendTo, String json) {
        WrappedChatComponent component = WrappedChatComponent.fromJson(json);
        sendActionBar(sendTo, component);
    }

    public static void sendActionBar(Player sendTo, WrappedChatComponent content) {
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.TITLE);
        packet.getTitleActions().write(0, EnumWrappers.TitleAction.ACTIONBAR);
        packet.getChatComponents().write(0, content);
        protocolManager.sendServerPacket(sendTo, packet);
    }
}
