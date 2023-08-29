package top.ed333.mcplugin.advancedann.bungee.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;
import top.ed333.mcplugin.advancedann.bungee.config.Config;
import top.ed333.mcplugin.advancedann.bungee.config.ConfigManager;

import java.util.ArrayList;
import java.util.List;

public class LangUtils {
    private static Config langCfg = ConfigManager.getConfig("lang");
    public static final String prefix = getLangText("prefix");

    public static void refreshLang() {
        langCfg = ConfigManager.getConfig("lang");
    }

    public static @NotNull String parseLang_withPrefix(String path, Object @NotNull ... objects) {
        return prefix + parseLang(path, objects);
    }

    public static @NotNull String parseLang(String path, Object @NotNull ... objects) {
        String result = getLangText(path);
        for (int i = 0; i < objects.length; i++) {
            result = result.replace("{" + i + "}", objects[i].toString());
        }
        return result;
    }

    public static @NotNull String getLangText_withPrefix(String path) {
        return prefix + getLangText(path);
    }

    public static @NotNull String getLangText(String path) {
        String result = langCfg.getConfig().getString(path);
        /* prevent have NPE */
        if (result == null || result.isEmpty()) {
            result = langCfg.getConfigInJar().getString(path);
            langCfg.set(path, result);
            langCfg.save();
        }
        return ChatColor.translateAlternateColorCodes('&', result);
    }

    public static @NotNull List<String> getLangList(String path) {
        List<String> rawList = langCfg.getConfig().getStringList(path);
        /* prevent have NPE */
        if (rawList.isEmpty()) {
            rawList = langCfg.getConfigInJar().getStringList(path);
            langCfg.set(path, rawList);
            langCfg.save();
        }

        List<String> coloredList = new ArrayList<>();
        rawList.forEach(s -> coloredList.add(ChatColor.translateAlternateColorCodes('&', s)));
        return coloredList;
    }

    public static void sendMessage(CommandSender sender, String messages) {
        sendMessage(sender, TextComponent.fromLegacyText(messages));
    }

    public static void sendMessage(CommandSender sender, BaseComponent[] messages) {
        sender.sendMessage(messages);
    }
}
