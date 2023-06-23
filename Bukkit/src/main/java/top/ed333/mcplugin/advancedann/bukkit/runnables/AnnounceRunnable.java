package top.ed333.mcplugin.advancedann.bukkit.runnables;

import top.ed333.mcplugin.advancedann.bukkit.announcement.Announcement;
import top.ed333.mcplugin.advancedann.bukkit.AdvancedAnnouncement;
import top.ed333.mcplugin.advancedann.bukkit.config.ConfigKeys;
import top.ed333.mcplugin.advancedann.bukkit.announcement.AnnouncementManager;
import top.ed333.mcplugin.advancedann.bukkit.utils.GlobalConsoleSender;
import top.ed333.mcplugin.advancedann.bukkit.utils.LangUtils;
import org.bukkit.scheduler.BukkitRunnable;

public class AnnounceRunnable extends BukkitRunnable {
    private static int lastIndex = 0;

    @Override
    public void run() {
        Announcement announcement;
        if (ConfigKeys.RANDOM) {
            announcement = AnnouncementManager.randomNext();
        } else {
            if (lastIndex >= AnnouncementManager.loadedAnnouncements.size()) {
                lastIndex = 0;
            }
            announcement = AnnouncementManager.forIndex(lastIndex);
            lastIndex = announcement.getIndex() + 1;
        }
        announcement.broadcast();

        GlobalConsoleSender.debugInfo("Sending announcement: " + announcement.getName() + ", announce: " + announcement.getName() + ", index: " + announcement.getIndex());
        AdvancedAnnouncement.announceTask = new AnnounceRunnable().runTaskLaterAsynchronously(AdvancedAnnouncement.INSTANCE, announcement.delay()*20L);
    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        super.cancel();
        GlobalConsoleSender.info(LangUtils.getLangText("ann-task-stop"));
    }
}
