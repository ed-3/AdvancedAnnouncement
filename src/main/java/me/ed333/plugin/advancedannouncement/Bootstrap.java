package me.ed333.plugin.advancedannouncement;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.ed333.plugin.advancedannouncement.config.Config;
import me.ed333.plugin.advancedannouncement.config.PluginConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import me.ed333.plugin.advancedannouncement.config.ConfigManager;
import me.ed333.plugin.advancedannouncement.instances.announcement.ActionBarTypeAnnouncement;
import me.ed333.plugin.advancedannouncement.instances.announcement.Announcement;
import me.ed333.plugin.advancedannouncement.instances.announcement.AnnouncementManager;
import me.ed333.plugin.advancedannouncement.instances.announcement.AnnouncementType;
import me.ed333.plugin.advancedannouncement.instances.announcement.BossBarTypeAnnouncement;
import me.ed333.plugin.advancedannouncement.instances.announcement.ChatTypeAnnouncement;
import me.ed333.plugin.advancedannouncement.instances.announcement.MultipleLineBossBarTypeAnnouncement;
import me.ed333.plugin.advancedannouncement.instances.announcement.PreTypeAnnouncement;
import me.ed333.plugin.advancedannouncement.instances.announcement.TitleTypeAnnouncement;
import me.ed333.plugin.advancedannouncement.instances.component.TextComponentBlock;
import me.ed333.plugin.advancedannouncement.runnables.PreAnnRunnable;
import me.ed333.plugin.advancedannouncement.utils.GlobalConsoleSender;
import me.ed333.plugin.advancedannouncement.utils.LangUtils;
import me.ed333.plugin.advancedannouncement.utils.TimeHandler;
import me.ed333.toolkits.utils.http.HttpRequest;
import me.ed333.toolkits.utils.http.HttpResponse;
import me.ed333.toolkits.utils.version.InvalidVersionException;
import me.ed333.toolkits.utils.version.Version;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;

public class Bootstrap {

    static void initCfg() {
        final File DATA_FOLDER = AdvancedAnnouncement.DATA_FOLDER;
        new PluginConfig(
                "config",
                getResource("config.yml"),
                new File(DATA_FOLDER, "config.yml")
        );
        new PluginConfig(
                "components",
                getResource("components.yml"),
                new File(DATA_FOLDER, "components.yml")
        );
        new PluginConfig(
                "announcements",
                getResource("announcements.yml"),
                new File(DATA_FOLDER, "announcements.yml")
        );
        ConfigManager.checkAllFile();
        ConfigManager.loadAll();

        Config config = ConfigManager.getConfig("config");
        String translation = config.getConfiguration().getString("translation");
        Config c = new PluginConfig(
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

    static void collectData() {
        final int pluginID = 17508;
        final Metrics metrics = new Metrics(AdvancedAnnouncement.INSTANCE, pluginID);

        final Metrics.AdvancedPie pie = new Metrics.AdvancedPie("ann_types", () -> {
            int chatCount = 0;
            int actionBarCount = 0;
            int bossBarCount = 0;
            int titleTypeCount = 0;
            int preTypeCount = 0;
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
                } else if (ann.type().equals(AnnouncementType.PRE_ANNOUNCE)) {
                    preTypeCount++;
                } else if (ann.type().equals(AnnouncementType.MULTIPLE_LINE_BOSS_BAR)) {
                    multipleBossBarCount++;
                }
            }

            Map<String, Integer> data = new HashMap<>();
            data.put("CHAT", chatCount);
            data.put("ACTION_BAR", actionBarCount);
            data.put("BOSS_BAR", bossBarCount);
            data.put("TITLE", titleTypeCount);
            data.put("PRE_ANNOUNCE", preTypeCount);
            data.put("MULTIPLE_LINE_BOSS_BAR", multipleBossBarCount);
            return data;
        });
        metrics.addCustomChart(pie);
    }

    static void loadComponentBlock() {
        YamlConfiguration componentCfg = ConfigManager.getConfig("components").getConfiguration();
        for (String key : componentCfg.getKeys(false)) {
            if (TextComponentBlock.blocks.containsKey(key)) continue;

            ConfigurationSection componentSection = componentCfg.getConfigurationSection(key);
            if (componentSection == null) continue;

            String text = componentSection.getString("text");
            String clickActionName = componentSection.getString("onClick.action", "SUGGEST_COMMAND");
            String hover_value = componentSection.getString("hover-value", "{#f4114a->#20d820}An example hover text. \n {rainbow}you can use multiple lines.");
            ClickEvent.Action clickAction = ClickEvent.Action.valueOf(clickActionName);
            String clickVal = componentSection.getString("onClick.value", "/say hello! this is a example action.");

            TextComponentBlock componentBlock = new TextComponentBlock(key, text);
            componentBlock.setClick(clickAction, clickVal);
            componentBlock.setHover(HoverEvent.Action.SHOW_TEXT, hover_value);
        }
        GlobalConsoleSender.info(LangUtils.parseLang("load.load-components-done", TextComponentBlock.blocks.size()));
    }

    static void checkUpdate() {
        GlobalConsoleSender.info(LangUtils.getLangText("update.check"));
        String updateUrl = "https://api.github.com/repos/ed-3/AdvancedAnnouncement/releases/latest";
        if (ConfigKeys.UPDATE_SOURCE == 0) {
            updateUrl = "https://gitee.com/api/v5/repos/ed3/advanced-announcement/releases/latest";
        }
        JsonParser parser = new JsonParser();
        HttpRequest request = new HttpRequest();
        try {
            HttpResponse response = request.send(updateUrl);
            JsonObject object = parser.parse(response.getBody()).getAsJsonObject();
            String tag = object.get("tag_name").getAsString();
            Version latestVer = Version.parse(tag);
            Version pluginVer = Version.parse(AdvancedAnnouncement.INSTANCE.getDescription().getVersion());
            boolean isLatest = latestVer.isNewer(pluginVer);
            if (isLatest) {
                GlobalConsoleSender.warn(LangUtils.parseLang("update.has-update-line1", pluginVer, latestVer));
                GlobalConsoleSender.warn(LangUtils.getLangText("update.has-update-line2"));
            } else {
                GlobalConsoleSender.info(LangUtils.getLangText("update.check-latest"));
            }
        } catch (IOException e) {
            GlobalConsoleSender.warn(LangUtils.getLangText("update.check-exception"));
            e.printStackTrace();
        } catch (InvalidVersionException ignored) {
        }
    }

    static void loadAnnouncements() {
        ConfigurationSection announceCfg = ConfigManager.getConfig("announcements").getConfiguration().getConfigurationSection("announcements");

        int index = 0;
        for (String annName : announceCfg.getKeys(false)) {
            // check if there is already loaded a similar announcement. 
            if (AnnouncementManager.forName(annName) != null) continue;

            ConfigurationSection section = announceCfg.getConfigurationSection(annName);
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

            Announcement announcement = null;
            // for title type
            double fadeIn = section.getDouble("fadeIn", 0.2);
            double stay = section.getDouble("stay", 3);
            double fadeOut = section.getDouble("fadeOut", 1);

            switch (type) {
                case CHAT:
                    announcement = new ChatTypeAnnouncement(index, annName, permission, delay, contents);
                    break;
                case MULTIPLE_LINE_BOSS_BAR:
                    announcement = new MultipleLineBossBarTypeAnnouncement(index, annName, permission, delay, stay, contents);
                    break;
                case BOSS_BAR:
                    announcement = new BossBarTypeAnnouncement(index, annName, permission, delay, contents);
                    break;
                case ACTION_BAR:
                    announcement = new ActionBarTypeAnnouncement(index, annName, permission, delay, contents);
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
                    announcement = new TitleTypeAnnouncement(index, annName, permission, delay, contents, fadeIn, stay, fadeOut);
                    break;
                case PRE_ANNOUNCE:
                    Date dateTime;
                    String dateStr = section.getString("date");
                    try {
                        dateTime = TimeHandler.toDate(dateStr);
                    } catch (ParseException e) {
                        GlobalConsoleSender.warn(LangUtils.parseLang("load.preann-time-format-err", annName, dateStr, ConfigKeys.DATE_FORMAT));
                        continue;
                    }

                    AnnouncementType shownType;
                    try {
                        shownType = AnnouncementType.valueOf(section.getString("displayType"));
                    } catch (IllegalArgumentException e) {
                        GlobalConsoleSender.warn(LangUtils.parseLang_withPrefix("preann-shown-type-unknown", annName, section.getString("displayType")));
                        continue;
                    }
                    if (shownType.equals(AnnouncementType.PRE_ANNOUNCE)) {
                        GlobalConsoleSender.warn(LangUtils.parseLang_withPrefix("preann-shown-type-equals", annName));
                        continue;
                    }

                    announcement = new PreTypeAnnouncement(
                        index, annName, permission, delay, contents, dateTime, shownType, 
                        // display type may be the title type, transfer the time
                        fadeIn, stay, fadeOut
                        );

                    new PreAnnRunnable(((PreTypeAnnouncement) announcement)).runTaskLaterAsynchronously(AdvancedAnnouncement.INSTANCE, TimeHandler.getTimeSecRemain(dateTime) * 20L);
            }
            if (announcement != null) {
                AnnouncementManager.loadedAnnouncements.add(announcement);
            }

            index++;
        }
        GlobalConsoleSender.info(LangUtils.parseLang("load.load-announcements-done", AnnouncementManager.loadedAnnouncements.size()));
    }

}
