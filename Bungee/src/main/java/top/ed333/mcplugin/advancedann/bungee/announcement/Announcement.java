package top.ed333.mcplugin.advancedann.bungee.announcement;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.ed333.mcplugin.advancedann.bungee.AdvancedAnnouncement;
import top.ed333.mcplugin.advancedann.bungee.utils.ProtocolUtils;
import top.ed333.mcplugin.advancedann.bungee.utils.SchedulerUtils;
import top.ed333.mcplugin.advancedann.common.announcement.AnnouncementType;

import java.util.Collection;
import java.util.List;

public abstract class Announcement {
    private final int index;
    private final String name;
    private final List<String> content;
    private final String permissionName;
    private final int delay;
    private final AnnouncementType type;
    private final List<String> servers;

    public Announcement(
            int index,
            String name,
            List<String> content,
            String permissionName,
            int delay,
            AnnouncementType type,
            List<String> servers
    ) {
        this.index = index;
        this.name = name;
        this.content = content;
        this.permissionName = permissionName;
        this.delay = delay;
        this.type = type;
        this.servers = servers;
    }

    public int getIndex() {
        return index;
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
                if ((permissionName != null && !permissionName.isEmpty()) && !player.hasPermission(permissionName)) {
                    continue;
                }

                Server playerServer = player.getServer();
                if (!servers.contains(playerServer.getInfo().getName())) return;

                send(player, !ProtocolUtils.canHandleRGB(player));
            }
        });
    }

    public List<String> getEnabledServers() {
        return servers;
    }

    public abstract void send(ProxiedPlayer sender, boolean isLegacy);
}
