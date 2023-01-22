package me.ed333.plugin.advancedannouncement.cmd.sub;

import me.ed333.plugin.advancedannouncement.cmd.PermissionRequirement;
import me.ed333.plugin.advancedannouncement.instances.announcement.Announcement;
import me.ed333.plugin.advancedannouncement.instances.announcement.AnnouncementManager;
import me.ed333.plugin.advancedannouncement.utils.LangUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class Display {
    @PermissionRequirement("aa.command.display")
    public static void callCmd(@NotNull CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(LangUtils.getLangText_withPrefix("command.invalidArgs"));
            return;
        }

        String annName = args[1];
        Announcement ann = AnnouncementManager.forName(annName);
        if (ann == null) {
            sender.sendMessage(LangUtils.getLangText_withPrefix("command.command-display-not-found"));
            return;
        }
        boolean result = ann.send(sender);
        if (result) {
            sender.sendMessage(LangUtils.parseLang_withPrefix("command.command-display-message", ann.getName()));
        }
    }
}
