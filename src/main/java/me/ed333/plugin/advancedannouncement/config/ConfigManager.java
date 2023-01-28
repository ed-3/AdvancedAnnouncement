package me.ed333.plugin.advancedannouncement.config;

import me.ed333.plugin.advancedannouncement.utils.GlobalConsoleSender;
import me.ed333.plugin.advancedannouncement.utils.Streams;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ConfigManager {
    private static final HashMap<String, Config> configMap = new HashMap<>();

    /**
     * 添加一个配置
     *
     * @param identify 配置的识别符，用于获取 {@link Config}
     * @param config   配置, {@link Config}
     */
    public static void addConfig(String identify, Config config) {
        Validate.notNull(identify, "identify cannot be null!");
        Validate.notEmpty(identify, "identify cannot be empty!");

        configMap.put(identify, config);
    }

    public static Config getConfig(String identify) {
        Validate.notNull(identify, "identify cannot be null!");
        Validate.notEmpty(identify, "identify cannot be empty!");

        return configMap.get(identify);
    }

    public static List<File> getFiles() {
        List<File> result = new ArrayList<>();
        configMap.values().forEach(config -> result.add(config.getConfigFile()));
        return result;
    }

    /**
     * 保存指定配置
     *
     * @param c {@link Config}
     */
    public static void saveConfig(@NotNull Config c) {
        c.save();
    }

    /**
     * 获取所有配置的标识符
     *
     * @return 配置的标识符
     */
    public static @NotNull
    Set<String> getIdentifies() {
        return configMap.keySet();
    }

    /**
     * 获取所有的配置
     *
     * @return 配置
     */
    public static @NotNull Collection<Config> getConfigs() {
        return configMap.values();
    }

    /**
     * 检查所有的配置文件，如果不存在就创建新的配置
     */
    public static void checkAllFile() {
        getConfigs().forEach(ConfigManager::checkFile);
    }

    public static void checkFile(@NotNull Config config) {
        File file = config.getConfigFile();
        GlobalConsoleSender.debugInfo("Checking file: " + file.getAbsolutePath());
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            if (config.getConfigurationInStream() != null) {
                try {
                    config.getConfigurationInStream().save(config.getConfigFile());
                    GlobalConsoleSender.debugInfo("extract config file: " + file.getAbsolutePath());
                } catch (IOException e) {
                    GlobalConsoleSender.err("could not save config file into disk file '" + file + "'.\n " +
                            "cause: " + e.getLocalizedMessage());
                }
            } else {
                try {
                    file.createNewFile();
                    GlobalConsoleSender.debugInfo("Create new config file: " + file.getAbsolutePath());
                } catch (IOException e) {
                    GlobalConsoleSender.err("could not create config file into disk file '" + file + "'.\n " +
                            "cause: " + e.getLocalizedMessage());
                }
            }
        }
    }

    public static void load(Config c) {
        c.load();
    }

    public static void loadAll() {
        configMap.forEach(((s, moduleConfig) -> moduleConfig.load()));
    }

    public static File getConfigFile(String config) {
        return getConfig(config).getConfigFile();
    }
}