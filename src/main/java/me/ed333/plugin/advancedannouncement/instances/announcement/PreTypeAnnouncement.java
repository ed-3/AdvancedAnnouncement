package me.ed333.plugin.advancedannouncement.instances.announcement;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PreTypeAnnouncement extends Announcement {
    private static final List<Announcement> preAnn_shown = new ArrayList<>(); // stores the pre-announcement's display announcements
    private final Date date;
    private final AnnouncementType shownType;
    private final Announcement shownAnn;

    public PreTypeAnnouncement(
            int index,
            @NotNull String name,
            String permissionName,
            int delay,
            List<String> content,
            Date date,
            AnnouncementType shownType
    ) {
        this(index, name, permissionName, delay, content, date, shownType, -1, -1, -1);
    }

    // if display type is TITLE
    public PreTypeAnnouncement(
            int index,
            @NotNull String name,
            String permissionName,
            int delay,
            List<String> content,
            Date date,
            AnnouncementType shownType,
            double fadeIn,
            double stay,
            double fadeout
    ) {
        super(index, name, AnnouncementType.PRE_ANNOUNCE, permissionName, delay, content);

        this.shownType = shownType;
        this.date = date;

        int subIndex = preAnn_shown.size();
        name = name + ".shown";
        switch (shownType) {
            case CHAT:
                shownAnn = new ChatTypeAnnouncement(subIndex, name, permissionName, delay, content);
                break;
            case TITLE:
                shownAnn = new TitleTypeAnnouncement(subIndex, name, permissionName, delay, content, fadeIn, stay, fadeout);
                break;
            case BOSS_BAR:
                shownAnn = new BossBarTypeAnnouncement(subIndex, name, permissionName, delay, content);
                break;
            case ACTION_BAR:
                shownAnn = new ActionBarTypeAnnouncement(subIndex, name, permissionName, delay, content);
                break;
            default:
                shownAnn = null;
        }
    }

    @Override
    public boolean send(CommandSender sender) {
        return shownAnn.send(sender);
    }

    public AnnouncementType getShownType() {
        return shownType;
    }

    public Announcement getShownAnn() {
        return shownAnn;
    }

    public static List<Announcement> getPreAnn_shown() {
        return preAnn_shown;
    }

    public Date getDate() {
        return date;
    }
}
