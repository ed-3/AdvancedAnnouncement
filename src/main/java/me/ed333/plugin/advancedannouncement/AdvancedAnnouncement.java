package me.ed333.plugin.advancedannouncement;

import me.ed333.plugin.advancedannouncement.cmd.AA_CommandCompleter;
import me.ed333.plugin.advancedannouncement.cmd.AA_CommandExecutor;
import me.ed333.plugin.advancedannouncement.config.ConfigManager;
import me.ed333.plugin.advancedannouncement.runnables.AnnounceRunnable;
import me.ed333.plugin.advancedannouncement.runnables.PreAnnRunnable;
import me.ed333.plugin.advancedannouncement.utils.GlobalConsoleSender;
import me.ed333.plugin.advancedannouncement.utils.LangUtils;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;

public final class AdvancedAnnouncement extends JavaPlugin {
    public static BukkitTask announceTask = null;

    public static File DATA_FOLDER;
    public static AdvancedAnnouncement INSTANCE;

    public AdvancedAnnouncement() {
        INSTANCE = this;
        DATA_FOLDER = getDataFolder();
    }

    @Override
    public void onEnable() {
        Bootstrap.initCfg();
        ConfigKeys.initKey(ConfigManager.getConfigFile("config"));
        Bootstrap.loadComponentBlock();
        Bootstrap.loadAnnouncements();

        // bStats
        if (ConfigKeys.BSTATS) {
            Bootstrap.collectData();
        }

        announceTask = new AnnounceRunnable().runTaskLaterAsynchronously(this, 600);
        GlobalConsoleSender.setDEBUG(ConfigKeys.DEBUG);
        GlobalConsoleSender.info(LangUtils.getLangText("ann-task-start"));

        String cmdName = "autoannouncement";
        getCommand(cmdName).setExecutor(new AA_CommandExecutor());
        getCommand(cmdName).setTabCompleter(new AA_CommandCompleter());

        Bootstrap.checkUpdate();
    }

    @Override
    public void onDisable() {
        INSTANCE = null;

        PreAnnRunnable.preAnnRunnableList.forEach(PreAnnRunnable::cancel);
        if (announceTask != null) {
            announceTask.cancel();
            GlobalConsoleSender.info(LangUtils.getLangText("ann-task-cancel"));
        }
    }
}
