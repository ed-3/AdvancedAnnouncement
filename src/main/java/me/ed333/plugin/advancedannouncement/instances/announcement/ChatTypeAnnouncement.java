package me.ed333.plugin.advancedannouncement.instances.announcement;

import com.comphenix.protocol.ProtocolLibrary;
import me.ed333.plugin.advancedannouncement.ConfigKeys;
import me.ed333.plugin.advancedannouncement.utils.LangUtils;
import me.ed333.plugin.advancedannouncement.utils.ProtocolUtils;
import me.ed333.plugin.advancedannouncement.utils.TextHandler;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ChatTypeAnnouncement extends Announcement {
    public ChatTypeAnnouncement(int index, String name, String permissionName, int delay, List<String> content) {
        super(index, name, AnnouncementType.CHAT, permissionName, delay, content);
    }

    @Override
    public void broadcast() {
        if (ConfigKeys.CONSOLE_BROAD_CAST) {
            send(Bukkit.getConsoleSender());
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            String permissionName = permissionName();
            if (permissionName != null && !player.hasPermission(permissionName)) continue;
            send(player);
        }
    }

    @Override
    public boolean send(CommandSender sender) {
        final List<TextComponent> components = new ArrayList<>();
        boolean legacy;
        if (sender instanceof Player) {
            Player player = ((Player) sender);
            int clientVer = ProtocolLibrary.getProtocolManager().getProtocolVersion(player);
            legacy = clientVer < 735;
        } else if (sender instanceof ConsoleCommandSender) {
            legacy = ProtocolUtils.isLegacyServer();
        } else {
            sender.sendMessage(LangUtils.getLangText_withPrefix("command.command-display-sender-not-known"));
            return false;
        }

        for (Object raw : content()) {
            components.add(TextHandler.toTextComponent(raw.toString(), legacy));
        }
        components.forEach(sender.spigot()::sendMessage);
        return true;
    }
}
