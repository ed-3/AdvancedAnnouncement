package me.ed333.plugin.advancedannouncement.cmd.sub;

import me.ed333.plugin.advancedannouncement.AdvancedAnnouncement;
import me.ed333.plugin.advancedannouncement.ConfigKeys;
import me.ed333.plugin.advancedannouncement.cmd.PermissionRequirement;
import me.ed333.plugin.advancedannouncement.config.ConfigManager;
import me.ed333.plugin.advancedannouncement.runnables.AnnounceRunnable;
import me.ed333.plugin.advancedannouncement.runnables.PreAnnRunnable;
import me.ed333.plugin.advancedannouncement.utils.GlobalConsoleSender;
import me.ed333.plugin.advancedannouncement.utils.LangUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class Reload {
    @PermissionRequirement("aa.command.reload")
    public static void callCmd(@NotNull CommandSender sender, String[] args) {

        PreAnnRunnable.preAnnRunnableList.forEach(PreAnnRunnable::cancel);
        if (AdvancedAnnouncement.announceTask != null) {
            AdvancedAnnouncement.announceTask.cancel();
            GlobalConsoleSender.info("§6Announce task was canceled.");
        }

        AdvancedAnnouncement.INSTANCE.initCfg();
        ConfigKeys.initCfg(ConfigManager.getConfigFile("config"));
        AdvancedAnnouncement.INSTANCE.loadComponentBlock();
        AdvancedAnnouncement.INSTANCE.loadAnnouncements();

        AdvancedAnnouncement.announceTask = new AnnounceRunnable().runTaskLaterAsynchronously(AdvancedAnnouncement.INSTANCE, 600);
        GlobalConsoleSender.setDEBUG(ConfigKeys.DEBUG);
        GlobalConsoleSender.info("§6Announce task was started.");
        LangUtils.refreshLang();
    }
}
