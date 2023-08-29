package top.ed333.mcplugin.advancedann.bungee.config;

import net.md_5.bungee.config.Configuration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.ed333.mcplugin.advancedann.bungee.utils.ConsoleSender;
import top.ed333.mcplugin.advancedann.common.utils.Streams;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Config {
    private final @NotNull File configFile;
    private final @NotNull String identify;
    private String rawContent;
    private Configuration config;
    private Configuration configInJar;

    public Config(@NotNull String identify, @Nullable InputStream streamInJar, @NotNull File configFile) {
        if (identify.isEmpty()) {
            throw new IllegalArgumentException("Identify cannot be empty!");
        }

        this.configFile = configFile;
        this.identify = identify;

        if (streamInJar != null) {
            try {
                this.rawContent = Streams.read(streamInJar, StandardCharsets.UTF_8);
                this.configInJar = ConfigManager.YAML_PROVIDER.load(rawContent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ConfigManager.addConfig(identify, this);
    }

    public void set(String path, Object val) {
        config.set(path, val);
    }

    public void load() {
        try {
            this.config = ConfigManager.YAML_PROVIDER.load(this.configFile);
        } catch (IOException e) {
            ConsoleSender.err("An error occurred while loading config form config file: '" + configFile + "'. \n" +
                    "cause: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            ConfigManager.YAML_PROVIDER.save(config, configFile);
            ConsoleSender.debugInfo("Saved config into disk file '" + configFile.getAbsolutePath() + "'");
        } catch (IOException e) {
            ConsoleSender.err("Could not save config into disk file '" + configFile + "'.\n " +
                    "cause: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public String getIdentify() {
        return identify;
    }

    public String getRawContent() {
        return rawContent;
    }

    public File getConfigFile() {
        return configFile;
    }

    public Configuration getConfig() {
        return config;
    }

    public Configuration getConfigInJar() {
        return configInJar;
    }
}
