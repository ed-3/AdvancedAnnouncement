package top.ed333.mcplugin.advancedann.bungee.announcement;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import top.ed333.mcplugin.advancedann.bungee.utils.ProtocolUtils;
import top.ed333.mcplugin.advancedann.bungee.utils.SchedulerUtils;
import top.ed333.mcplugin.advancedann.bungee.utils.Serializer;
import top.ed333.mcplugin.advancedann.common.announcement.AnnouncementType;
import top.ed333.mcplugin.advancedann.common.utils.TextHandler;

import java.util.List;

public class TitleType extends Announcement {
    /* seconds */
    private final int fadeIn;
    private final int stay;
    private final int fadeout;

    private final int sub_fadeIn;
    private final int sub_stay;
    private final int sub_fadeout;
    public TitleType(
            int index, 
            String name,
            List<String> content, 
            String permissionName, 
            int delay,
            double fadeIn, double stay, double fadeout,
            double sub_fadeIn, double sub_stay, double sub_fadeout
    ) {
        super(index, name, content, permissionName, delay, AnnouncementType.TITLE);

        this.fadeIn = (int) Math.round(fadeIn * 20);
        this.stay = (int) Math.round(stay * 20);
        this.fadeout = (int) Math.round(fadeout * 20);
        // sub
        this.sub_fadeIn = (int) Math.round(sub_fadeIn * 20);
        this.sub_stay = (int) Math.round(sub_stay * 20);
        this.sub_fadeout = (int) Math.round(sub_fadeout * 20);
    }

    @Override
    public void send(ProxiedPlayer sender, boolean legacy) {
        SchedulerUtils.runAsync(() -> {
            BaseComponent[] titleContent = Serializer.serializeToComponent(TextHandler.constructToJsonArr(getContent().get(0), legacy));
            BaseComponent[] subContent = Serializer.serializeToComponent(TextHandler.constructToJsonArr(getContent().get(1).isEmpty() ? "" : getContent().get(1), legacy));
            ProtocolUtils.sendTitle(sender, fadeIn, stay, fadeout, titleContent);
            ProtocolUtils.sendSub(sender, sub_fadeIn, sub_stay, sub_fadeout, subContent);
        });
    }
}
