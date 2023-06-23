package top.ed333.mcplugin.advancedann.bukkit.announcement;

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
}
