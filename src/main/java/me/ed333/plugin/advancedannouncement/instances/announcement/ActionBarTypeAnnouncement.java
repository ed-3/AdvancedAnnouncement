package me.ed333.plugin.advancedannouncement.instances.announcement;

import me.ed333.plugin.advancedannouncement.AdvancedAnnouncement;
import me.ed333.plugin.advancedannouncement.utils.LangUtils;
import me.ed333.plugin.advancedannouncement.utils.ProtocolUtils;
import me.ed333.plugin.advancedannouncement.utils.TextHandler;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Matcher;

public class ActionBarTypeAnnouncement extends Announcement {
    public ActionBarTypeAnnouncement(int index, @NotNull String name, String permissionName, int delay, List<String> content) {
        super(index, name, AnnouncementType.ACTION_BAR, permissionName, delay, content);
    }

    @Override
    public boolean send(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(LangUtils.getLangText_withPrefix("command.command-display-not-supported"));
            return false;
        } else if (sender instanceof Player) {
            double nextDelay = 0;
            for (String rawText : content()) {
                StringBuilder barText = new StringBuilder();
                double stay = 5;
                double delay = 0;
                int lastIndex = 0;

                Matcher matcher = ActionBarPlaceholderRegex.getACTION_BAR_PLACEHOLDER_REGEX_MATCHER(rawText);
                while (matcher.find()) {
                    int start = matcher.start();
                    int end = matcher.end();
                    String placeholderStr = matcher.group();
                    double timeSec = ActionBarPlaceholderRegex.getSec(placeholderStr);
                    if (placeholderStr.matches(ActionBarPlaceholderRegex.STAY_SEC_REGEX_STRING)) {
                        stay = timeSec;
                    } else if (placeholderStr.matches(ActionBarPlaceholderRegex.DELAY_TO_NEXT_REGEX_STRING)) {
                        delay = timeSec;
                    }
                    barText.append(rawText, lastIndex, start);
                    lastIndex = end;
                }
                if (lastIndex < rawText.length()) {
                    barText.append(rawText.substring(lastIndex));
                }

                ActionBarRunnable abr = new ActionBarRunnable(barText.toString(), (Player) sender, nextDelay);
                abr.runTaskLaterAsynchronously(AdvancedAnnouncement.INSTANCE, (long) ((stay + nextDelay) * 20L));
                nextDelay = nextDelay + stay + delay;
            }
            return true;
        } else {
            sender.sendMessage(LangUtils.getLangText_withPrefix("command.command-display-sender-not-known"));
            return false;
        }
    }



    private static class ActionBarRunnable extends BukkitRunnable {
        private final BukkitTask stayTask;
        private ActionBarRunnable(String text, Player sendTo, double delay) {
            // task for refresh action bar, due to we cannot set the bar stay time,
            // so use it to refresh actionbar like text stays.
            stayTask = new BukkitRunnable() {
                @Override
                public void run() {
                    sendTo.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextHandler.toTextComponent(text, sendTo));
                }
            }.runTaskTimerAsynchronously(AdvancedAnnouncement.INSTANCE, (long) (delay * 20L), 40);
        }

        @Override
        public void run() {
            stayTask.cancel();
        }
    }
}
