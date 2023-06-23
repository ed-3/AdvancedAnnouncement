package top.ed333.mcplugin.advancedann.bungee.config;

import net.md_5.bungee.config.Configuration;

public class ConfigKeys {
    public static String TRANSLATION = "zh_CN";
    public static boolean DEBUG = false;
    public static boolean RANDOM = true;
    public static boolean UPDATE_CHECK = true;
    public static int UPDATE_SOURCE = 1;
    public static boolean BSTATS = true;

    public static void initKey() {
        Config config = ConfigManager.getConfig("config");
        config.load();
        Configuration ymlCfg = config.getConfig();

        TRANSLATION = ymlCfg.getString("translation", "zh_CN");
        UPDATE_CHECK = ymlCfg.getBoolean("updateCheck", true);
        UPDATE_SOURCE = ymlCfg.getInt("updateSource", 1);
        BSTATS = ymlCfg.getBoolean("bstats", false);
        RANDOM = ymlCfg.getBoolean("random", true);
        DEBUG = ymlCfg.getBoolean("debug", false);
    }
}
