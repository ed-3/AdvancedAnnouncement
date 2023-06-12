package me.ed333.plugin.advancedannouncement;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.ed333.plugin.advancedannouncement.config.ConfigKeys;
import me.ed333.plugin.advancedannouncement.utils.GlobalConsoleSender;
import me.ed333.plugin.advancedannouncement.utils.LangUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

public class Updater {

    public static void check()  {
        GlobalConsoleSender.info(LangUtils.getLangText("update.check"));
        String repoUrl = "repos/ed-3/AdvancedAnnouncement/releases/latest";
        String updateUrl = "https://api.github.com/" + repoUrl;
        if (ConfigKeys.UPDATE_SOURCE == 0) {
            updateUrl = "https://gitee.com/api/v5/" + repoUrl;
        }

        JsonParser parser = new JsonParser();

        try {
            // send request
            URL url = new URL(updateUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);

            int statusCode = connection.getResponseCode();
            InputStreamReader inReader = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(inReader);
            String lines;
            StringBuilder responseBody = new StringBuilder();
            while ((lines = reader.readLine()) != null) {
                responseBody.append(lines);
            }

            reader.close();
            inReader.close();

            if (statusCode != 200) {
                GlobalConsoleSender.warn(LangUtils.getLangText("update.check-exception") + "HTTP: " + statusCode);
                throw new InvalidVersionException();
            }

            // compare version
            JsonObject object = parser.parse(responseBody.toString()).getAsJsonObject();
            String tag = object.get("tag_name").getAsString();
            Version latestVer = Version.parse(tag);
            Version pluginVer = Version.parse(AdvancedAnnouncement.INSTANCE.getDescription().getVersion());
            boolean result = latestVer.isNewer(pluginVer);
            if (!result) {
                GlobalConsoleSender.warn(LangUtils.parseLang("update.has-update-line1", pluginVer, latestVer));
                GlobalConsoleSender.warn(LangUtils.getLangText("update.has-update-line2"));
            } else {
                GlobalConsoleSender.info(LangUtils.getLangText("update.check-latest"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidVersionException ignored) {}
    }

    static class InvalidVersionException extends Exception {

        public InvalidVersionException(String msg) {
            super(msg);
        }

        public InvalidVersionException() {
            super();
        }
        public InvalidVersionException(String msg, Throwable throwable) {
            super(msg, throwable);
        }
    }

    static class Version {
        private final int major;
        private final int minor;
        private final int patch;
        private final VersionModifier modifier;

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

        public VersionModifier getModifier() {
            return modifier;
        }

        /**
         * 返回版本的字符串格式
         * <p>示例字符串： 2.5.2-rc4</p>
         * @return 版本的字符串格式
         */
        @Override
        public String toString() {
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
                return !version.modifier.getModifierName().equals("release");
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

    static class VersionModifier {
        private final int version;
        private final String modifierName;

        public VersionModifier(String modifierName, int version) {
            this.modifierName = modifierName;
            this.version = version;
        }

        public String getModifierName() {
            return this.modifierName;
        }

        public int getVersion() {
            return this.version;
        }

        @NotNull
        public String contentToString() {
            return this.version == 0 ? this.modifierName : this.modifierName + this.version;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (o != null && this.getClass() == o.getClass()) {
                VersionModifier that = (VersionModifier) o;
                return this.version == that.version && this.modifierName.equals(that.modifierName);
            } else {
                return false;
            }
        }
    }
}
