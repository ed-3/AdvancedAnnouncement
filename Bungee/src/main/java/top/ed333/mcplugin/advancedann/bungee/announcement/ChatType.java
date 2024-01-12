package top.ed333.mcplugin.advancedann.bungee.announcement;

import com.google.gson.JsonArray;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import top.ed333.mcplugin.advancedann.bungee.utils.ProtocolUtils;
import top.ed333.mcplugin.advancedann.bungee.utils.SchedulerUtils;
import top.ed333.mcplugin.advancedann.common.utils.Serializer;
import top.ed333.mcplugin.advancedann.common.announcement.AnnouncementType;
import top.ed333.mcplugin.advancedann.common.utils.TextHandler;

import java.util.List;

public class ChatType extends Announcement {
    public ChatType(
            int index,
            String name,
            List<String> content,
            String permissionName,
            int delay,
            List<String> servers
    ) {
        super(index, name, content, permissionName, delay, AnnouncementType.CHAT, servers);
    }

    @Override
    public void send(ProxiedPlayer sender, boolean legacy) {
        SchedulerUtils.runAsync(() -> {
            for (String raw : getContent()) {
                JsonArray array = TextHandler.constructToJsonArr(raw, legacy);
                BaseComponent[] components = Serializer.serializeToComponent(array);
                ProtocolUtils.sendChat(sender, components);
            }
        });
    }
}
