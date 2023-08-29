package top.ed333.mcplugin.advancedann.bukkit.utils;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

public class BukkitAdapter {
    public static BarColor toBukkit_barColor(top.ed333.mcplugin.advancedann.common.objects.bossbar.BarColor color) {
        switch (color) {
            case RED:
                return BarColor.RED;
            case BLUE:
                return BarColor.BLUE;
            case PINK:
                return BarColor.PINK;
            case GREEN:
                return BarColor.GREEN;
            case WHITE:
                return BarColor.WHITE;
            case PURPLE:
                return BarColor.PURPLE;
            case YELLOW:
                return BarColor.YELLOW;
        }
        return BarColor.PINK;
    }

    public static BarStyle toBukkit_barStyle(top.ed333.mcplugin.advancedann.common.objects.bossbar.BarStyle style) {
        switch (style) {
            case SOLID:
                return BarStyle.SOLID;
            case SEGMENTED_6:
                return BarStyle.SEGMENTED_6;
            case SEGMENTED_10:
                return BarStyle.SEGMENTED_10;
            case SEGMENTED_12:
                return BarStyle.SEGMENTED_12;
            case SEGMENTED_20:
                return BarStyle.SEGMENTED_20;
        }
        return BarStyle.SOLID;
    }
}
