package top.ed333.mcplugin.advancedann.bukkit.announcement;

import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.ed333.mcplugin.advancedann.bukkit.utils.ProtocolUtils;
import top.ed333.mcplugin.advancedann.common.utils.TextHandler;

import java.util.HashMap;
import java.util.Map;

public class AdvancedBossBar {
    final BossBar bar;
    final String title;
    final BossBarTextSettings settings;

    AdvancedBossBar(@NotNull String title, @NotNull BossBarTextSettings settings) {

        if (!BarManager.hasBar(title)) {
            bar = Bukkit.createBossBar(TextHandler.handleColor(title, ProtocolUtils.isLegacyServer()), settings.barColor, settings.style);
            BarManager.barMap.put(title.hashCode(), bar);
        } else {
            bar = BarManager.forTitle(title);
        }

        this.settings = settings;
        this.title = title;
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