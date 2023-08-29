/**
 * Copyright (c) 2023. All rights reserved.
 *
 * 注意：你可以将这个代码应用到你的项目中，
 * 你允许的更改为:
 *   - 将包名更改成你自己的包名
 *   - 将以下内容应用到你自己的代码中(为了调用方便我把它放在了 Common 中)
 *     - {@link top.ed333.mcplugin.advancedann.common.objects.bossbar.BarColor}
 *     - {@link top.ed333.mcplugin.advancedann.common.objects.bossbar.BarStyle}
 *     - {@link top.ed333.mcplugin.advancedann.common.objects.bossbar.BarFlag}
 * 不允许的修改包括但不限于:
 *   - 对本代码进行混淆(你应该在你的混淆器中排除这个文件)
 *   - 删除本代码中未曾用到的方法、字段等
 *
 * ****************************
 *
 * Copyright (c) 2023. All rights reserved.
 *
 * Note: You can apply this code to your project,
 * The modifications you are allowed are below:
 * - Change the package name to your own
 * - Apply the following file to your own code(For convince I put them in 'Common' project)
 *     - {@link top.ed333.mcplugin.advancedann.common.objects.bossbar.BarColor}
 *     - {@link top.ed333.mcplugin.advancedann.common.objects.bossbar.BarStyle}
 *     - {@link top.ed333.mcplugin.advancedann.common.objects.bossbar.BarFlag}
 * Unallowed modifications include, but are not limited to:
 *   - Obfuscate the code (You should exclude this file in your obfuscator.)
 *   - Delete methods, fields, etc. that are not used in this code
 *
 */

package top.ed333.mcplugin.advancedann.bungee.objects.bossbar;

import com.google.common.base.Preconditions;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;
import org.jetbrains.annotations.NotNull;
import top.ed333.mcplugin.advancedann.common.objects.bossbar.BarColor;
import top.ed333.mcplugin.advancedann.common.objects.bossbar.BarFlag;
import top.ed333.mcplugin.advancedann.common.objects.bossbar.BarStyle;

import java.util.*;

/**
 * BungeeCord 发送 BossBar 的实现
 * @author ed-3/ed3
 */
public class BossBar {
    private final UUID uuid;
    private final Set<BarFlag> flags;
    private final Set<ProxiedPlayer> players;

    private BaseComponent[] titleContent;
    private BarColor color;
    private BarStyle style;
    // range from 0 to 1
    private float health;
    private boolean visible;

    protected BossBar(
            BaseComponent[] titleContent,
            BarColor color,
            BarStyle style,
            float health
    ) {
        this.titleContent = titleContent;
        this.color = color;
        this.style = style;
        this.flags = EnumSet.noneOf(BarFlag.class);
        this.uuid = UUID.randomUUID();
        this.players = new HashSet<>();
        this.visible = true;
        Preconditions.checkArgument(health <= 1 || health >= 0, "The value of health must be range from 0 to 1");
        this.health = health;
    }

    public void addPlayers(@NotNull Collection<ProxiedPlayer> players) {
        this.players.addAll(players);

        for (ProxiedPlayer player : players) {
            addPlayer(player);
        }
    }

    public void addPlayer(@NotNull ProxiedPlayer player) {
        this.players.add(player);

        if (player.isConnected() && visible) {
            sendPacket(player, getAddBarPacket());
        }
    }

    public void removePlayer(@NotNull ProxiedPlayer player) {
        this.players.remove(player);
        if (player.isConnected() && visible) {
            sendPacket(player, getRemoveBarPacket());
        }
    }

    public void removePlayers(@NotNull Collection<ProxiedPlayer> players) {
        this.players.removeAll(players);
        for (ProxiedPlayer player : players) {
            removePlayer(player);
        }
    }

    public void removeAllPlayer() {
        removePlayers(this.players);
    }

    public void setTitle(@NotNull BaseComponent[] titleContent) {
        this.titleContent = titleContent;
        if (visible) {
            net.md_5.bungee.protocol.packet.BossBar barPacket = new net.md_5.bungee.protocol.packet.BossBar(uuid, 3);
            barPacket.setTitle(ComponentSerializer.toString(titleContent));
            sendPacket(barPacket);
        }
    }

    public void setColor(@NotNull BarColor color) {
        this.color = color;

        net.md_5.bungee.protocol.packet.BossBar barPacket = new net.md_5.bungee.protocol.packet.BossBar(uuid, 4);
        barPacket.setColor(color.ordinal());
        sendPacket(barPacket);
    }

    public void setHealth(float health) {
        Preconditions.checkArgument(health <= 1 || health >= 0, "The value of health must be range from 0 to 1");
        this.health = health;

        if (visible) {
            net.md_5.bungee.protocol.packet.BossBar barPacket = new net.md_5.bungee.protocol.packet.BossBar(uuid, 2);
            barPacket.setHealth(health);
            sendPacket(barPacket);
        }
    }

    public void setStyle(@NotNull BarStyle style) {
        this.style = style;
        net.md_5.bungee.protocol.packet.BossBar barPacket = new net.md_5.bungee.protocol.packet.BossBar(uuid, 4);
        barPacket.setDivision(style.ordinal());
        sendPacket(barPacket);
    }

    public void setVisible(boolean visible) {
        if (this.visible && !visible) {
            sendPacket(getRemoveBarPacket());
        } else if (!this.visible && visible) {
            sendPacket(getAddBarPacket());
        }
        this.visible = visible;
    }

    public void addFlags(@NotNull Collection<BarFlag> flags) {
        if (this.flags.addAll(flags) && visible) {
            sendPacket(getUpdateFlagPacket());
        }
    }

    public void addFlag(@NotNull BarFlag flag) {
        if (this.flags.add(flag) && visible) {
            sendPacket(getUpdateFlagPacket());
        }
    }

    public void removeFlag(@NotNull BarFlag flag) {
        if (this.flags.remove(flag) && visible) {
            sendPacket(getUpdateFlagPacket());
        }
    }

    public void removeFlags(@NotNull Collection<BarFlag> flags) {
        if (this.flags.removeAll(flags) && visible) {
            sendPacket(getUpdateFlagPacket());
        }
    }

    public void removeAllFlags() {
        removeFlags(this.flags);
    }

    public Set<ProxiedPlayer> getPlayers() {
        return Collections.unmodifiableSet(players);
    }

    public BarColor getColor() {
        return color;
    }

    public BarStyle getStyle() {
        return style;
    }

    public BaseComponent[] getTitleContent() {
        return titleContent;
    }

    public Set<BarFlag> getFlags() {
        return Collections.unmodifiableSet(flags);
    }

    public float getHealth() {
        return health;
    }

    public UUID getUuid() {
        return uuid;
    }

    public static final class Builder {
        private BaseComponent[] titleContent = new ComponentBuilder("Title").color(ChatColor.WHITE).create();
        private BarColor color = BarColor.PINK;
        private BarStyle style = BarStyle.SOLID;
        private Set<BarFlag> flags = EnumSet.noneOf(BarFlag.class);
        // range from 0 to 1
        private float health = 1;
        private Set<ProxiedPlayer> players = new HashSet<>();
        private boolean visible = true;

        public Builder title(BaseComponent... components) {
            this.titleContent = components;
            return this;
        }

        public Builder color(BarColor color) {
            this.color = color;
            return this;
        }

        public Builder style(BarStyle style) {
            this.style = style;
            return this;
        }

        public Builder addFlag(BarFlag flag) {
            if (flags.contains(flag)) {
                throw new IllegalArgumentException("BossBar already has the same flag: " + flag.name());
            }
            this.flags.add(flag);
            return this;
        }

        public Builder addFlag(BarFlag... flags) {
            this.flags.addAll(Arrays.asList(flags));
            return this;
        }

        public Builder health(float health) {
            Preconditions.checkArgument(health <= 1 || health >= 0, "The value of health must be range from 0 to 1");
            this.health = health;
            return this;
        }

        public Builder visible(boolean visible) {
            this.visible = visible;
            return this;
        }

        public Builder addPlayers(Collection<ProxiedPlayer> players) {
            this.players.addAll(players);
            return this;
        }

        public Builder addPlayer(ProxiedPlayer player) {
            this.players.add(player);
            return this;
        }

        public BossBar build() {
            BossBar result = new BossBar(titleContent, color, style, health);
            result.addFlags(flags);
            result.setVisible(visible);
            result.addPlayers(players);
            return result;
        }

    }

    private void sendPacket(DefinedPacket packet) {
        for (ProxiedPlayer player : this.players) {
            if (player.isConnected() && visible) {
                sendPacket(player, packet);
            }
        }
    }

    private void sendPacket(ProxiedPlayer player, DefinedPacket packet) {
        if (player.getPendingConnection().getVersion() >= ProtocolConstants.MINECRAFT_1_9) {
            player.unsafe().sendPacket(packet);
        }
    }

    private byte serializeFlags() {
        byte flagMask = 0x0;
        if (flags.contains(BarFlag.DARKEN_SCREEN)) {
            flagMask |= 0x1;
        }
        if (flags.contains(BarFlag.PLAY_BOSS_MUSIC)) {
            flagMask |= 0x2;
        }
        if (flags.contains(BarFlag.CREATE_WORLD_FOG)) {
            flagMask |= 0x4;
        }
        return flagMask;
    }

    private DefinedPacket getAddBarPacket() {
        net.md_5.bungee.protocol.packet.BossBar barPacket = new net.md_5.bungee.protocol.packet.BossBar(uuid, 0);
        barPacket.setTitle(ComponentSerializer.toString(titleContent));
        barPacket.setColor(color.ordinal());
        barPacket.setDivision(style.ordinal());
        barPacket.setHealth(health);
        barPacket.setFlags(serializeFlags());
        return barPacket;
    }

    private DefinedPacket getRemoveBarPacket() {
        return new net.md_5.bungee.protocol.packet.BossBar(uuid, 1);
    }

    private DefinedPacket getUpdateFlagPacket() {
        net.md_5.bungee.protocol.packet.BossBar barPacket = new net.md_5.bungee.protocol.packet.BossBar(uuid, 5);
        barPacket.setHealth(serializeFlags());
        return barPacket;
    }
}
