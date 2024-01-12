package top.ed333.mcplugin.advancedann.bukkit.announcement;

import com.google.gson.JsonArray;
import me.clip.placeholderapi.PlaceholderAPI;
import top.ed333.mcplugin.advancedann.bukkit.AdvancedAnnouncement;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import top.ed333.mcplugin.advancedann.bukkit.utils.AnnouncementUtils;
import top.ed333.mcplugin.advancedann.common.announcement.AnnouncementType;
import top.ed333.mcplugin.advancedann.common.utils.PlaceholderRegex;
import top.ed333.mcplugin.advancedann.common.utils.TextHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class ActionBarType extends Announcement {
    private final List<ActionBarText> barTexts = new ArrayList<>();

    public ActionBarType(
            int index,
            @NotNull String name,
            String permissionName,
            int delay,
            List<String> content,
            List<String> worlds
    ) {
        super(index, name, AnnouncementType.ACTION_BAR, permissionName, delay, content, worlds);

        double nextDelay = 0;
        for (String rawText : content()) {
            StringBuilder barTextString = new StringBuilder();
            double stay = 5;
            double bar_delay = 0;
            int lastIndex = 0;

            Matcher matcher = PlaceholderRegex.ActionBarRegex.getACTION_BAR_PLACEHOLDER_REGEX_MATCHER(rawText);
            while (matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();
                String placeholderStr = matcher.group();
                double timeSec = PlaceholderRegex.getSec(placeholderStr);
                if (placeholderStr.matches(PlaceholderRegex.ActionBarRegex.STAY_SEC_REGEX_STRING)) {
                    stay = timeSec;
                } else if (placeholderStr.matches(PlaceholderRegex.ActionBarRegex.DELAY_TO_NEXT_REGEX_STRING)) {
                    bar_delay = timeSec;
                }
                barTextString.append(rawText, lastIndex, start);
                lastIndex = end;
            }
            if (lastIndex < rawText.length()) {
                barTextString.append(rawText.substring(lastIndex));
            }

            ActionBarSettings settings = new ActionBarSettings();
            settings.stay = stay;
            settings.delay = bar_delay;
            settings.next_delay = nextDelay;

            ActionBarText barText = new ActionBarText(barTextString.toString(), settings);
            barTexts.add(barText);
            nextDelay = nextDelay + stay + bar_delay;
        }
    }

    @Override
    public void send(CommandSender sender) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(AdvancedAnnouncement.INSTANCE, () -> {

            String playerWorldN = ((Player) sender).getWorld().getName();
            if (!getWorlds().isEmpty() && !getWorlds().contains(playerWorldN)) {
                return;
            }

            for (ActionBarText barText : barTexts) {
                /* gitee issue: #I7MEPP */ String barTextStr = PlaceholderAPI.setPlaceholders((Player) sender, barText.text);
                ActionBarRunnable abr = new ActionBarRunnable(
                        TextHandler.constructToJsonArr(barTextStr, isUseLegacyColorDefault()),
                        (Player) sender,
                        barText.settings.next_delay
                );
                abr.runTaskLaterAsynchronously(AdvancedAnnouncement.INSTANCE, (long) ((barText.settings.stay + barText.settings.next_delay - barText.settings.delay)* 20L));
            }

        },0L);
    }

    private static class ActionBarRunnable extends BukkitRunnable {
        private final BukkitTask stayTask;
        private ActionBarRunnable(JsonArray jsonComponent, Player sendTo, double delay) {
            // task for refresh action bar, due to we cannot set the bar stay time,
            // so use it to refresh actionbar like text stays.
            stayTask = new BukkitRunnable() {
                @Override
                public void run() {
                    AnnouncementUtils.sendActionBar(sendTo, jsonComponent);
                }
            }.runTaskTimerAsynchronously(AdvancedAnnouncement.INSTANCE, (long) (delay * 20L), 40);
        }

        @Override
        public void run() {
            stayTask.cancel();
        }
    }

    private static class ActionBarText {
        private final String text;
        private final ActionBarSettings settings;

        private ActionBarText(String text, ActionBarSettings settings) {
            this.settings = settings;
            this.text = text;
        }

        @Override
        public String toString() {
            return "ActionBarText{" + "text='" + text + '\'' +
                    ", settings=" + settings +
                    '}';
        }
    }

    private static class ActionBarSettings {
        private double stay = 2.0D;
        private double delay = 2.0D;
        private double next_delay = 0.0D;

        ActionBarSettings() {}

        @Override
        public String toString() {
            return "ActionBarSettings{" + "stay=" + stay +
                    ", delay=" + delay +
                    ", next_delay=" + next_delay +
                    '}';
        }
    }
}
