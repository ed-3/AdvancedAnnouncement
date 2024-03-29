package top.ed333.mcplugin.advancedann.bukkit.announcement;

import com.google.gson.JsonArray;
import me.clip.placeholderapi.PlaceholderAPI;
import top.ed333.mcplugin.advancedann.bukkit.AdvancedAnnouncement;
import top.ed333.mcplugin.advancedann.bukkit.utils.AnnouncementUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.ed333.mcplugin.advancedann.common.announcement.AnnouncementType;
import top.ed333.mcplugin.advancedann.common.utils.TextHandler;

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
                /* gitee issue: #I7MEPP */ String rawStr = PlaceholderAPI.setPlaceholders((Player) sender, raw);
                JsonArray array = TextHandler.constructToJsonArr(rawStr, isUseLegacyColorDefault());
                AnnouncementUtils.sendChat((Player) sender, array);
            }
        }, 0L);
    }
}
