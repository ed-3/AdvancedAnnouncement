package top.ed333.mcplugin.advancedann.bungee.announcement;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.ed333.mcplugin.advancedann.bungee.AdvancedAnnouncement;
import top.ed333.mcplugin.advancedann.bungee.utils.SchedulerUtils;

import java.util.Collection;
import java.util.List;

public abstract class Announcement {
    private final int index;
    private final String name;
    private final List<String> content;
    private final String permissionName;
    private final int delay;
    private final AnnouncementType type;

    protected Announcement(
            int index,
            String name,
            List<String> content,
            String permissionName,
            int delay,
            AnnouncementType type
    ) {
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

    public @Nullable List<String> getWorlds() {
        return worlds;
    }

    public @NotNull List<String> getContent() {
        return content;
    }

    public @NotNull String getName() {
        return name;
    }

    public @NotNull AnnouncementType type() {
        return type;
    }

    public int getDelay() {
        return delay;
    }

    public @Nullable String getPermissionName() {
        return permissionName;
    }

    public void broadcast() {
        SchedulerUtils.runAsync(() -> {
            Collection<ProxiedPlayer> players = AdvancedAnnouncement.INSTANCE.getProxy().getPlayers();
            for (ProxiedPlayer player : players) {
                if (permissionName != null && !player.hasPermission(permissionName)) {
                    continue;
                }

                send(player, false);
            }
        });
    }

    public abstract void send(CommandSender sender, boolean legacy);
}
