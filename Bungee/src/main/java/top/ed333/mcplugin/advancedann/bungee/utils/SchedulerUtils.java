package top.ed333.mcplugin.advancedann.bungee.utils;

import net.md_5.bungee.api.scheduler.ScheduledTask;
import top.ed333.mcplugin.advancedann.bungee.AdvancedAnnouncement;

import java.util.concurrent.TimeUnit;

public class SchedulerUtils {

    public static ScheduledTask scheduleNew(Runnable runnable, long delay, TimeUnit timeUnit) {
        return AdvancedAnnouncement.INSTANCE.getProxy().getScheduler().schedule(
                AdvancedAnnouncement.INSTANCE,
                runnable,
                delay,
                timeUnit
        );
    }

    public static ScheduledTask scheduleNewTimer(Runnable runnable, long delay, long period, TimeUnit timeUnit) {
        return AdvancedAnnouncement.INSTANCE.getProxy().getScheduler().schedule(
                AdvancedAnnouncement.INSTANCE,
                runnable,
                delay,
                period,
                timeUnit
        );
    }

    public static ScheduledTask runAsync(Runnable runnable) {
        return AdvancedAnnouncement.INSTANCE.getProxy().getScheduler().runAsync(
                AdvancedAnnouncement.INSTANCE,
                runnable
        );
    }
}
