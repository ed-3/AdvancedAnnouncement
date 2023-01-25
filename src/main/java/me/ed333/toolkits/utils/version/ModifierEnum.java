package me.ed333.toolkits.utils.version;

import org.jetbrains.annotations.Nullable;

/**
 * 常用的版本修饰词枚举
 */
public enum ModifierEnum {
    /**
     * 表示 内部版本 的修饰符
     */
    ALPHA("alpha"),
    /**
     * 表示 测试版本 的修饰符
     */
    BETA("beta"),
    /**
     * 表示 演示版本 的修饰符
     */
    DEMO("demo"),
    /**
     * 表示 增强版本 的修饰符
     */
    ENHANCE("enhance"),
    /**
     * 表示 自由版本 的修饰符
     */
    FREE("free"),
    /**
     * 表示 完整版，即正式版 的修饰符
     */
    FULL_VERSION("full_version"),
    /**
     * 表示 长期维护版本 的修饰符
     */
    LTS("lts"),
    /**
     * 表示 发行版 的修饰符, <b>不可设置版本号</b>
     */
    RELEASE("release"),
    /**
     * 表示 先行版 的修饰符
     */
    PRE_RELEASE("pre_release"),
    /**
     * 表示 即将作为正式版发布 的修饰符
     */
    RC("rc"),
    /**
     * 表示 标准版 的修饰符
     */
    STANDARD("standard"),
    /**
     * 表示 旗舰版 的修饰符
     */
    ULTIMATE("ultimate"),
    /**
     * 表示 升级版 的修饰符
     */
    UPGRADE("upgrade");


    private final String name;
    ModifierEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static @Nullable ModifierEnum forName(String name) {
        for (ModifierEnum value : values()) {
            if (value.name.equals(name)) {
                return value;
            }
        }
        return null;
    }
}
