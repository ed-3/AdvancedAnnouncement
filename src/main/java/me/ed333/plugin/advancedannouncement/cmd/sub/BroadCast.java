package me.ed333.plugin.advancedannouncement.cmd.sub;

import me.ed333.plugin.advancedannouncement.ConfigKeys;
import me.ed333.plugin.advancedannouncement.cmd.PermissionRequirement;
import me.ed333.plugin.advancedannouncement.instances.announcement.Announcement;
import me.ed333.plugin.advancedannouncement.instances.announcement.AnnouncementManager;
import me.ed333.plugin.advancedannouncement.instances.announcement.AnnouncementType;
import me.ed333.plugin.advancedannouncement.utils.LangUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

public class BroadCast {

    @PermissionRequirement("aa.command.broadcast")
    public static void callCmd(@NotNull CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(LangUtils.getLangText_withPrefix("command.invalidArgs"));
            return;
        }

        String annName = args[1];
        Announcement ann = AnnouncementManager.forName(annName);
        if (ann == null) {
            sender.sendMessage(LangUtils.getLangText_withPrefix("command.command-broadcast-not-found"));
            return;
        }

        ann.broadcast();

        if (sender instanceof ConsoleCommandSender
                && ann.type().equals(AnnouncementType.CHAT)
                && ConfigKeys.CONSOLE_BROAD_CAST
        ) {
            ann.send(sender);
        }

        sender.sendMessage(LangUtils.parseLang_withPrefix("command.command-broadcast-sent", ann.getName()));
    }
}
