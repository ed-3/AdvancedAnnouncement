package me.ed333.plugin.advancedannouncement.instances.announcement;

import me.ed333.plugin.advancedannouncement.AdvancedAnnouncement;
import me.ed333.plugin.advancedannouncement.utils.LangUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
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
            BossBarTextSettings settings = new BossBarTextSettings();

            int lastIndex = 0;
            Matcher placeHolderMatcher = BossBarPlaceholderRegex.getPLACEHOLDER_REGEX_MATCHER(rawText);
            while (placeHolderMatcher.find()) {
                int start = placeHolderMatcher.start();
                int end = placeHolderMatcher.end();

                String placeHolder = placeHolderMatcher.group();
                if (placeHolder.matches(BossBarPlaceholderRegex.STAY_PLACEHOLDER_REGEX_STRING)) {
                    settings.stay = BossBarPlaceholderRegex.getSec(placeHolder);
                } else if (placeHolder.matches(BossBarPlaceholderRegex.DELAY_PLACEHOLDER_REGEX_STRING)) {
                    settings.delaySec = BossBarPlaceholderRegex.getSec(placeHolder);
                } else if (placeHolder.matches(BossBarPlaceholderRegex.UPDATE_PLACEHOLDER_REGEX_STRING)) {
                    settings.update = BossBarPlaceholderRegex.getSec(placeHolder);
                } else if (placeHolder.matches(BossBarPlaceholderRegex.PROGRESS_PLACEHOLDER_REGEX_STRING)) {
                    settings.progress = BossBarPlaceholderRegex.getBool(placeHolder);
                } else if (placeHolder.matches(BossBarPlaceholderRegex.COLOR_PLACEHOLDER_REGEX_STRING)) {
                    settings.barColor = BossBarPlaceholderRegex.getBarColor(placeHolder);
                } else if (placeHolder.matches(BossBarPlaceholderRegex.SEGMENT_PLACEHOLDER_REGEX_STRING)) {
                    settings.style = BossBarPlaceholderRegex.getBarStyle(placeHolder);
                }
                barText.append(rawText, lastIndex, start);
                lastIndex = end;
            }
            if (lastIndex < rawText.length()) {
                barText.append(rawText.substring(lastIndex));
            }

            settings.nextDelay = nextDelay;
            bars.add(new AdvancedBossBar(barText.toString(), settings));

            nextDelay = nextDelay + settings.stay + settings.delaySec;
        }
    }

    @Override
    public boolean send(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(LangUtils.getLangText_withPrefix("command.command-display-not-supported"));
            return false;
        } else if (sender instanceof Player) {
            for (AdvancedBossBar bar : bars) {
                new BossBarRunnable(bar, (Player) sender)
                        .runTaskLaterAsynchronously(
                                AdvancedAnnouncement.INSTANCE,
                                (long) (bar.settings.nextDelay - bar.settings.delaySec) * 20L
                        );
            }
            return true;
        } else {
            sender.sendMessage(LangUtils.getLangText_withPrefix("command.command-display-sender-not-known"));
            return false;
        }
    }
}