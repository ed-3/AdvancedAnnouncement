package top.ed333.mcplugin.advancedann.bungee.announcement;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import top.ed333.mcplugin.advancedann.bungee.utils.SchedulerUtils;
import top.ed333.mcplugin.advancedann.common.utils.PlaceholderRegex;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

public class LinedBossBarType extends Announcement {
    private final List<AdvancedBossBar> bars = new ArrayList<>();
    public LinedBossBarType(
            int index,
            String name,
            List<String> content,
            String permissionName,
            int delay,
            double stay
    ) {
        super(index, name, content, permissionName, delay, AnnouncementType.MULTIPLE_LINE_BOSS_BAR);

        for (String rawText : getContent()) {
            StringBuilder barText = new StringBuilder();
            AdvancedBossBar.BossBarTextSettings settings = new AdvancedBossBar.BossBarTextSettings();
            settings.stay = stay;

            int lastIndex = 0;
            Matcher placeHolderMatcher = PlaceholderRegex.BossBarRegex.getPLACEHOLDER_REGEX_MATCHER(rawText);
            while (placeHolderMatcher.find()) {
                int start = placeHolderMatcher.start();
                int end = placeHolderMatcher.end();

                String placeHolder = placeHolderMatcher.group();
                if (placeHolder.matches(PlaceholderRegex.BossBarRegex.UPDATE_PLACEHOLDER_REGEX_STRING)) {
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

            bars.add(new AdvancedBossBar(barText.toString(), settings));
        }
    }

    @Override
    public void send(ProxiedPlayer sender, boolean legacy) {
        SchedulerUtils.runAsync(
                () -> bars.forEach(
                        bar -> SchedulerUtils.scheduleNew(
                                new BossBarScheduler(bar, sender),
                                (long) (bar.settings.stay) * 1000,
                                TimeUnit.MILLISECONDS
                        )
                )
        );
    }
}
