package top.ed333.mcplugin.advancedann.bukkit.utils;

import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public enum LegacyColor {
    BLACK(0, 0, 0, ChatColor.BLACK),
    DARK_BLUE(0, 0, 170, ChatColor.DARK_BLUE),
    DARK_GREEN(0, 170, 0, ChatColor.DARK_GREEN),
    DARK_AQUA(0, 170, 170, ChatColor.DARK_AQUA),
    DARK_RED(170, 0, 0, ChatColor.DARK_RED),
    DARK_PURPLE(170, 0, 170, ChatColor.DARK_PURPLE),
    GOLD(255, 170, 0, ChatColor.GOLD),
    GRAY(170, 170, 170, ChatColor.GRAY),
    DARK_GRAY(85, 85, 85, ChatColor.DARK_GRAY),
    BLUE(85, 85, 255, ChatColor.BLUE),
    GREEN(85, 255, 85, ChatColor.GREEN),
    AQUA(85, 255, 255, ChatColor.AQUA),
    RED(255, 85, 85, ChatColor.RED),
    LIGHT_PURPLE(255, 85, 255, ChatColor.LIGHT_PURPLE),
    YELLOW(255, 255, 85, ChatColor.YELLOW),
    WHITE(255, 255, 255, ChatColor.WHITE);

    public final int r;
    public final int g;
    public final int b;
    public final ChatColor color;

    LegacyColor(int r, int g, int b, ChatColor color) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.color = color;
    }

    public static @Nullable ChatColor getLegacyClosestColor(Color target) {
        int closestIndex = -1;
        double closestDistance = Double.POSITIVE_INFINITY;

        // use euclidean metric
        for (int index = 0; index < values().length; index++) {
            LegacyColor legacyColor = values()[index];
            double distance =
                    Math.sqrt((legacyColor.r - target.getRed()) * (legacyColor.r - target.getRed()) +
                            (legacyColor.g - target.getGreen()) * (legacyColor.g - target.getGreen()) +
                            (legacyColor.b - target.getBlue()) * (legacyColor.b - target.getBlue()));
            if (distance < closestDistance) { // compare the closest
                closestIndex = index;
                closestDistance = distance;
            }
        }

        if (closestIndex >= 0) {
            LegacyColor closestLegacy = values()[closestIndex];
            return closestLegacy.color;
        }
        return null;
    }
}
