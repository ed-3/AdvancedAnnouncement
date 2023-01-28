package me.ed333.plugin.advancedannouncement.cmd.sub;

import me.ed333.plugin.advancedannouncement.AdvancedAnnouncement;
import me.ed333.plugin.advancedannouncement.ConfigKeys;
import me.ed333.plugin.advancedannouncement.cmd.PermissionRequirement;
import me.ed333.plugin.advancedannouncement.config.Config;
import me.ed333.plugin.advancedannouncement.config.ConfigManager;
import me.ed333.plugin.advancedannouncement.config.PluginConfig;
import me.ed333.plugin.advancedannouncement.runnables.AnnounceRunnable;
import me.ed333.plugin.advancedannouncement.runnables.PreAnnRunnable;
import me.ed333.plugin.advancedannouncement.utils.GlobalConsoleSender;
import me.ed333.plugin.advancedannouncement.utils.LangUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class Reload {
    @PermissionRequirement("aa.command.reload")
    public static void callCmd(@NotNull CommandSender sender, String[] args) {

        PreAnnRunnable.preAnnRunnableList.forEach(PreAnnRunnable::cancel);
        if (AdvancedAnnouncement.announceTask != null) {
            AdvancedAnnouncement.announceTask.cancel();
            GlobalConsoleSender.info(LangUtils.getLangText("ann-task-cancel"));
        }
        ConfigManager.checkAllFile();
        ConfigManager.loadAll();
        ConfigKeys.initKey(ConfigManager.getConfigFile("config"));

        // load translation
        Config config = ConfigManager.getConfig("config");
        String translationName = config.getConfiguration().getString("translation");
        Config translationCfg = new PluginConfig(
                "lang",
                AdvancedAnnouncement.INSTANCE.getResource("translations/" + translationName + ".yml"),
                new File(AdvancedAnnouncement.DATA_FOLDER, "translations/" + translationName + ".yml")
        );
        ConfigManager.checkFile(translationCfg);
        translationCfg.load();

        // refresh translation
        LangUtils.refreshLang();
        System.out.println(ConfigManager.getIdentifies());
        System.out.println(ConfigManager.getConfig("lang").getConfigFile());

        AdvancedAnnouncement.INSTANCE.loadComponentBlock();
        AdvancedAnnouncement.INSTANCE.loadAnnouncements();

        AdvancedAnnouncement.announceTask = new AnnounceRunnable().runTaskLaterAsynchronously(AdvancedAnnouncement.INSTANCE, 600);
        GlobalConsoleSender.setDEBUG(ConfigKeys.DEBUG);
        GlobalConsoleSender.info(LangUtils.getLangText("ann-task-start"));
    }
}
