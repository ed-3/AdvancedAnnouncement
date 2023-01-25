package me.ed333.plugin.advancedannouncement.instances.announcement;

import me.ed333.plugin.advancedannouncement.utils.LangUtils;
import me.ed333.plugin.advancedannouncement.utils.ProtocolUtils;
import me.ed333.plugin.advancedannouncement.utils.TextHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TitleTypeAnnouncement extends Announcement {
    private final int fadeIn;
    private final int stay;
    private final int fadeOut;

    public TitleTypeAnnouncement(int index, @NotNull String name, String permissionName, int delay, List<String> content, double fadeIn, double stay, double fadeOut) {
        super(index, name, AnnouncementType.TITLE, permissionName, delay, content);

        this.fadeIn = (int) Math.round(fadeIn * 20);
        this.stay = (int) Math.round(stay * 20);
        this.fadeOut = (int) Math.round(fadeOut * 20);
    }

    @Override
    public boolean send(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(LangUtils.getLangText_withPrefix("command.command-display-not-supported"));
            return false;
        } else if (sender instanceof Player) {
            String title = TextHandler.handleColor(content().get(0), sender);
            String subTitle = TextHandler.handleColor(content().get(1).isEmpty() ? "" : content().get(1), sender);
            ((Player) sender).sendTitle(title, subTitle, fadeIn, stay, fadeOut);
            return true;
        } else {
            sender.sendMessage(LangUtils.getLangText_withPrefix("command.command-display-sender-not-known"));
            return false;
        }
    }
}
