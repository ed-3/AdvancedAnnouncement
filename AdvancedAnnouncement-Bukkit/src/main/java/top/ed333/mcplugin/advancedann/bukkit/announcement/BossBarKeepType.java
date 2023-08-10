package top.ed333.mcplugin.advancedann.bukkit.announcement;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.ed333.mcplugin.advancedann.bukkit.utils.BukkitAdapter;
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
            String permissionName,
            int delay,
            List<String> content,
            List<String> worlds
    ) {
        super(index, name, AnnouncementType.BOSSBAR_KEEP, permissionName, delay, content, worlds);

        for (String rawText : content()) {
            StringBuilder barText = new StringBuilder();
            BossBarTextSettings settings = new BossBarTextSettings();

            int lastIndex = 0;
            Matcher placeHolderMatcher = PlaceholderRegex.BossBarRegex.getPLACEHOLDER_REGEX_MATCHER(rawText);
            while (placeHolderMatcher.find()) {
                int start = placeHolderMatcher.start();
                int end = placeHolderMatcher.end();

                String placeHolder = placeHolderMatcher.group();
                if (placeHolder.matches(PlaceholderRegex.BossBarRegex.UPDATE_PLACEHOLDER_REGEX_STRING)) {
                    settings.update = PlaceholderRegex.getSec(placeHolder);
                } else if (placeHolder.matches(PlaceholderRegex.BossBarRegex.COLOR_PLACEHOLDER_REGEX_STRING)) {
                    settings.barColor = BukkitAdapter.toBukkit_barColor(PlaceholderRegex.BossBarRegex.getBarColor(placeHolder));
                } else if (placeHolder.matches(PlaceholderRegex.BossBarRegex.SEGMENT_PLACEHOLDER_REGEX_STRING)) {
                    settings.style = BukkitAdapter.toBukkit_barStyle(PlaceholderRegex.BossBarRegex.getBarStyle(placeHolder));
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
        bars.forEach(bar -> {
            bar.bar.addPlayer((Player) sender);
        });
    }

    public void removeAllPlayer() {
        bars.forEach(bar-> {
            bar.bar.removeAll();
        });
    }

    public void removePlayer(Player player) {
        bars.forEach(bar -> {
            bar.bar.removePlayer(player);
        });
    }
}
