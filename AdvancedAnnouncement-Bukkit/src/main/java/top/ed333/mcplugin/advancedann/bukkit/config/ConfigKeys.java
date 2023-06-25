package top.ed333.mcplugin.advancedann.bukkit.config;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigKeys {
    public static String TRANSLATION = "zh_CN";
    public static boolean DEBUG = false;
    public static boolean RANDOM = true;
    public static boolean UPDATE_CHECK = true;
    public static int UPDATE_SOURCE = 1;
    public static boolean BSTATS = true;

    public static void initKey(File cfgFile) {
        YamlConfiguration ymlCfg = YamlConfiguration.loadConfiguration(cfgFile);

        TRANSLATION = ymlCfg.getString("translation", "zh_CN");
        UPDATE_CHECK = ymlCfg.getBoolean("updateCheck", true);
        UPDATE_SOURCE = ymlCfg.getInt("updateSource", 1);
        BSTATS = ymlCfg.getBoolean("bStats", true);
        RANDOM = ymlCfg.getBoolean("random", true);
        DEBUG = ymlCfg.getBoolean("debug", false);
    }
}
