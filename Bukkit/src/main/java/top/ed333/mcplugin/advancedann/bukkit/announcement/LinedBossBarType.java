package top.ed333.mcplugin.advancedann.bukkit.announcement;

import top.ed333.mcplugin.advancedann.bukkit.AdvancedAnnouncement;
import top.ed333.mcplugin.advancedann.bukkit.utils.PlaceholderRegex;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class LinedBossBarType extends Announcement {
    private final List<AdvancedBossBar> bars = new ArrayList<>();

    public LinedBossBarType(
            int index,
            @NotNull String name,
            @Nullable String permissionName,
            int delay,
            double stay,
            List<String> content,
            List<String> worlds
    ) {
        super(index, name, AnnouncementType.MULTIPLE_LINE_BOSS_BAR, permissionName, delay, content, worlds);

        for (String rawText : content()) {
            StringBuilder barText = new StringBuilder();
            BossBarTextSettings settings = new BossBarTextSettings();
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
    public void send(CommandSender sender, boolean legacy) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(AdvancedAnnouncement.INSTANCE, () -> {
            bars.forEach(bar -> new BossBarRunnable(bar, (Player) sender)
                    .runTaskLaterAsynchronously(AdvancedAnnouncement.INSTANCE, (long) (bar.settings.stay * 20L)));
        }, 0L);
    }
}
