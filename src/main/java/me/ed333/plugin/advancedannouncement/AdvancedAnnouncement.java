package me.ed333.plugin.advancedannouncement;

import me.ed333.plugin.advancedannouncement.cmd.AA_CommandCompleter;
import me.ed333.plugin.advancedannouncement.cmd.AA_CommandExecutor;
import me.ed333.plugin.advancedannouncement.config.Config;
import me.ed333.plugin.advancedannouncement.config.ConfigManager;
import me.ed333.plugin.advancedannouncement.config.PluginConfig;
import me.ed333.plugin.advancedannouncement.instances.announcement.*;
import me.ed333.plugin.advancedannouncement.instances.component.TextComponentBlock;
import me.ed333.plugin.advancedannouncement.runnables.AnnounceRunnable;
import me.ed333.plugin.advancedannouncement.runnables.PreAnnRunnable;
import me.ed333.plugin.advancedannouncement.utils.GlobalConsoleSender;
import me.ed333.plugin.advancedannouncement.utils.LangUtils;
import me.ed333.plugin.advancedannouncement.utils.TimeHandler;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

public final class AdvancedAnnouncement extends JavaPlugin {
    public BukkitTask announceTask = null;

    private final File DATA_FOLDER = getDataFolder();
    public static AdvancedAnnouncement INSTANCE;

    public AdvancedAnnouncement() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        initCfg();
        ConfigKeys.initCfg(ConfigManager.getConfigFile("config"));
        loadComponentBlock();
        loadAnnouncements();

        announceTask = new AnnounceRunnable().runTaskLaterAsynchronously(this, 600);
        GlobalConsoleSender.setDEBUG(ConfigKeys.DEBUG);
        GlobalConsoleSender.info("§6Announce task was started.");

        getCommand("autoannouncement").setExecutor(new AA_CommandExecutor());
        getCommand("autoannouncement").setTabCompleter(new AA_CommandCompleter());
    }

    @Override
    public void onDisable() {
        INSTANCE = null;

        PreAnnRunnable.preAnnRunnableList.forEach(PreAnnRunnable::cancel);
        if (announceTask != null) {
            announceTask.cancel();
            GlobalConsoleSender.info("§6Announce task was canceled.");
        }
    }

    public void loadComponentBlock() {
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
        GlobalConsoleSender.info(String.format("Loaded %s components.", TextComponentBlock.blocks.size()));
    }

    public void loadAnnouncements() {
        ConfigurationSection announceCfg = ConfigManager.getConfig("announcements").getConfiguration().getConfigurationSection("announcements");

        int index = 0;
        for (String key : announceCfg.getKeys(false)) {
            if (AnnouncementManager.forName(key) != null) continue;

            ConfigurationSection section = announceCfg.getConfigurationSection(key);
            if (section == null) continue;
            String permission = section.getString("permission");
            int delay = TimeHandler.parse(section.getString("delay", "60s"));
            AnnouncementType type;
            try {
                type = AnnouncementType.valueOf(section.getString("type"));
            } catch (IllegalArgumentException e) {
                GlobalConsoleSender.warn(LangUtils.parseLang_withPrefix("load.ann-type-unknown", key, section.getString("type")));
                continue;
            }

            List<String> contents = section.getStringList("content");

            Announcement announcement = null;
            switch (type) {
                case CHAT:
                    announcement = new ChatTypeAnnouncement(index, key, permission, delay, contents);
                    break;
                case BOSS_BAR:
                    announcement = new BossBarTypeAnnouncement(index, key, permission, delay, contents);
                    break;
                case ACTION_BAR:
                    announcement = new ActionBarTypeAnnouncement(index, key, permission, delay, contents);
                    break;
                case TITLE:
                    double fadeIn = section.getDouble("fadeIn", 0.2);
                    double stay = section.getDouble("stay", 3);
                    double fadeOut = section.getDouble("fadeOut", 1);

                    if (contents.size() == 0) {
                        GlobalConsoleSender.warn(LangUtils.parseLang_withPrefix("load.title-type-empty", key));
                        break;
                    } else if (contents.size() == 1) {
                        GlobalConsoleSender.warn(LangUtils.parseLang_withPrefix("load.title-type-one-line", key));
                        contents.add(" ");
                    } else if (contents.size() >= 3) {
                        GlobalConsoleSender.warn(LangUtils.parseLang_withPrefix("title-type-three-or-more", key));
                    }
                    announcement = new TitleTypeAnnouncement(index, key, permission, delay, contents, fadeIn, stay, fadeOut);
                    break;
                case PRE_ANNOUNCE:
                    Date dateTime;
                    try {
                        dateTime = TimeHandler.toDate(section.getString("date"));
                    } catch (ParseException e) {
                        LangUtils.parseLang("load.exception", key, e.getMessage());
                        continue;
                    }
                    AnnouncementType shownType;
                    try {
                        shownType = AnnouncementType.valueOf(section.getString("displayType"));
                    } catch (IllegalArgumentException e) {
                        GlobalConsoleSender.warn(LangUtils.parseLang_withPrefix("preann-shown-type-unknown", key, section.getString("displayType")));
                        continue;
                    }
                    if (shownType.equals(AnnouncementType.PRE_ANNOUNCE)) {
                        GlobalConsoleSender.warn(LangUtils.parseLang_withPrefix("preann-shown-type-equals", key));
                        continue;
                    }

                    announcement = new PreTypeAnnouncement(index, key, permission, delay, contents, dateTime, shownType);

                    new PreAnnRunnable(((PreTypeAnnouncement) announcement)).runTaskLaterAsynchronously(this, TimeHandler.getTimeSecRemain(dateTime) * 20L);
            }
            if (announcement != null) {
                AnnouncementManager.loadedAnnouncements.add(announcement);
            }

            index++;
        }
        GlobalConsoleSender.info(String.format("loaded %s announcements", AnnouncementManager.loadedAnnouncements.size()));
    }

    public void initCfg() {
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
}
