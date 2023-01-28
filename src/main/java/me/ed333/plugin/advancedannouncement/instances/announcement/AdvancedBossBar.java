package me.ed333.plugin.advancedannouncement.instances.announcement;

import me.ed333.plugin.advancedannouncement.utils.TextHandler;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.jetbrains.annotations.NotNull;

public class AdvancedBossBar implements Cloneable {
    BossBar bar;
    final String title;
    final BarColor color;
    final BarStyle style;
    final double update;
    final double stay;
    final double delay;
    final boolean progress;
    final double nextDelay;

    AdvancedBossBar(
            @NotNull String title,
            @NotNull BarColor color,
            @NotNull BarStyle style,
            double delay,
            double update,
            double nextDelay,
            double stay,
            boolean progress
    ) {
        bar = Bukkit.createBossBar(TextHandler.handleColor(title, null), color, style);
        this.color = color;
        this.style = style;
        this.title = TextHandler.handleColor(title, null);
        this.update = update;
        this.stay = stay;
        this.delay = delay;
        this.progress = progress;
        this.nextDelay = nextDelay;
    }

    @Override
    public String toString() {
        return "AdvancedBossBar{" + "bar=" + bar +
                ", title='" + title + '\'' +
                ", update=" + update +
                ", stay=" + stay +
                ", delay=" + delay +
                ", progress=" + progress +
                '}';
    }

    @Override
    public @NotNull AdvancedBossBar clone() {
        try {
            AdvancedBossBar clone = (AdvancedBossBar) super.clone();
            clone.bar = Bukkit.createBossBar(title, color, style);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}