package top.ed333.mcplugin.advancedann.bungee.announcement;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import top.ed333.mcplugin.advancedann.bungee.AdvancedAnnouncement;
import top.ed333.mcplugin.advancedann.bungee.objects.bossbar.BossBar;
import top.ed333.mcplugin.advancedann.bungee.utils.ProtocolUtils;
import top.ed333.mcplugin.advancedann.bungee.utils.SchedulerUtils;
import top.ed333.mcplugin.advancedann.bungee.utils.Serializer;
import top.ed333.mcplugin.advancedann.common.utils.TextHandler;

import java.util.concurrent.TimeUnit;

public class BossBarScheduler implements Runnable {
    private final AdvancedBossBar bar;
    private final ProxiedPlayer sendTo;
    private final ScheduledTask updateTask;
    private final ScheduledTask animationTask;

    public BossBarScheduler(AdvancedBossBar bar, ProxiedPlayer sendTo) {
        this.bar = bar;
        this.sendTo = sendTo;

        // time of this bar start to show
        // when start time is smaller than 0, then appear immediately
        long startTime = (long) (bar.settings.nextDelay - (bar.settings.delaySec + bar.settings.stay)) * 1000;
        SchedulerUtils.scheduleNew(() -> bar.bar.addPlayer(sendTo), startTime < 0 ? 0 : startTime, TimeUnit.MILLISECONDS);

        // update task
        if (bar.settings.update != -1) {
            updateTask = SchedulerUtils.scheduleNewTimer(() -> {
                boolean isLegacy = ProtocolUtils.isPlayerLegacyVer(sendTo);
                if (ProtocolUtils.isLegacyServer()) {
                    isLegacy = true;
                }
                bar.bar.setTitle(Serializer.serializeToComponent(TextHandler.constructToJsonArr(bar.title, isLegacy)));
            }, startTime < 0 ? 0 : startTime, (long) (bar.settings.update * 1000), TimeUnit.MILLISECONDS);
        } else {
            updateTask = null;
        }

        // progress animation
        if (bar.settings.progress) {
            animationTask = SchedulerUtils.scheduleNewTimer(
                    new Runnable() {
                        private double elapsedTime = 0.0;
                        // 20: frames per second
                        double progressPerTick = 1 / (bar.settings.stay * 20);
                        @Override
                        public void run() {
                            elapsedTime ++;
                            BossBar bossBar = bar.bar;
                            double progress = 1 - (progressPerTick * elapsedTime);
                            bossBar.setHealth((float) (progress));
                        }
                    },
                    startTime < 0 ? 0 : startTime,
                    48,
                    TimeUnit.MILLISECONDS
            );
        } else {
            animationTask = null;
        }
    }

    @Override
    public void run() {
        if (updateTask != null) updateTask.cancel();
        if (animationTask != null) animationTask.cancel();
        bar.bar.removePlayer(sendTo);
        bar.bar.setHealth(1);
    }
}
