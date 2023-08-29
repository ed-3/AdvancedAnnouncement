package top.ed333.mcplugin.advancedann.bungee;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.config.Configuration;
import top.ed333.mcplugin.advancedann.bungee.announcement.*;
import top.ed333.mcplugin.advancedann.bungee.config.Config;
import top.ed333.mcplugin.advancedann.bungee.config.ConfigKeys;
import top.ed333.mcplugin.advancedann.bungee.config.ConfigManager;
import top.ed333.mcplugin.advancedann.bungee.utils.ConsoleSender;
import top.ed333.mcplugin.advancedann.bungee.utils.LangUtils;
import top.ed333.mcplugin.advancedann.common.announcement.AnnouncementType;
import top.ed333.mcplugin.advancedann.common.components.ComponentManager;
import top.ed333.mcplugin.advancedann.common.components.ComponentType;
import top.ed333.mcplugin.advancedann.common.components.JsonBlock;
import top.ed333.mcplugin.advancedann.common.components.NormalBlock;
import top.ed333.mcplugin.advancedann.common.utils.Streams;
import top.ed333.mcplugin.advancedann.common.utils.TimeHandler;
import top.ed333.mcplugin.advancedann.common.utils.Updater;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BootStrap {
    static void printIcon() {
        try {
            String icon = Streams.read(getResource("icon.txt"), StandardCharsets.UTF_8);
            /*
            Version code should be processed in build.gradle, but an unclear error occurred while
            running processResources task, so this compromise approach was used.
             */
            icon = icon.replace("${version}", AdvancedAnnouncement.INSTANCE.getDescription().getVersion());

            for (String line : icon.split("\n")) ConsoleSender.info(line);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

        metrics.addCustomChart(pie);
    }

    public static void loadComponentBlock() {
        Configuration componentCfg = ConfigManager.getConfig("components").getConfig();
        for (String key : componentCfg.getKeys()) {
            if (ComponentManager.blocks.containsKey(key)) continue;

            Configuration componentSection = componentCfg.getSection(key);
            if (componentSection == null) continue;

            ComponentType type;
            String componentTypeName = componentSection.getString("type", "NORMAL");
            try {
                type = ComponentType.valueOf(componentTypeName);
            } catch (IllegalArgumentException e) {
                ConsoleSender.warn(LangUtils.parseLang_withPrefix("load.load-comonents-unknown", componentTypeName, key));
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
        ConsoleSender.info(LangUtils.parseLang("load.load-components-done", ComponentManager.blocks.size()));
    }

    public static void loadAnnouncements() {
        Configuration announceCfg = ConfigManager.getConfig("announcements").getConfig();

        Configuration announceSection = announceCfg.getSection("announcements");
        int index = 0;
        for (String annName : announceSection.getKeys()) {
            // check if there is already loaded a similar announcement.
            if (AnnouncementManager.forName(annName) != null) continue;

            Configuration section = announceSection.getSection(annName);
            if (section == null) continue;
            String permission = section.getString("permission");
            int delay = TimeHandler.parse(section.getString("delay", "60s"));
            AnnouncementType type;
            try {
                type = AnnouncementType.valueOf(section.getString("type"));
            } catch (IllegalArgumentException e) {
                ConsoleSender.warn(LangUtils.parseLang_withPrefix("load.ann-type-unknown", annName, section.getString("type")));
                continue;
            }

            List<String> contents = section.getStringList("content");

            Announcement announcement = null;
            // for title type
            double stay = section.getDouble("stay", 3);
            double fadeIn = section.getDouble("fadeIn", 2);
            double fadeOut = section.getDouble("fadeOut", 1);
            // for subtitle
            double sub_fadeIn = section.getDouble("sub-fadeIn", 3);
            double sub_stay = section.getDouble("sub-stay", 2);
            double sub_fadeout = section.getDouble("sub-fadeout", 1);

            switch (type) {
                case BOSSBAR_KEEP:
                    announcement = new BossBarKeepType(index, annName, contents, permission, -1);
                    break;
                case CHAT:
                    announcement = new ChatType(index, annName, contents, permission, delay);
                    break;
                case MULTIPLE_LINE_BOSS_BAR:
                    announcement = new LinedBossBarType(index, annName, contents, permission, delay, stay);
                    break;
                case BOSS_BAR:
                    announcement = new BossBarType(index, annName, contents, permission, delay);
                    break;
                case ACTION_BAR:
                    announcement = new ActionBarType(index, annName, contents, permission, delay);
                    break;
                case TITLE:
                    if (contents.size() == 0) {
                        ConsoleSender.warn(LangUtils.parseLang_withPrefix("load.title-type-empty", annName));
                        break;
                    } else if (contents.size() == 1) {
                        ConsoleSender.warn(LangUtils.parseLang_withPrefix("load.title-type-one-line", annName));
                        contents.add(" ");
                    } else if (contents.size() >= 3) {
                        ConsoleSender.warn(LangUtils.parseLang_withPrefix("load.title-type-three-or-more", annName));
                    }

                    announcement = new TitleType(
                            index, annName, contents, permission, delay,
                            fadeIn, stay, fadeOut,
                            sub_fadeIn, sub_stay, sub_fadeout
                    );

            }
            if (announcement != null) {
                AnnouncementManager.loadedAnnouncements.add(announcement);
            }

            index++;
        }
        ConsoleSender.info(LangUtils.parseLang("load.load-announcements-done", AnnouncementManager.loadedAnnouncements.size()));
    }

    static void checkUpdate() {
        ConsoleSender.info(LangUtils.getLangText("update.check"));
        String updateUrl = "https://api.github.com/repos/ed-3/AdvancedAnnouncement/releases/latest";
        if (ConfigKeys.UPDATE_SOURCE == 0) {
            updateUrl = "https://gitee.com/api/v5/repos/ed3/advanced-announcement/releases/latest";
        }

        JsonParser parser = new JsonParser();

        try {
            // send request
            URL url = new URL(updateUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);

            int statusCode = connection.getResponseCode();
            InputStreamReader inReader = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(inReader);
            String lines;
            StringBuilder responseBody = new StringBuilder();
            while ((lines = reader.readLine()) != null) {
                responseBody.append(lines);
            }

            reader.close();
            inReader.close();

            if (statusCode != 200) {
                ConsoleSender.warn(LangUtils.getLangText("update.check-exception") + "HTTP: " + statusCode);
                throw new Updater.InvalidVersionException();
            }

            // compare version
            JsonObject object = parser.parse(responseBody.toString()).getAsJsonObject();
            String tag = object.get("tag_name").getAsString();
            Updater.Version latestVer = Updater.Version.parse(tag);
            Updater.Version pluginVer = Updater.Version.parse(AdvancedAnnouncement.INSTANCE.getDescription().getVersion());
            boolean result = latestVer.isNewer(pluginVer);
            if (!latestVer.equals(pluginVer)) {
                if (result) {
                    ConsoleSender.warn(LangUtils.parseLang("update.has-update-line1", pluginVer, latestVer));
                    ConsoleSender.warn(LangUtils.getLangText("update.has-update-line2"));
                } else {
                    ConsoleSender.info(LangUtils.getLangText("update.check-latest"));
                }
            } else {
                ConsoleSender.info(LangUtils.getLangText("update.check-latest"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Updater.InvalidVersionException ignored) {}
    }
}
