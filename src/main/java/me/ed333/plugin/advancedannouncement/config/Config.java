package me.ed333.plugin.advancedannouncement.config;

import me.ed333.plugin.advancedannouncement.utils.GlobalConsoleSender;
import me.ed333.plugin.advancedannouncement.utils.Streams;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class Config {
    private final @NotNull File configFile;
    private final @NotNull String identify;
    private String rawContent;
    private YamlConfiguration configurationInStream = null;
    private YamlConfiguration configuration = new YamlConfiguration();

    public Config(@NotNull String identify, @Nullable InputStream cfgStream, @NotNull File configFile) {
        Validate.notNull(identify, "identify cannot be null!");
        Validate.notEmpty(identify, "identify cannot be empty!");
        Validate.notNull(configFile, "config file cannot be null!");

        this.identify = identify;
        this.configFile = configFile;
        if (cfgStream != null) {
            try {
                rawContent = Streams.read(cfgStream, StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.configurationInStream = YamlConfiguration.loadConfiguration(new InputStreamReader(cfgStream, StandardCharsets.UTF_8));
        }

        ConfigManager.addConfig(identify, this);
    }

    public @NotNull String getIdentify() {
        return identify;
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

    public void save_WithComment() {
        try {
            Streams.save(saveToStr_withComment(), configFile);
        } catch (IOException e) {
            GlobalConsoleSender.err("could not save config into disk file '" + configFile + "'.\n " +
                    "cause: " + e.getLocalizedMessage());
        }
    }

    public void set(String path, Object val) {
        configuration.set(path, val);
    }

    public void createSection(String path) {
        if (this.configuration.getConfigurationSection(path) == null) {
            this.configuration.createSection(path);
            GlobalConsoleSender.debugInfo("create section for config '" + identify + "', path: " + path);
        }
    }

    // not supported to set a section to null
    public @NotNull String saveToStr_withComment() {
        StringBuilder sb = new StringBuilder();
        Map<Integer, String> toStringMap = new LinkedHashMap<>();
        String[] saveToString = configuration.saveToString().split("\n");
        int i = 1;
        for (String s : saveToString) {
            if (!s.startsWith("#")) {
                toStringMap.put(i, s);
                i ++;
            }
        }
        try {
            List<String> stringList = Files.readAllLines(configFile.toPath());
            int z = 1;
            for (String s : stringList) {
                if (s.trim().startsWith("#") || s.trim().isEmpty()) {
                    sb.append(s);
                } else {
                    sb.append(toStringMap.get(z));
                    z++;
                }
                sb.append("\n");
            }
        } catch (IOException e) {
            GlobalConsoleSender.err("could not read disk file '" + configFile + "'.\n " +
                    "cause: " + e.getLocalizedMessage());
        }
        return sb.toString();
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
