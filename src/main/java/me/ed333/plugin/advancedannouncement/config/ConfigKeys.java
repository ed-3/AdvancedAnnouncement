package me.ed333.plugin.advancedannouncement.config;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigKeys {
    public static boolean DEBUG = false;
    public static boolean RANDOM = true;
    public static boolean PLACEHOLDER_API_SUPPORT = true;
    public static boolean UPDATE_CHECK = true;
    public static int UPDATE_SOURCE = 1;
    public static boolean BSTATS = true;
    public static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static void initKey(File cfgFile) {
        YamlConfiguration ymlCfg = YamlConfiguration.loadConfiguration(cfgFile);

        RANDOM = ymlCfg.getBoolean("random", true);
        PLACEHOLDER_API_SUPPORT = ymlCfg.getBoolean("PlaceholderAPI-support", true);
        DATE_FORMAT = ymlCfg.getString("date-format", "yyyy-MM-dd HH:mm:ss");
        DEBUG = ymlCfg.getBoolean("debug", false);
        UPDATE_CHECK = ymlCfg.getBoolean("updateCheck", true);
        UPDATE_SOURCE = ymlCfg.getInt("updateSource", 1);
    }
}
