package me.ed333.plugin.advancedannouncement.announcement;

import com.google.gson.JsonArray;
import me.ed333.plugin.advancedannouncement.AdvancedAnnouncement;
import me.ed333.plugin.advancedannouncement.utils.LangUtils;
import me.ed333.plugin.advancedannouncement.utils.ProtocolUtils;
import me.ed333.plugin.advancedannouncement.utils.TextHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatType extends Announcement {
    public ChatType(
            int index,
            String name,
            String permissionName,
            int delay,
            List<String> content,
            List<String> worlds
    ) {
        super(index, name, AnnouncementType.CHAT, permissionName, delay, content, worlds);
    }

    @Override
    public boolean send(CommandSender sender) {
        AtomicBoolean result = new AtomicBoolean(false);

        Bukkit.getScheduler().runTaskLaterAsynchronously(AdvancedAnnouncement.INSTANCE, () -> {
            if (sender instanceof ConsoleCommandSender) {
                sender.sendMessage(LangUtils.getLangText_withPrefix("command.command-display-not-supported"));
                result.set(false);
                return;
            }

            if (!(sender instanceof Player)) {
                sender.sendMessage(LangUtils.getLangText_withPrefix("command.command-display-sender-not-known"));
                result.set(false);
                return;
            }

            String playerWorldN = ((Player) sender).getWorld().getName();
            if (!getWorlds().isEmpty() && !getWorlds().contains(playerWorldN)) {
                result.set(false);
                return;
            }

            for (String raw : content()) {
                JsonArray array = TextHandler.constructToJsonArr(raw, sender);
                ProtocolUtils.sendChat((Player) sender, array.toString());
            }
            result.set(true);
        }, 0L);

        return result.get();
    }
}
