package me.ed333.plugin.advancedannouncement.announcement;

import com.google.gson.JsonArray;
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
    private final int fadeout;

    private final int sub_fadeIn;
    private final int sub_stay;
    private final int sub_fadeout;

    public TitleTypeAnnouncement(
            int index, @NotNull String name, String permissionName, int delay, List<String> content,
            double fadeIn, double stay, double fadeout,
            double sub_fadeIn, double sub_stay, double sub_fadeout
    ) {
        super(index, name, AnnouncementType.TITLE, permissionName, delay, content);

        this.fadeIn = (int) Math.round(fadeIn * 20);
        this.stay = (int) Math.round(stay * 20);
        this.fadeout = (int) Math.round(fadeout * 20);
        // sub
        this.sub_fadeIn = (int) Math.round(sub_fadeIn * 20);
        this.sub_stay = (int) Math.round(sub_stay * 20);
        this.sub_fadeout = (int) Math.round(sub_fadeout * 20);
    }

    @Override
    public boolean send(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(LangUtils.getLangText_withPrefix("command.command-display-not-supported"));
            return false;
        } else if (sender instanceof Player) {
            JsonArray titleArr = TextHandler.constructToJsonArr(TextHandler.handleColor(content().get(0), sender), sender);
            JsonArray subArr = TextHandler.constructToJsonArr(TextHandler.handleColor(content().get(1).isEmpty() ? "" : content().get(1), sender), sender);
            ProtocolUtils.sendTitle((Player) sender, fadeIn, stay, fadeout, titleArr.toString());
            ProtocolUtils.sendSubtitle((Player) sender, sub_fadeIn, sub_stay, sub_fadeout, subArr.toString());
            return true;
        } else {
            sender.sendMessage(LangUtils.getLangText_withPrefix("command.command-display-sender-not-known"));
            return false;
        }
    }
}
