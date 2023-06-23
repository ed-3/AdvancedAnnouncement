package top.ed333.mcplugin.advancedann.bungee;

import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import top.ed333.mcplugin.advancedann.bungee.announcement.Announcement;
import top.ed333.mcplugin.advancedann.bungee.announcement.AnnouncementManager;
import top.ed333.mcplugin.advancedann.bungee.cmd.AA_Cmd;
import top.ed333.mcplugin.advancedann.bungee.config.ConfigKeys;
import net.md_5.bungee.api.plugin.Plugin;
import top.ed333.mcplugin.advancedann.bungee.runnable.AnnRunnable;
import top.ed333.mcplugin.advancedann.bungee.utils.ConsoleSender;
import top.ed333.mcplugin.advancedann.bungee.utils.LangUtils;
import top.ed333.mcplugin.advancedann.bungee.utils.SchedulerUtils;

import java.io.File;
import java.util.concurrent.TimeUnit;

public final class AdvancedAnnouncement extends Plugin {
    public static ScheduledTask announceTask = null;
    public static AdvancedAnnouncement INSTANCE;
    public static final File DATA_FOLDER = new File("./plugins/AdvancedAnnouncement");
    private static int annLastIndex = 0;

    @Override
    public void onEnable() {
        INSTANCE = this;

        Command pluginCmd = new AA_Cmd();
        getProxy().getPluginManager().registerCommand(this, pluginCmd);
        getProxy().getPluginManager().reg

        BootStrap.initConfig();
        ConfigKeys.initKey();

        // bstats
        if (ConfigKeys.BSTATS) {
            BootStrap.metric();
        }

        announceTask = SchedulerUtils.scheduleNew(new AnnRunnable(), 60*1000, TimeUnit.MILLISECONDS);
        ConsoleSender.setDEBUG(ConfigKeys.DEBUG);
        ConsoleSender.info(LangUtils.getLangText("ann-task-start"));
    }

    @Override
    public void onDisable() {
        INSTANCE = null;

        if (announceTask != null) {
            announceTask.cancel();
            ConsoleSender.info(LangUtils.getLangText("ann-task-stop"));
        }

        ConsoleSender.info(LangUtils.getLangText("plugin-unload"));
    }
}
