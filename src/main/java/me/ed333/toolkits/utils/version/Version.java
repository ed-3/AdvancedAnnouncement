package me.ed333.toolkits.utils.version;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * 一个版本号的对象
 * <p>版本号命名规则：</p>
 * 主版本号.次版本号.修订号-修饰词 | e.g.: 2.5.2-beta5
 * <p>修饰词可有可无</p>
 */
public class Version {
    private int major;
    private int minor;
    private int patch;
    private VersionModifier modifier;

    /**
     * 构造一个 Version 的实例
     * @param major 主版本号
     * @param minor 次版本号
     * @param patch 修订号
     * @param modifier 版本号的修饰词, 无修饰词填写 null
     * @see VersionModifier
     */
    public Version(int major, int minor, int patch, VersionModifier modifier) {
        try {
            check(major, minor, patch);
        } catch (InvalidVersionException e) {
            try {
                throw new InvalidVersionException("Invalid version.", e);
            } catch (InvalidVersionException ex) {
                ex.printStackTrace();
            }
        }
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.modifier = modifier;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getPatch() {
        return patch;
    }

    public VersionModifier getModifier() {
        return modifier;
    }

    /**
     * 返回版本的字符串格式
     * <p>示例字符串： 2.5.2-rc4</p>
     * @return 版本的字符串格式
     */
    public String contentToString() {
        StringBuilder sb = new StringBuilder();
        sb.append(major);
        sb.append(".");
        sb.append(minor);
        sb.append(".");
        sb.append(patch);
        if (!(modifier == null)) {
            sb.append("-");
            sb.append(modifier.contentToString());
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return contentToString();
    }

    /**
     * 解析一个字符串为 Version 实例, (所用分隔符为 '.')
     * @param versionCode 字符串
     * @return Version 实例
     * @throws InvalidVersionException 版本号错误时抛出
     */
    @Contract("_ -> new")
    public static @NotNull Version parse(@NotNull String versionCode) throws InvalidVersionException {
        String[] withoutModifier = versionCode.split("-"); // spilt version and modifier
        String[] strSpilt = withoutModifier[0].split("\\."); // spilt Major, Minor and Path ver
        int major, minor, patch;


        if (strSpilt.length != 3) {
            throw new InvalidVersionException("Invalid major version length. version: " + versionCode);
        }

        if (!isNumber(strSpilt[0])) {
            throw new InvalidVersionException("Major version is an Invalid integer. Major: " + versionCode);
        }

        if (!isNumber(strSpilt[1])) {
            throw new InvalidVersionException("Minor version is an Invalid integer. Minor: " + versionCode);
        }

        if (!isNumber(strSpilt[2])) {
            throw new InvalidVersionException("Patch version is an Invalid integer. patch: " + versionCode);
        }

        major = Integer.parseInt(strSpilt[0]);
        minor = Integer.parseInt(strSpilt[1]);
        patch = Integer.parseInt(strSpilt[2]);

        VersionModifier modifier = null;
        int modifyIndex = versionCode.lastIndexOf("-");

        // create an instance of VersionModifier
        if (modifyIndex != -1) {
            String modifierName = versionCode.substring(modifyIndex + 1);
            int modifierVersion = 0;
            // deal VersionModifier version
            for (char c : modifierName.toCharArray()) {
                if (isNumber(c+"")) {
                    String str = modifierName.replaceAll(".*[^\\d](?=(\\d+))","");
                    if (isNumber(str)) {
                        modifierVersion = Integer.parseInt(str);
                    }
                }
            }

            if (modifierVersion == 0) {
                modifier = new VersionModifier(modifierName, 0);
            }else {
                modifier = new VersionModifier(modifierName.substring(0, modifierName.indexOf(modifierVersion + "")), modifierVersion);
            }
        }
        return new Version(major, minor, patch, modifier);
    }

    private static boolean isNumber(String str) {
        try {
            int i = Integer.parseInt(str);
            return i >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static void check(int major, int minor, int patch) throws InvalidVersionException {
        if (major < 0) {
            throw new InvalidVersionException("Cannot construct a version instance with negative major number. major: " + major);
        }
        if (minor < 0) {
            throw new InvalidVersionException("Cannot construct a version instance with negative minor number. minor: " + minor);
        }
        if (patch < 0) {
            throw new InvalidVersionException("Cannot construct a version instance with negative patch number. patch: " + patch);
        }
    }

    /**
     * 比较这个版本是否比指定版本更新一些
     * <p>比较规则：</p>
     * 1. 依次比较主版本号、次版本号和修订号的数值
     * <p>如：1.0.0 &lt; 1.0.1 &lt; 1.1.1 &lt; 2.0.0</p>
     * 2. 含有修饰符和不含有修饰符的版本号比较，优先比较第一条规则，若版本号相同，则比较修饰符版本
     * <p>如：2.5.0 &gt; 2.4.6-release, 2.5.0 &gt; 2.5.0-beta, 2.5.0-beta5 &gt; 2.5.0-beta</p>
     * 3. 暂时无法比较不同修饰符但相同版本号的版本
     * @param version 被比较的版本
     * @return 这个版本是否比指定版本更新一些
     */
    public boolean isNewer(@NotNull Version version) {
        if (major > version.major) {
            return true;
        }

        if (minor > version.minor) {
            return true;
        }

        if (patch > version.patch) {
            return true;
        }

        // contrast modifier version
        if (this.modifier != null) {
            if (this.modifier.getModifierName().equals("release")) {
                return true;
            } else {
                if (version.modifier == null) {
                    return false;
                } else {
                    if (version.modifier.getModifierName().equals("release")) {
                        return false;
                    } else {
                        if (this.modifier.getModifierName().equals(version.getModifier().getModifierName())) {
                            return this.modifier.getVersion() > version.getModifier().getVersion();
                        } else {
                            return false;
                        }
                    }
                }
            }
        } else {
            if (version.modifier.getModifierName().equals("release")) {
                return false;
            }
            return version.modifier != null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Version version = (Version) o;
        return major == version.major && minor == version.minor && patch == version.patch && Objects.equals(modifier, version.modifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor, patch, modifier);
    }
}
