package top.ed333.mcplugin.advancedann.bungee;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.event.EventHandler;
import top.ed333.mcplugin.advancedann.bungee.announcement.Announcement;
import top.ed333.mcplugin.advancedann.bungee.announcement.AnnouncementManager;
import top.ed333.mcplugin.advancedann.bungee.cmd.AA_Cmd;
import top.ed333.mcplugin.advancedann.bungee.config.ConfigKeys;
import net.md_5.bungee.api.plugin.Plugin;
import top.ed333.mcplugin.advancedann.bungee.runnable.AnnRunnable;
import top.ed333.mcplugin.advancedann.bungee.utils.ConsoleSender;
import top.ed333.mcplugin.advancedann.bungee.utils.LangUtils;
import top.ed333.mcplugin.advancedann.bungee.utils.ProtocolUtils;
import top.ed333.mcplugin.advancedann.bungee.utils.SchedulerUtils;
import top.ed333.mcplugin.advancedann.common.announcement.AnnouncementType;

import java.io.File;
import java.util.concurrent.TimeUnit;

public final class AdvancedAnnouncement extends Plugin implements Listener {
    public static ScheduledTask announceTask = null;
    public static AdvancedAnnouncement INSTANCE;
    public static final File DATA_FOLDER = new File("./plugins/AdvancedAnnouncement");

    @Override
    public void onEnable() {
        INSTANCE = this;
        BungeeCord.getInstance().getPluginManager().registerListener(this, this);
        BootStrap.printIcon();

        Command pluginCmd = new AA_Cmd();
        getProxy().getPluginManager().registerCommand(this, pluginCmd);

        BootStrap.initConfig();
        ConfigKeys.initKey();
        BootStrap.loadComponentBlock();
        BootStrap.loadAnnouncements();

        // bstats
        if (ConfigKeys.BSTATS) {
            BootStrap.metric();
        }

        if (ConfigKeys.AUTO_RUN) {
            announceTask = SchedulerUtils.scheduleNew(new AnnRunnable(), 60 * 1000, TimeUnit.MILLISECONDS);
            ConsoleSender.info(LangUtils.getLangText("ann-task-start"));
        }
        ConsoleSender.setDEBUG(ConfigKeys.DEBUG);

        if (ConfigKeys.UPDATE_CHECK) BootStrap.checkUpdate();
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

    @EventHandler
    public void onPlayerLogin(PostLoginEvent event) {
        for (Announcement ann : AnnouncementManager.loadedAnnouncements) {
            if (ann.type().equals(AnnouncementType.BOSSBAR_KEEP)) {
                ann.send(event.getPlayer(), ProtocolUtils.canHandleRGB(event.getPlayer()));
            }
        }
    }
}
