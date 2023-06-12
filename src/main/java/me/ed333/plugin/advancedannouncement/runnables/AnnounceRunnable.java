package me.ed333.plugin.advancedannouncement.runnables;

import me.ed333.plugin.advancedannouncement.AdvancedAnnouncement;
import me.ed333.plugin.advancedannouncement.config.ConfigKeys;
import me.ed333.plugin.advancedannouncement.announcement.Announcement;
import me.ed333.plugin.advancedannouncement.announcement.AnnouncementManager;
import me.ed333.plugin.advancedannouncement.announcement.PreTypeAnnouncement;
import me.ed333.plugin.advancedannouncement.utils.GlobalConsoleSender;
import me.ed333.plugin.advancedannouncement.utils.LangUtils;
import org.bukkit.scheduler.BukkitRunnable;

public class AnnounceRunnable extends BukkitRunnable {

    private int lastIndex = 0;
    @Override
    public void run() {
        Announcement announcement;
        if (ConfigKeys.RANDOM) {
            announcement = AnnouncementManager.randomNext();
            while (announcement instanceof PreTypeAnnouncement) {
                announcement = AnnouncementManager.randomNext();
            }
        } else {
            if (lastIndex >= AnnouncementManager.loadedAnnouncements.size()) lastIndex = 0;
            announcement = AnnouncementManager.forIndex(lastIndex);
            lastIndex++;
            if (announcement instanceof PreTypeAnnouncement) {
                announcement = AnnouncementManager.forIndex(lastIndex);
            }
        }
        announcement.broadcast();
        lastIndex = announcement.getIndex();

        GlobalConsoleSender.debugInfo("Sending announcement: " + announcement.getName());
        AdvancedAnnouncement.announceTask = new AnnounceRunnable().runTaskLaterAsynchronously(AdvancedAnnouncement.INSTANCE, announcement.delay()*20L);
    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        super.cancel();
        GlobalConsoleSender.info(LangUtils.getLangText("ann-task-stop"));
    }
}
