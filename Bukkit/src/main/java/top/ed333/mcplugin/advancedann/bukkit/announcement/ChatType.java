package top.ed333.mcplugin.advancedann.bukkit.announcement;

import com.google.gson.JsonArray;
import top.ed333.mcplugin.advancedann.bukkit.AdvancedAnnouncement;
import top.ed333.mcplugin.advancedann.bukkit.utils.ProtocolUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
    public void send(CommandSender sender, boolean legacy) {

        Bukkit.getScheduler().runTaskLaterAsynchronously(AdvancedAnnouncement.INSTANCE, () -> {
            String playerWorldN = ((Player) sender).getWorld().getName();
            if (!getWorlds().isEmpty() && !getWorlds().contains(playerWorldN)) {
                return;
            }

            for (String raw : content()) {
                JsonArray array = TextHandler.constructToJsonArr(raw, legacy);
                ProtocolUtils.sendChat((Player) sender, array.toString());
            }
        }, 0L);
    }
}
