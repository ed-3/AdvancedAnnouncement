package me.ed333.plugin.advancedannouncement.instances.announcement;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class Announcement {
    private final int index;
    private final String name;
    private final List<String> content;
    private final String permissionName;
    private final int delay;
    private final AnnouncementType type;

    /**
     * @param index the index of this announcement, auto generated when loading announcements
     * @param name the name of this announcement
     * @param type the {@linkplain AnnouncementType type} of this announcement
     * @param permissionName the permission name that this announcement will be required. can be null or an empty string.
     * @param delay the delay time to next announcement.
     * @param content the content of this announcement.
     */
    public Announcement(int index, @NotNull String name, AnnouncementType type, String permissionName, int delay, List<String> content) {
        this.index = index;
        this.name = name;
        this.content = content;
        this.permissionName = permissionName;
        this.delay = delay;
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public @NotNull String getName() {
        return name;
    }

    public int delay() {
        return delay;
    }

    public @Nullable String permissionName() {
        return permissionName;
    }

    public @NotNull AnnouncementType type() {
        return type;
    }

    public @NotNull List<String> content() {
        return content;
    }

    public void broadcast() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            String permissionName = permissionName();
            if (permissionName != null && !player.hasPermission(permissionName)) continue;
            send(player);
        }
    }

    public abstract boolean send(CommandSender sender);
}
