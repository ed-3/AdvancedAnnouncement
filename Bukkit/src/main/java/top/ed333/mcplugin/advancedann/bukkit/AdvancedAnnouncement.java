package top.ed333.mcplugin.advancedann.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import top.ed333.mcplugin.advancedann.bukkit.announcement.Announcement;
import top.ed333.mcplugin.advancedann.bukkit.announcement.AnnouncementManager;
import top.ed333.mcplugin.advancedann.bukkit.cmd.AA_CommandExecutor;
import top.ed333.mcplugin.advancedann.bukkit.config.ConfigKeys;
import top.ed333.mcplugin.advancedann.bukkit.config.ConfigManager;
import top.ed333.mcplugin.advancedann.bukkit.runnables.AnnounceRunnable;
import top.ed333.mcplugin.advancedann.bukkit.cmd.AA_CommandCompleter;
import top.ed333.mcplugin.advancedann.bukkit.utils.GlobalConsoleSender;
import top.ed333.mcplugin.advancedann.bukkit.utils.LangUtils;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import top.ed333.mcplugin.advancedann.bukkit.utils.ProtocolUtils;
import top.ed333.mcplugin.advancedann.common.announcement.AnnouncementType;

import java.io.File;

public final class AdvancedAnnouncement extends JavaPlugin implements Listener {
    public static BukkitTask announceTask = null;

    public static File DATA_FOLDER;
    public static AdvancedAnnouncement INSTANCE;

    public AdvancedAnnouncement() {
        INSTANCE = this;
        DATA_FOLDER = getDataFolder();
    }

    @Override
    public void onEnable() {
        BootStrap.printIcon();

        BootStrap.initCfg();
        ConfigKeys.initKey(ConfigManager.getConfigFile("config"));
        BootStrap.loadComponentBlock();
        BootStrap.loadAnnouncements();
        Bukkit.getPluginManager().registerEvents(this, this);

        // bStats
        if (ConfigKeys.BSTATS) {
            BootStrap.metric();
        }

        announceTask = new AnnounceRunnable().runTaskLaterAsynchronously(this, 600L);
        GlobalConsoleSender.setDEBUG(ConfigKeys.DEBUG);
        GlobalConsoleSender.info(LangUtils.getLangText("ann-task-start"));

        String cmdName = "advancedannouncement";
        getCommand(cmdName).setExecutor(new AA_CommandExecutor());
        getCommand(cmdName).setTabCompleter(new AA_CommandCompleter());

        if (ConfigKeys.UPDATE_CHECK) BootStrap.checkUpdate();
    }

    @Override
    public void onDisable() {
        INSTANCE = null;

        if (announceTask != null) {
            announceTask.cancel();
            GlobalConsoleSender.info(LangUtils.getLangText("ann-task-stop"));
        }

        GlobalConsoleSender.info(LangUtils.getLangText("plugin-unload"));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        for (Announcement ann : AnnouncementManager.loadedAnnouncements) {
            if (ann.type().equals(AnnouncementType.BOSSBAR_KEEP) &&
                    ann.getWorlds().contains(player.getWorld().getName())
            ) {
                ann.send(player, ProtocolUtils.canHandleRGB(player));
            }
        }
    }
}
