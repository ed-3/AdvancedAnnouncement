package top.ed333.mcplugin.advancedann.bungee.announcement;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import top.ed333.mcplugin.advancedann.bungee.utils.ProtocolUtils;
import top.ed333.mcplugin.advancedann.bungee.utils.SchedulerUtils;
import top.ed333.mcplugin.advancedann.common.utils.PlaceholderRegex;
import top.ed333.mcplugin.advancedann.common.utils.TextHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

public class ActionBarType extends Announcement {
    private final List<ActionBarText> barTexts = new ArrayList<>();

    public ActionBarType(
            int index,
            String name,
            List<String> content,
            String permissionName,
            int delay
    ) {
        super(index, name, content, permissionName, delay, AnnouncementType.ACTION_BAR);

        double nextDelay = 0;
        for (String rawText : getContent()) {
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
    public void send(CommandSender sender, boolean legacy) {
        SchedulerUtils.runAsync(() -> {
            for (ActionBarText barText : barTexts) {
                SchedulerUtils.scheduleNew(() -> {
                    String text = TextHandler.handleColor(barText.text, legacy);
                    SchedulerUtils.scheduleNew(() -> ProtocolUtils.sendActionBar((ProxiedPlayer) sender, text), (long) (barText.settings.next_delay * 1000), TimeUnit.MILLISECONDS);
                }, (long) (barText.settings.stay + barText.settings.next_delay - barText.settings.delay * 1000), TimeUnit.MILLISECONDS);
            }
        });
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
