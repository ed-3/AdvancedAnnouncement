package top.ed333.mcplugin.advancedann.bukkit.announcement;

import top.ed333.mcplugin.advancedann.bukkit.AdvancedAnnouncement;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import top.ed333.mcplugin.advancedann.bukkit.utils.ProtocolUtils;
import top.ed333.mcplugin.advancedann.common.utils.TextHandler;

public class BossBarRunnable extends BukkitRunnable {
    private final AdvancedBossBar bar;
    private final Player sendTo;
    private final BukkitTask updateTask;
    private final BukkitTask progressTask;

    BossBarRunnable(AdvancedBossBar bar, Player sendTo) {
        this.bar = bar;
        this.sendTo = sendTo;

        // time of this bar start to show
        // when start time is smaller than 0, then appear immediately
        long startTime = (long) (bar.settings.nextDelay - (bar.settings.delaySec + bar.settings.stay)) * 20L;
        new BukkitRunnable() {
            @Override
            public void run() {
                bar.bar.addPlayer(sendTo);
            }
        }.runTaskLaterAsynchronously(AdvancedAnnouncement.INSTANCE, startTime < 0 ? 0 : startTime);

        // deal update bar setting
        if (bar.settings.update != -1) {
            updateTask = new BukkitRunnable() {
                @Override
                public void run() {
                    boolean isLegacy = ProtocolUtils.isPlayerLegacyVer(sendTo);
                    if (ProtocolUtils.isLegacyServer()) {
                        isLegacy = true;
                    }
                    bar.bar.setTitle(TextHandler.handleColor(bar.title, isLegacy));
                }
            }.runTaskTimerAsynchronously(
                    AdvancedAnnouncement.INSTANCE,
                    startTime < 0 ? 0 : startTime,
                    (long) (bar.settings.update * 20L)
            );
        } else {
            updateTask = null;
        }

        // deal processing bar setting
        if (bar.settings.progress) {
            progressTask = new BukkitRunnable() {
                double remain = bar.settings.stay;
                double progress = 1.0;

                @Override
                public void run() {
                    progress = remain / bar.settings.stay;
                    remain = remain - 0.05; // 0.05 is 1 tick.
                    bar.bar.setProgress(progress);
                }
            }.runTaskTimerAsynchronously(AdvancedAnnouncement.INSTANCE, startTime < 0 ? 0 : startTime, 1);
        } else {
            progressTask = null;
        }
    }

    @Override
    public void run() {
        if (updateTask != null) updateTask.cancel();
        if (progressTask != null) progressTask.cancel();
        bar.bar.removePlayer(sendTo);
        bar.bar.setProgress(1);
    }
}
