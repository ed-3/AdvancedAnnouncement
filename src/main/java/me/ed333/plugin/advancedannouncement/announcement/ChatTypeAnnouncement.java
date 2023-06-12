package me.ed333.plugin.advancedannouncement.announcement;

import com.google.gson.JsonArray;
import me.ed333.plugin.advancedannouncement.utils.LangUtils;
import me.ed333.plugin.advancedannouncement.utils.ProtocolUtils;
import me.ed333.plugin.advancedannouncement.utils.TextHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ChatTypeAnnouncement extends Announcement {
    public ChatTypeAnnouncement(int index, String name, String permissionName, int delay, List<String> content) {
        super(index, name, AnnouncementType.CHAT, permissionName, delay, content);
    }

    @Override
    public boolean send(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(LangUtils.getLangText_withPrefix("command.command-display-not-supported"));
            return false;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(LangUtils.getLangText_withPrefix("command.command-display-sender-not-known"));
            return false;
        }

        for (String raw : content()) {
            JsonArray array = TextHandler.constructToJsonArr(raw, sender);
            ProtocolUtils.sendJsonMsg((Player) sender, array.toString());
        }
        return true;
    }
}
