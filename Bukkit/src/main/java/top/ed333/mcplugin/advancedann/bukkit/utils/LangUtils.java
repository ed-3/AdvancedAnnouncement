package top.ed333.mcplugin.advancedann.bukkit.utils;

import top.ed333.mcplugin.advancedann.bukkit.config.Config;
import top.ed333.mcplugin.advancedann.bukkit.config.ConfigManager;
import org.apache.commons.lang.Validate;
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
        /* prevent have NPE */
        if (result == null) {
            result = langCfg.getConfigurationInStream().getString(path);
            langCfg.set(path, result);
            langCfg.save();
        }
        return translateAlternateColorCodes('&', result);
    }

    public static @NotNull List<String> getLangList(String path) {
        List<String> rawList = langCfg.getConfiguration().getStringList(path);
        /* prevent have NPE */
        if (rawList.isEmpty()) {
            rawList = langCfg.getConfigurationInStream().getStringList(path);
            langCfg.set(path, rawList);
            langCfg.save();
        }

        List<String> coloredList = new ArrayList<>();
        rawList.forEach(s -> coloredList.add(translateAlternateColorCodes('&', s)));
        return coloredList;
    }

    // copied from org.bukkit.ChatColor
    @NotNull
    private static String translateAlternateColorCodes(char altColorChar, @NotNull String textToTranslate) {
        Validate.notNull(textToTranslate, "Cannot translate null text");

        char[] b = textToTranslate.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx".indexOf(b[i + 1]) > -1) {
                b[i] = '\u00A7';
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }
        return new String(b);
    }
}
