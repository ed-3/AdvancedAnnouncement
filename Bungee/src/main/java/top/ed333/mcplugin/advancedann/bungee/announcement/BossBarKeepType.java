package top.ed333.mcplugin.advancedann.bungee.announcement;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import top.ed333.mcplugin.advancedann.common.announcement.AnnouncementType;
import top.ed333.mcplugin.advancedann.common.utils.PlaceholderRegex;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

public class BossBarKeepType extends Announcement {
    Set<AdvancedBossBar> bars = new HashSet<>();
    public BossBarKeepType(
            int index,
            String name,
            List<String> content,
            String permissionName,
            int delay
    ) {
        super(index, name, content, permissionName, delay, AnnouncementType.BOSSBAR_KEEP);

        for (String rawText : getContent()) {
            StringBuilder barText = new StringBuilder();
            AdvancedBossBar.BossBarTextSettings settings = new AdvancedBossBar.BossBarTextSettings();

            int lastIndex = 0;
            Matcher placeHolderMatcher = PlaceholderRegex.BossBarRegex.getPLACEHOLDER_REGEX_MATCHER(rawText);
            while (placeHolderMatcher.find()) {
                int start = placeHolderMatcher.start();
                int end = placeHolderMatcher.end();

                String placeHolder = placeHolderMatcher.group();
                if (placeHolder.matches(PlaceholderRegex.BossBarRegex.UPDATE_PLACEHOLDER_REGEX_STRING)) {
                    settings.update = PlaceholderRegex.getSec(placeHolder);
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
        bars.forEach(bar -> {
            bar.bar.addPlayer(sender);
        });
    }

    public void removeAllPlayer() {
        bars.forEach(bar-> {
            bar.bar.removeAllPlayer();
        });
    }

    public void removePlayer(ProxiedPlayer player) {
        bars.forEach(bar -> {
            bar.bar.removePlayer(player);
        });
    }
}
