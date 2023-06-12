package me.ed333.plugin.advancedannouncement.announcement;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

public class BossBarTextSettings {
    double stay = 5;
    double delaySec = 0;
    double nextDelay = 0;
    double update = -1;
    public boolean progress = false;
    public BarColor barColor = BarColor.PURPLE;
    public BarStyle style = BarStyle.SOLID;

    BossBarTextSettings() {}

    /**
     * @param nextDelay total delay sec to next, (Count form the first one.)
     */
    BossBarTextSettings(double stay, double delaySec, double nextDelay, double update, boolean progress, BarColor barColor, BarStyle style) {
        this.stay = stay;
        this.delaySec = delaySec;
        this.nextDelay = nextDelay;
        this.update = update;
        this.progress = progress;
        this.barColor = barColor;
        this.style = style;
    }
}
