package top.ed333.mcplugin.advancedann.bungee.announcement;

import org.jetbrains.annotations.Nullable;
import top.ed333.mcplugin.advancedann.bungee.objects.bossbar.BossBar;
import top.ed333.mcplugin.advancedann.bungee.utils.ProtocolUtils;
import top.ed333.mcplugin.advancedann.common.utils.Serializer;
import top.ed333.mcplugin.advancedann.common.objects.bossbar.BarColor;
import top.ed333.mcplugin.advancedann.common.objects.bossbar.BarStyle;
import top.ed333.mcplugin.advancedann.common.utils.TextHandler;

import java.util.HashMap;
import java.util.Map;

public class AdvancedBossBar {
    final BossBar bar;
    final String title;
    final BossBarTextSettings settings;

    public AdvancedBossBar(String title, BossBarTextSettings settings) {
        if (!BarManager.hasBar(title)) {
            boolean legacy = ProtocolUtils.isLegacyServer();
            bar = new BossBar.Builder()
                    .title(
                            Serializer.serializeToComponent(
                                    TextHandler.constructToJsonArr(
                                            title,
                                            legacy
                                    )
                            )
                    ).style(settings.style)
                    .color(settings.barColor)
                    .build();
        } else {
            bar = BarManager.forTitle(title);
        }
        this.title = title;
        this.settings = settings;
    }

    public static class BossBarTextSettings {
        double stay = 5;
        double delaySec = 0;
        double nextDelay = 0;
        double update = -1;
        public boolean progress = false;
        public BarColor barColor = BarColor.PURPLE;
        public BarStyle style = BarStyle.SOLID;

        BossBarTextSettings() {}

        @Override
        public String toString() {
            return "BossBarTextSettings{" +
                    "stay=" + stay +
                    ", delaySec=" + delaySec +
                    ", nextDelay=" + nextDelay +
                    ", update=" + update +
                    ", progress=" + progress +
                    ", barColor=" + barColor +
                    ", style=" + style +
                    '}';
        }
    }

    private static class BarManager {
        private final static Map<Integer, BossBar> barMap = new HashMap<>();

        private static @Nullable BossBar forTitle(String title) {
            return forHash(title.hashCode());
        }

        private static @Nullable BossBar forHash(int hash) {
            return barMap.get(hash);
        }

        private static boolean hasBar(String text) {
            return hasBar(text.hashCode());
        }

        private static boolean hasBar(int hash) {
            return barMap.containsKey(hash);
        }
    }
}
