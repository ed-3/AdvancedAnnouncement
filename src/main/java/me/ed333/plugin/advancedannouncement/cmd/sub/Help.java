package me.ed333.plugin.advancedannouncement.cmd.sub;

import me.ed333.plugin.advancedannouncement.utils.LangUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class Help {
    public static void callCmd(@NotNull CommandSender sender, String[] args) {
        LangUtils.getLangList("command.help").forEach(sender::sendMessage);
    }
}
