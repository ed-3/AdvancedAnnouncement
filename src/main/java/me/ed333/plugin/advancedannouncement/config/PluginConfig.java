package me.ed333.plugin.advancedannouncement.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;

public class PluginConfig extends Config {
    public PluginConfig(@NotNull String identify, @Nullable InputStream cfgStream, @NotNull File configFile) {
        super(identify, cfgStream, configFile);
    }
}
