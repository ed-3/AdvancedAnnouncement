package me.ed333.plugin.advancedannouncement.cmd.sub;

import me.ed333.plugin.advancedannouncement.cmd.PermissionRequirement;
import me.ed333.plugin.advancedannouncement.instances.announcement.Announcement;
import me.ed333.plugin.advancedannouncement.instances.announcement.AnnouncementManager;
import me.ed333.plugin.advancedannouncement.utils.LangUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class List {
    @PermissionRequirement("aa.command.list")
    public static void callCmd(@NotNull CommandSender sender, String[] args) {
        sender.sendMessage(LangUtils.prefix + ChatColor.translateAlternateColorCodes('&', "&3所有已加载的公告: "));
        for (Announcement announcement : AnnouncementManager.loadedAnnouncements) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &7&l- &2Name: &a" + announcement.getName() + "&2, Delay: &a" + announcement.delay() + "s&2, Type: &a" + announcement.type().name()));
        }
    }
}
