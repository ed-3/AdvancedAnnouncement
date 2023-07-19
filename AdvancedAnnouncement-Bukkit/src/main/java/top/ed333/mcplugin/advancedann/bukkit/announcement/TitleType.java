package top.ed333.mcplugin.advancedann.bukkit.announcement;

import com.google.gson.JsonArray;
import me.clip.placeholderapi.PlaceholderAPI;
import top.ed333.mcplugin.advancedann.bukkit.AdvancedAnnouncement;
import top.ed333.mcplugin.advancedann.bukkit.utils.ProtocolUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import top.ed333.mcplugin.advancedann.common.utils.TextHandler;

import java.util.List;

public class TitleType extends Announcement {
    private final int fadeIn;
    private final int stay;
    private final int fadeout;

    private final int sub_fadeIn;
    private final int sub_stay;
    private final int sub_fadeout;

    public TitleType(
            int index,
            @NotNull String name,
            String permissionName,
            int delay,
            List<String> content,
            List<String> worlds,
            double fadeIn, double stay, double fadeout,
            double sub_fadeIn, double sub_stay, double sub_fadeout
    ) {
        super(index, name, AnnouncementType.TITLE, permissionName, delay, content, worlds);

        this.fadeIn = (int) Math.round(fadeIn * 20);
        this.stay = (int) Math.round(stay * 20);
        this.fadeout = (int) Math.round(fadeout * 20);
        // sub
        this.sub_fadeIn = (int) Math.round(sub_fadeIn * 20);
        this.sub_stay = (int) Math.round(sub_stay * 20);
        this.sub_fadeout = (int) Math.round(sub_fadeout * 20);
    }

    @Override
    public void send(CommandSender sender, boolean legacy) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(AdvancedAnnouncement.INSTANCE, () -> {
            // #I7MEPP
            String title = PlaceholderAPI.setPlaceholders((Player) sender, content().get(0));
            String sub = PlaceholderAPI.setPlaceholders((Player) sender, content().get(1));

            JsonArray titleArr = TextHandler.constructToJsonArr(TextHandler.handleColor(title, legacy), legacy);
            JsonArray subArr = TextHandler.constructToJsonArr(TextHandler.handleColor(content().get(1).isEmpty() ? "" : sub, legacy), legacy);
            ProtocolUtils.sendTitle((Player) sender, fadeIn, stay, fadeout, titleArr.toString());
            ProtocolUtils.sendSubtitle((Player) sender, sub_fadeIn, sub_stay, sub_fadeout, subArr.toString());
        }, 0L);
    }
}
