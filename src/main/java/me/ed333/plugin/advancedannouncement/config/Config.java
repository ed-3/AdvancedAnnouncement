package me.ed333.plugin.advancedannouncement.config;

import me.ed333.plugin.advancedannouncement.utils.GlobalConsoleSender;
import me.ed333.plugin.advancedannouncement.utils.Streams;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Config {
    private final @NotNull File configFile;
    private final @NotNull String identify;
    private String rawContent;
    private final YamlConfiguration configurationInStream = new YamlConfiguration();
    private YamlConfiguration configuration = new YamlConfiguration();

    public Config(@NotNull String identify, @Nullable InputStream cfgStream, @NotNull File configFile) {
        Validate.notNull(identify, "identify cannot be null!");
        Validate.notEmpty(identify, "identify cannot be empty!");
        Validate.notNull(configFile, "config file cannot be null!");

        this.identify = identify;
        this.configFile = configFile;
        if (cfgStream != null) {
            try {
                this.rawContent = Streams.read(cfgStream, StandardCharsets.UTF_8);
                this.configurationInStream.loadFromString(rawContent);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }

        ConfigManager.addConfig(identify, this);
    }

    public @NotNull File getConfigFile() {
        return configFile;
    }

    public YamlConfiguration getConfiguration() {
        return configuration;
    }

    public @Nullable YamlConfiguration getConfigurationInStream() {
        return configurationInStream;
    }

    public String getRawContent() {
        return rawContent;
    }

    public void load() {
        this.configuration = YamlConfiguration.loadConfiguration(configFile);
        GlobalConsoleSender.debugInfo("loaded config from disk '" + configFile.getAbsolutePath() + "'");
    }

    public void save() {
        try {
            configuration.save(configFile);
            GlobalConsoleSender.debugInfo("saved config into disk file '" + configFile.getAbsolutePath() + "'");
        } catch (IOException e) {
            GlobalConsoleSender.err("could not save config into disk file '" + configFile + "'.\n " +
                    "cause: " + e.getLocalizedMessage());
        }
    }

    public void set(String path, Object val) {
        configuration.set(path, val);
    }

    @Override
    public String toString() {
        return "Config{" + "configFile=" + configFile +
                ", identify='" + identify + '\'' +
                ", configurationInStream=" + configurationInStream +
                ", configuration=" + configuration +
                '}';
    }
}
