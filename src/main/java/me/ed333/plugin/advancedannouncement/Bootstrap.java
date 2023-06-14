package me.ed333.plugin.advancedannouncement;

import me.ed333.plugin.advancedannouncement.announcement.*;
import me.ed333.plugin.advancedannouncement.components.ComponentManager;
import me.ed333.plugin.advancedannouncement.components.ComponentType;
import me.ed333.plugin.advancedannouncement.components.JsonBlock;
import me.ed333.plugin.advancedannouncement.components.NormalBlock;
import me.ed333.plugin.advancedannouncement.config.Config;
import me.ed333.plugin.advancedannouncement.config.ConfigManager;
import me.ed333.plugin.advancedannouncement.utils.GlobalConsoleSender;
import me.ed333.plugin.advancedannouncement.utils.LangUtils;
import me.ed333.plugin.advancedannouncement.utils.TimeHandler;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bootstrap {

    static void initCfg() {
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

        Config config = ConfigManager.getConfig("config");
        String translation = config.getConfiguration().getString("translation");
        Config c = new Config(
                "lang",
                getResource("translations/" + translation + ".yml"),
                new File(DATA_FOLDER, "translations/" + translation + ".yml")
        );
        ConfigManager.checkFile(c);
        c.load();
    }

    private static InputStream getResource(String path) {
        return AdvancedAnnouncement.INSTANCE.getResource(path);
    }

    static void metric() {
        final int pluginID = 17508;
        final Metrics metrics = new Metrics(AdvancedAnnouncement.INSTANCE, pluginID);

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
        metrics.addCustomChart(pie);
    }

    public static void loadComponentBlock() {
        YamlConfiguration componentCfg = ConfigManager.getConfig("components").getConfiguration();
        for (String key : componentCfg.getKeys(false)) {
            if (ComponentManager.blocks.containsKey(key)) continue;

            ConfigurationSection componentSection = componentCfg.getConfigurationSection(key);
            if (componentSection == null) continue;

            ComponentType type;
            String componentTypeName = componentSection.getString("type");
            try {
                type = ComponentType.valueOf(componentTypeName);
            } catch (IllegalArgumentException e) {
                GlobalConsoleSender.err(LangUtils.parseLang_withPrefix("load.load-comonents-unknown", componentTypeName, key));
                continue;
            }

            switch (type) {
                case NORMAL:
                    String text = componentSection.getString("text");
                    // load click action
                    String clickActionName = componentSection.getString("onClick.action", "SUGGEST_COMMAND");
                    String clickVal = componentSection.getString("onClick.value", "/say hello! this is a example action.");
                    ClickEvent.Action clickAction = ClickEvent.Action.valueOf(clickActionName);
                    // hover
                    String hover_value = componentSection.getString("hover-value", "{#f4114a->#20d820}An example hover text. \n {rainbow}you can use multiple lines.");

                    NormalBlock nb = new NormalBlock(key, text, hover_value, clickAction, clickVal);
                    ComponentManager.blocks.put(key, nb);
                    break;
                case JSON:
                    String jsonContent = componentSection.getString("content").replaceAll("\n", "").replaceAll("  ", "");
                    JsonBlock jb = new JsonBlock(key, jsonContent);
                    ComponentManager.blocks.put(key, jb);
            }
        }
        GlobalConsoleSender.info(LangUtils.parseLang("load.load-components-done", ComponentManager.blocks.size()));
    }

    public static void loadAnnouncements() {
        ConfigurationSection announceCfg = ConfigManager.getConfig("announcements").getConfiguration();
        List<String> defaultWorlds = announceCfg.getStringList("Settings.enabled_worlds");

        ConfigurationSection announceSection = announceCfg.getConfigurationSection("announcements");
        int index = 0;
        for (String annName : announceSection.getKeys(false)) {
            // check if there is already loaded a similar announcement. 
            if (AnnouncementManager.forName(annName) != null) continue;

            ConfigurationSection section = announceSection.getConfigurationSection(annName);
            if (section == null) continue;
            String permission = section.getString("permission");
            int delay = TimeHandler.parse(section.getString("delay", "60s"));
            AnnouncementType type;
            try {
                type = AnnouncementType.valueOf(section.getString("type"));
            } catch (IllegalArgumentException e) {
                GlobalConsoleSender.warn(LangUtils.parseLang_withPrefix("load.ann-type-unknown", annName, section.getString("type")));
                continue;
            }

            List<String> contents = section.getStringList("content");
            List<String> worlds = section.getStringList("worlds");

            Announcement announcement = null;
            // for title type
            double stay = section.getDouble("stay", 3);
            double fadeIn = section.getDouble("fadeIn", 0.2);
            double fadeOut = section.getDouble("fadeOut", 1);
            // for subtitle
            double sub_fadeIn = section.getDouble("sub-fadeIn", 3);
            double sub_stay = section.getDouble("sub-stay", 0.2);
            double sub_fadeout = section.getDouble("sub-fadeout", 1);

            switch (type) {
                case CHAT:
                    announcement = new ChatType(index, annName, permission, delay, contents, worlds.isEmpty() ? defaultWorlds : worlds);
                    break;
                case MULTIPLE_LINE_BOSS_BAR:
                    announcement = new LinedBossBarType(index, annName, permission, delay, stay, contents, worlds.isEmpty() ? defaultWorlds : worlds);
                    break;
                case BOSS_BAR:
                    announcement = new BossBarType(index, annName, permission, delay, contents, worlds.isEmpty() ? defaultWorlds : worlds);
                    break;
                case ACTION_BAR:
                    announcement = new ActionBarType(index, annName, permission, delay, contents, worlds.isEmpty() ? defaultWorlds : worlds);
                    break;
                case TITLE:
                    if (contents.size() == 0) {
                        GlobalConsoleSender.warn(LangUtils.parseLang_withPrefix("load.title-type-empty", annName));
                        break;
                    } else if (contents.size() == 1) {
                        GlobalConsoleSender.warn(LangUtils.parseLang_withPrefix("load.title-type-one-line", annName));
                        contents.add(" ");
                    } else if (contents.size() >= 3) {
                        GlobalConsoleSender.warn(LangUtils.parseLang_withPrefix("title-type-three-or-more", annName));
                    }

                    announcement = new TitleType(
                            index, annName, permission, delay, contents, worlds.isEmpty() ? defaultWorlds : worlds,
                            fadeIn, stay, fadeOut,
                            sub_fadeIn, sub_stay, sub_fadeout
                    );

            }
            if (announcement != null) {
                AnnouncementManager.loadedAnnouncements.add(announcement);
            }

            index++;
        }
        GlobalConsoleSender.info(LangUtils.parseLang("load.load-announcements-done", AnnouncementManager.loadedAnnouncements.size()));
    }

}
