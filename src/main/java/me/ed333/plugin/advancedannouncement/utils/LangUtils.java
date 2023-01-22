package me.ed333.plugin.advancedannouncement.utils;

import me.ed333.plugin.advancedannouncement.config.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LangUtils {
    private static YamlConfiguration langCfg = ConfigManager.getConfig("lang").getConfiguration();
    public static final String prefix = getLangText("prefix");

    public static void refreshLang() {
        langCfg = ConfigManager.getConfig("lang").getConfiguration();
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
        String result = langCfg.getString(path);
        if (result == null) {
            result = "null";
        }
        return ChatColor.translateAlternateColorCodes('&', result);
    }

    public static @NotNull List<String> getLangList(String path) {
        List<String> rawList = langCfg.getStringList(path);
        List<String> coloredList = new ArrayList<>();
        rawList.forEach(s -> coloredList.add(ChatColor.translateAlternateColorCodes('&', s)));
        return coloredList;
    }
}
