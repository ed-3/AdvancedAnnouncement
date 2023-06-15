package me.ed333.plugin.advancedannouncement.announcement;

import me.ed333.plugin.advancedannouncement.AdvancedAnnouncement;
import me.ed333.plugin.advancedannouncement.utils.LangUtils;
import me.ed333.plugin.advancedannouncement.utils.PlaceholderRegex;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;

public class BossBarType extends Announcement {
    private final List<AdvancedBossBar> bars = new ArrayList<>();

    public BossBarType(
            int index,
            @NotNull String name,
            String permissionName,
            int delay,
            List<String> content,
            List<String> worlds
    ) {
        super(index, name, AnnouncementType.BOSS_BAR, permissionName, delay, content, worlds);
        double nextDelay = 0;

        for (String rawText : content()) {
            StringBuilder barText = new StringBuilder();
            BossBarTextSettings settings = new BossBarTextSettings();

            int lastIndex = 0;
            Matcher placeHolderMatcher = PlaceholderRegex.BossBarRegex.getPLACEHOLDER_REGEX_MATCHER(rawText);
            while (placeHolderMatcher.find()) {
                int start = placeHolderMatcher.start();
                int end = placeHolderMatcher.end();

                String placeHolder = placeHolderMatcher.group();
                if (placeHolder.matches(PlaceholderRegex.BossBarRegex.STAY_PLACEHOLDER_REGEX_STRING)) {
                    settings.stay = PlaceholderRegex.getSec(placeHolder);
                } else if (placeHolder.matches(PlaceholderRegex.BossBarRegex.DELAY_PLACEHOLDER_REGEX_STRING)) {
                    settings.delaySec = PlaceholderRegex.getSec(placeHolder);
                } else if (placeHolder.matches(PlaceholderRegex.BossBarRegex.UPDATE_PLACEHOLDER_REGEX_STRING)) {
                    settings.update = PlaceholderRegex.getSec(placeHolder);
                } else if (placeHolder.matches(PlaceholderRegex.BossBarRegex.PROGRESS_PLACEHOLDER_REGEX_STRING)) {
                    settings.progress = PlaceholderRegex.getBool(placeHolder);
                } else if (placeHolder.matches(PlaceholderRegex.BossBarRegex.COLOR_PLACEHOLDER_REGEX_STRING)) {
                    settings.barColor = PlaceholderRegex.BossBarRegex.getBarColor(placeHolder);
                } else if (placeHolder.matches(PlaceholderRegex.BossBarRegex.SEGMENT_PLACEHOLDER_REGEX_STRING)) {
                    settings.style = PlaceholderRegex.BossBarRegex.getBarStyle(placeHolder);
                }
                barText.append(rawText, lastIndex, start);
                lastIndex = end;
            }
            if (lastIndex < rawText.length()) {
                barText.append(rawText.substring(lastIndex));
            }

            settings.nextDelay = nextDelay + settings.stay + settings.delaySec;
            bars.add(new AdvancedBossBar(barText.toString(), settings));

            nextDelay = nextDelay + settings.stay + settings.delaySec;
        }
    }

    @Override
    public boolean send(CommandSender sender) {
        AtomicBoolean result = new AtomicBoolean(false);

        Bukkit.getScheduler().runTaskLaterAsynchronously(AdvancedAnnouncement.INSTANCE, ()-> {
            if (sender instanceof ConsoleCommandSender) {
                sender.sendMessage(LangUtils.getLangText_withPrefix("command.command-display-not-supported"));
                result.set(false);
            } else if (sender instanceof Player) {
                String playerWorldN = ((Player) sender).getWorld().getName();
                if (!getWorlds().isEmpty() && !getWorlds().contains(playerWorldN)) {
                    result.set(false);
                    return;
                }

                for (AdvancedBossBar bar : bars) {
                    new BossBarRunnable(bar, (Player) sender)
                            .runTaskLaterAsynchronously(
                                    AdvancedAnnouncement.INSTANCE,
                                    (long) (bar.settings.nextDelay - bar.settings.delaySec) * 20L
                            );
                }
                result.set(true);
            } else {
                sender.sendMessage(LangUtils.getLangText_withPrefix("command.command-display-sender-not-known"));
                result.set(false);
            }
        }, 0L);

        return result.get();
    }
}