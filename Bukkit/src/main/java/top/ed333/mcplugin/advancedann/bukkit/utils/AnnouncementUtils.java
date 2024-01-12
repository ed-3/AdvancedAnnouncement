package top.ed333.mcplugin.advancedann.bukkit.utils;

import com.google.gson.JsonArray;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import top.ed333.mcplugin.advancedann.common.utils.Serializer;

public class AnnouncementUtils {
    public static void sendChat(Player sendTo, JsonArray jsonArrComponent) {
        BaseComponent[] components = Serializer.serializeToComponent(jsonArrComponent);
        sendTo.spigot().sendMessage(components);
    }

    public static void sendActionBar(Player sendTo, JsonArray jsonArrComponent) {
        BaseComponent[] components = Serializer.serializeToComponent(jsonArrComponent);
        sendTo.spigot().sendMessage(ChatMessageType.ACTION_BAR, components);
    }

    public static void sendTitle(@NotNull Player sendTo, String title, int fadeIn, int stay, int fadeout) {
        sendTo.sendTitle(title, null, fadeIn, stay, fadeout);
    }

    public static void sendSubtitle(@NotNull Player sendTo, String subTitle, int fadeIn, int stay, int fadeout) {
        sendTo.sendTitle(null, subTitle, fadeIn, stay, fadeout);
    }
}
