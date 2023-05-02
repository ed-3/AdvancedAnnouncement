package me.ed333.plugin.advancedannouncement.utils;

import me.ed333.plugin.advancedannouncement.config.Config;
import me.ed333.plugin.advancedannouncement.config.ConfigManager;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

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
        String result = langCfg.getConfiguration().getString(path);
        /*
        check path specified if exists in file.
        if not exists, read and save the resource file's text.
         */
        if (result == null) {
            result = langCfg.getConfigurationInStream().getString(path);
            langCfg.set(path, result);
            langCfg.save();
        }
        return ChatColor.translateAlternateColorCodes('&', result);
    }

    public static @NotNull List<String> getLangList(String path) {
        List<String> rawList = langCfg.getConfiguration().getStringList(path);

        /*
        check path specified if exists in file.
        if not exists, read and save the resource file's text.
         */
        if (rawList.isEmpty()) {
            rawList = langCfg.getConfigurationInStream().getStringList(path);
            langCfg.set(path, rawList);
            langCfg.save();
        }

        List<String> coloredList = new ArrayList<>();
        rawList.forEach(s -> coloredList.add(ChatColor.translateAlternateColorCodes('&', s)));
        return coloredList;
    }
}
