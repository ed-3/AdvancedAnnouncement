package top.ed333.mcplugin.advancedann.bungee.config;

import top.ed333.mcplugin.advancedann.bungee.utils.ConsoleSender;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.JsonConfiguration;
import net.md_5.bungee.config.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.ed333.mcplugin.advancedann.common.utils.Streams;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

public class ConfigManager {
    public static final ConfigurationProvider YAML_PROVIDER = ConfigurationProvider.getProvider(YamlConfiguration.class);
    public static final ConfigurationProvider JSON_PROVIDER = ConfigurationProvider.getProvider(JsonConfiguration.class);

    private static final HashMap<String, Config> configMap = new HashMap<>();

    public static void addConfig(@NotNull String identify, @NotNull Config config) {
        if (identify.isEmpty()) {
            throw new IllegalArgumentException("identify cannot be empty!");
        }

        configMap.put(identify, config);
    }

    public static @Nullable Config getConfig(String identify) {
        if (identify.isEmpty()) {
            throw new IllegalArgumentException("identify cannot be empty!");
        }
        return configMap.get(identify);
    }

    public static @NotNull Collection<Config> getConfigs() {
        return configMap.values();
    }

    public static void checkAllFile() {
        getConfigs().forEach(ConfigManager::checkFile);
    }

    public static void checkFile(@NotNull Config config) {
        File cfgFile = config.getConfigFile();
        ConsoleSender.debugInfo("Checking file: " + cfgFile.getAbsolutePath());
        if (!cfgFile.exists()) {
            cfgFile.getParentFile().mkdirs();
            if (config.getConfigInJar() != null) {
                try {
                    Streams.save(config.getRawContent(), config.getConfigFile());
                    ConsoleSender.debugInfo("extract config file: " + cfgFile.getAbsolutePath());
                } catch (IOException e) {
                    ConsoleSender.err("Could not save config file into dis file '" + cfgFile + "'.\n" +
                            "cause: " + e.getLocalizedMessage());
                    e.printStackTrace();
                }
            } else {
                try {
                    cfgFile.createNewFile();
                    ConsoleSender.debugInfo("Create new config file: " + cfgFile.getAbsolutePath());
                } catch (IOException e) {
                    ConsoleSender.err("could not create config file into disk file '" + cfgFile + "'.\n " +
                            "cause: " + e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    public static void loadAll() {
        configMap.forEach(((s, config) -> config.load()));
    }

    public static @Nullable File getConfigFile(@NotNull String identify) {
        Config result = getConfig(identify);
        if (result != null) {
            return result.getConfigFile();
        }
        return null;
    }
}
