package me.ed333.plugin.advancedannouncement.instances.announcement;

import me.ed333.plugin.advancedannouncement.AdvancedAnnouncement;
import me.ed333.plugin.advancedannouncement.ConfigKeys;
import me.ed333.plugin.advancedannouncement.utils.LangUtils;
import me.ed333.plugin.advancedannouncement.utils.TextHandler;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class ChatTypeAnnouncement extends Announcement {
    public ChatTypeAnnouncement(int index, String name, String permissionName, int delay, List<String> content) {
        super(index, name, AnnouncementType.CHAT, permissionName, delay, content);
    }

    @Override
    public void broadcast() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (ConfigKeys.CONSOLE_BROAD_CAST) {
                    send(Bukkit.getConsoleSender());
                }

                ChatTypeAnnouncement.super.broadcast();
            }
        }.runTaskLaterAsynchronously(AdvancedAnnouncement.INSTANCE, 1L);
    }

    @Override
    public boolean send(CommandSender sender) {
        final List<TextComponent> components = new ArrayList<>();
        if (!(sender instanceof Player || sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(LangUtils.getLangText_withPrefix("command.command-display-sender-not-known"));
            return false;
        }

        for (Object raw : content()) {
            components.add(TextHandler.toTextComponent(raw.toString(), sender));
        }
        components.forEach(sender.spigot()::sendMessage);
        return true;
    }
}
