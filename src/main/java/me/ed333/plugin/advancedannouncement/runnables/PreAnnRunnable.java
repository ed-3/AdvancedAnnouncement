package me.ed333.plugin.advancedannouncement.runnables;

import me.ed333.plugin.advancedannouncement.instances.announcement.PreTypeAnnouncement;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.ArrayList;
import java.util.List;

public class PreAnnRunnable extends BukkitRunnable {
    public static List<PreAnnRunnable> preAnnRunnableList = new ArrayList<>();
    private final PreTypeAnnouncement announcement;

    public PreAnnRunnable(PreTypeAnnouncement announcement) {
        preAnnRunnableList.add(this);
        this.announcement = announcement;
    }

    @Override
    public void run() {
        announcement.broadcast();
    }
}
