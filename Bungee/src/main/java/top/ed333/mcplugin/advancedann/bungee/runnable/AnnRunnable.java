package top.ed333.mcplugin.advancedann.bungee.runnable;

import top.ed333.mcplugin.advancedann.bungee.AdvancedAnnouncement;
import top.ed333.mcplugin.advancedann.bungee.announcement.Announcement;
import top.ed333.mcplugin.advancedann.bungee.announcement.AnnouncementManager;
import top.ed333.mcplugin.advancedann.bungee.config.ConfigKeys;
import top.ed333.mcplugin.advancedann.bungee.utils.ConsoleSender;
import top.ed333.mcplugin.advancedann.bungee.utils.SchedulerUtils;

import java.util.concurrent.TimeUnit;

public class AnnRunnable implements Runnable {
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

        ConsoleSender.debugInfo("Sending announcement: " + announcement.getName() + ", announce: " + announcement.getName() + ", index: " + announcement.getIndex());
        AdvancedAnnouncement.announceTask = SchedulerUtils.scheduleNew(this, announcement.getDelay()*1000L, TimeUnit.MILLISECONDS);
    }
}
