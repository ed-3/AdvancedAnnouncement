package me.ed333.plugin.advancedannouncement;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigKeys {
    private static YamlConfiguration ymlCfg;
    public static boolean DEBUG = false;
    public static boolean RANDOM = true;
    public static boolean CONSOLE_BROAD_CAST = true;
    public static boolean PLACEHOLDER_API_SUPPORT = true;
    public static boolean UPDATE_CHECK = true;
    public static boolean BSTATS = true;
    public static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static void initCfg(File cfgFile) {
        ymlCfg = YamlConfiguration.loadConfiguration(cfgFile);

        RANDOM = ymlCfg.getBoolean("random", true);
        CONSOLE_BROAD_CAST = ymlCfg.getBoolean("Console-broadCast", true);
        PLACEHOLDER_API_SUPPORT = ymlCfg.getBoolean("PlaceholderAPI-support", true);
        DATE_FORMAT = ymlCfg.getString("date-format", "yyyy-MM-dd HH:mm:ss");
        DEBUG = ymlCfg.getBoolean("debug", false);
    }
}
