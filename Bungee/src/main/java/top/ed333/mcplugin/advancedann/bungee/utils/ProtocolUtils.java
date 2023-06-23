package top.ed333.mcplugin.advancedann.bungee.utils;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.packet.Title;
import top.ed333.mcplugin.advancedann.bungee.AdvancedAnnouncement;

public class ProtocolUtils {
    // minor version
    public static final short SERVER_VERSION = Short.parseShort(AdvancedAnnouncement.INSTANCE.getProxy().getVersion().split("\\.")[1].split("-")[0]);

    public static boolean isLegacyServer() {
        return false;
    }

    public static void sendActionBar(ProxiedPlayer sendTo, String text) {
        Title title = new Title();
        title.setAction(Title.Action.ACTIONBAR);
        title.setText(text);
        sendTo.unsafe().sendPacket(title);
    }
}
