package top.ed333.mcplugin.advancedann.bungee;

import top.ed333.mcplugin.advancedann.bungee.announcement.Announcement;
import top.ed333.mcplugin.advancedann.bungee.announcement.AnnouncementManager;
import top.ed333.mcplugin.advancedann.bungee.announcement.AnnouncementType;
import top.ed333.mcplugin.advancedann.bungee.config.Config;
import top.ed333.mcplugin.advancedann.bungee.config.ConfigKeys;
import top.ed333.mcplugin.advancedann.bungee.config.ConfigManager;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class BootStrap {
    static void initConfig() {
        final File DATA_FOLDER = AdvancedAnnouncement.DATA_FOLDER;
        new Config(
                "config",
                getResource("config.yml"),
                new File(DATA_FOLDER, "config.yml")
        );

        new Config(
                "components",
                getResource("components.yml"),
                new File(DATA_FOLDER, "components.yml")
        );

        new Config(
                "announcements",
                getResource("announcements.yml"),
                new File(DATA_FOLDER, "announcements.yml")
        );
        ConfigManager.checkAllFile();
        ConfigManager.loadAll();

        Config cfg = ConfigManager.getConfig("config");
        String translation = cfg.getConfig().getString("translation");
        Config translateConfig = new Config(
                "lang",
                getResource("translations/" + translation + ".yml"),
                new File(DATA_FOLDER, "translations/" + translation + ".yml")
        );
        ConfigManager.checkFile(translateConfig);
        translateConfig.load();
    }

    private static InputStream getResource(String path) {
        return AdvancedAnnouncement.INSTANCE.getResourceAsStream(path);
    }

    static void metric() {
        final int pluginId = 18847;
        final Metrics metrics = new Metrics(AdvancedAnnouncement.INSTANCE, pluginId);
        final Metrics.AdvancedPie pie = new Metrics.AdvancedPie("ann_types", () -> {
            int chatCount = 0;
            int actionBarCount = 0;
            int bossBarCount = 0;
            int titleTypeCount = 0;
            int multipleBossBarCount = 0;
            for (Announcement ann : AnnouncementManager.loadedAnnouncements) {
                if (ann.type().equals(AnnouncementType.CHAT)) {
                    chatCount++;
                } else if (ann.type().equals(AnnouncementType.ACTION_BAR)) {
                    actionBarCount++;
                } else if (ann.type().equals(AnnouncementType.BOSS_BAR)) {
                    bossBarCount++;
                } else if (ann.type().equals(AnnouncementType.TITLE)) {
                    titleTypeCount++;
                }
                else if (ann.type().equals(AnnouncementType.MULTIPLE_LINE_BOSS_BAR)) {
                    multipleBossBarCount++;
                }
            }

            Map<String, Integer> data = new HashMap<>();
            data.put("CHAT", chatCount);
            data.put("ACTION_BAR", actionBarCount);
            data.put("BOSS_BAR", bossBarCount);
            data.put("TITLE", titleTypeCount);
            data.put("MULTIPLE_LINE_BOSS_BAR", multipleBossBarCount);
            return data;
        });
        final Metrics.SimpleBarChart chart = new Metrics.SimpleBarChart("lang using", () -> {
            Map<String, Integer> data = new HashMap<>();
            data.put(ConfigKeys.TRANSLATION, 1);
            return data;
        });

        metrics.addCustomChart(pie);
        metrics.addCustomChart(chart);
    }



}
