package me.ed333.plugin.advancedannouncement.instances.announcement;

import me.clip.placeholderapi.PlaceholderAPI;
import me.ed333.plugin.advancedannouncement.AdvancedAnnouncement;
import me.ed333.plugin.advancedannouncement.utils.LangUtils;
import me.ed333.plugin.advancedannouncement.utils.TextHandler;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class BossBarTypeAnnouncement extends Announcement {
    private final List<AdvancedBossBar> bars = new ArrayList<>();

    public BossBarTypeAnnouncement(int index, @NotNull String name, String permissionName, int delay, List<String> content) {
        super(index, name, AnnouncementType.BOSS_BAR, permissionName, delay, content);
        double nextDelay = 0;

        for (String rawText : content()) {
            StringBuilder barText = new StringBuilder();
            double stay = 5;
            double delaySec = 0;
            double update = -1;
            boolean progress = false;
            BarColor barColor = BarColor.PURPLE;
            BarStyle style = BarStyle.SOLID;

            int lastIndex = 0;
            Matcher placeHolderMatcher = BossBarPlaceholderRegex.getPLACEHOLDER_REGEX_MATCHER(rawText);
            while (placeHolderMatcher.find()) {
                int start = placeHolderMatcher.start();
                int end = placeHolderMatcher.end();

                String placeHolder = placeHolderMatcher.group();
                if (placeHolder.matches(BossBarPlaceholderRegex.STAY_PLACEHOLDER_REGEX_STRING)) {
                    stay = BossBarPlaceholderRegex.getSec(placeHolder);
                } else if (placeHolder.matches(BossBarPlaceholderRegex.DELAY_PLACEHOLDER_REGEX_STRING)) {
                    delaySec = BossBarPlaceholderRegex.getSec(placeHolder);
                }else if (placeHolder.matches(BossBarPlaceholderRegex.UPDATE_PLACEHOLDER_REGEX_STRING)) {
                    update = BossBarPlaceholderRegex.getSec(placeHolder);
                } else if (placeHolder.matches(BossBarPlaceholderRegex.PROGRESS_PLACEHOLDER_REGEX_STRING)) {
                    progress = BossBarPlaceholderRegex.getBool(placeHolder);
                } else if (placeHolder.matches(BossBarPlaceholderRegex.COLOR_PLACEHOLDER_REGEX_STRING)) {
                    barColor = BossBarPlaceholderRegex.getBarColor(placeHolder);
                } else if (placeHolder.matches(BossBarPlaceholderRegex.SEGMENT_PLACEHOLDER_REGEX_STRING)) {
                    style = BossBarPlaceholderRegex.getBarStyle(placeHolder);
                }
                barText.append(rawText, lastIndex, start);
                lastIndex = end;
            }
            if (lastIndex < rawText.length()) {
                barText.append(rawText.substring(lastIndex));
            }

            bars.add(new AdvancedBossBar(barText.toString(), barColor, style, nextDelay + stay + delaySec, update, delaySec, stay, progress));
            nextDelay = nextDelay + stay + delaySec;
        }
    }

    @Override
    public boolean send(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(LangUtils.getLangText_withPrefix("command.command-display-not-supported"));
            return false;
        } else if (sender instanceof Player) {
            bars.forEach(bar -> new BossBarRunnable(bar.clone(), (Player) sender).runTaskLaterAsynchronously(AdvancedAnnouncement.INSTANCE, (long) ((bar.delay - bar.nextDelay) * 20L)));
            return true;
        } else {
            sender.sendMessage(LangUtils.getLangText_withPrefix("command.command-display-sender-not-known"));
            return false;
        }
    }

    private static class BossBarRunnable extends BukkitRunnable {
        private final AdvancedBossBar bar;
        private final Player sendTo;
        private final BukkitTask updateTask;
        private final BukkitTask progressTask;
        private BossBarRunnable(AdvancedBossBar bar, Player sendTo) {
            this.bar = bar;
            this.sendTo = sendTo;

            long startTime = (long) ((bar.delay - (bar.stay + bar.nextDelay)) * 20L);
            new BukkitRunnable() {
                @Override
                public void run() {
                    bar.bar.addPlayer(sendTo);
                }
            }.runTaskLaterAsynchronously(AdvancedAnnouncement.INSTANCE, startTime < 0 ? 0 : startTime);

            if (bar.update != -1) {
                updateTask = new BukkitRunnable() {
                    @Override
                    public void run() {
                        bar.bar.setTitle(TextHandler.handleColor(bar.title, sendTo));
                    }
                }.runTaskTimerAsynchronously(AdvancedAnnouncement.INSTANCE, startTime < 0 ? 0 : startTime, (long) (bar.update * 20L));
            } else {
                updateTask = null;
            }

            if (bar.progress) {
                progressTask = new BukkitRunnable() {
                    double remain = bar.stay;
                    double progress = 1.0;

                    @Override
                    public void run() {
                        progress = remain / bar.stay;
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
        }
    }
}
