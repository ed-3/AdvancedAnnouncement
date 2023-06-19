package me.ed333.plugin.advancedannouncement.announcement;

import com.google.gson.JsonArray;
import me.ed333.plugin.advancedannouncement.AdvancedAnnouncement;
import me.ed333.plugin.advancedannouncement.utils.ProtocolUtils;
import me.ed333.plugin.advancedannouncement.utils.TextHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

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
    public void send(CommandSender sender) {

        Bukkit.getScheduler().runTaskLaterAsynchronously(AdvancedAnnouncement.INSTANCE, () -> {
            String playerWorldN = ((Player) sender).getWorld().getName();
            if (!getWorlds().isEmpty() && !getWorlds().contains(playerWorldN)) {
                return;
            }

            for (String raw : content()) {
                JsonArray array = TextHandler.constructToJsonArr(raw, sender);
                ProtocolUtils.sendChat((Player) sender, array.toString());
            }
        }, 0L);
    }
}
