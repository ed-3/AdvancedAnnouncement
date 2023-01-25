package me.ed333.plugin.advancedannouncement.instances.announcement;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BossBarPlaceholderRegex {
    public static final String PLACEHOLDER_REGEX_STRING = "\\{progress:(true|false)\\}|\\{(stay|update|delay):(0|[1-9])(\\.\\d)?\\}|(\\{color:[A-Za-z]+\\})|\\{segment:(6|10|12|20)\\}";
    public static final String STAY_PLACEHOLDER_REGEX_STRING = "\\{stay:(0|[1-9])(\\.\\d)?\\}";
    public static final String COLOR_PLACEHOLDER_REGEX_STRING = "\\{color:[A-Za-z]+\\}";
    public static final String PROGRESS_PLACEHOLDER_REGEX_STRING = "\\{progress:(true|false)\\}";
    public static final String UPDATE_PLACEHOLDER_REGEX_STRING = "\\{update:(0|[1-9])(\\.\\d)?\\}";
    public static final String DELAY_PLACEHOLDER_REGEX_STRING = "\\{delay:(0|[1-9])(\\.\\d)?\\}";
    public static final String SEGMENT_PLACEHOLDER_REGEX_STRING = "\\{segment:(6|10|12|20)\\}";

    public static @NotNull Pattern compilePattern(String regex) {
        return Pattern.compile(regex);
    }

    public static @NotNull Matcher getRegexMatcher(String regex, String matchText) {
        return compilePattern(regex).matcher(matchText);
    }

    public static @NotNull Matcher getPLACEHOLDER_REGEX_MATCHER(String matchText) {
        return getRegexMatcher(PLACEHOLDER_REGEX_STRING, matchText);
    }

    public static @NotNull Matcher getSTAY_PLACEHOLDER_REGEX_MATCHER(String matchText) {
        return getRegexMatcher(STAY_PLACEHOLDER_REGEX_STRING, matchText);
    }

    public static @NotNull Matcher getCOLOR_PLACEHOLDER_REGEX_MATCHER(String matchText) {
        return getRegexMatcher(COLOR_PLACEHOLDER_REGEX_STRING, matchText);
    }

    public static @NotNull Matcher getUPDATE_PLACEHOLDER_REGEX_MATCHER(String matchText) {
        return getRegexMatcher(UPDATE_PLACEHOLDER_REGEX_STRING, matchText);
    }

    public static @NotNull Matcher getSEGMENT_PLACEHOLDER_REGEX_MATCHER(String matchText) {
        return getRegexMatcher(SEGMENT_PLACEHOLDER_REGEX_STRING, matchText);
    }

    public static @NotNull Matcher getDELAY_PLACEHOLDER_REGEX_MATCHER(String matchText) {
        return getRegexMatcher(DELAY_PLACEHOLDER_REGEX_STRING, matchText);
    }

    protected static double getSec(@NotNull String matchText) {
        double result = 0.0;
        Matcher matcher = getRegexMatcher("(0|[1-9])(\\.\\d)?", matchText);
        if (matcher.find()) {
            result = Double.parseDouble(matcher.group());
        }
        return result;
    }

    protected static boolean getBool(@NotNull String matchText) {
        boolean result = false;
        Matcher matcher = getRegexMatcher("true|false", matchText);
        if (matcher.find()) {
            result = Boolean.parseBoolean(matcher.group());
        }
        return result;
    }

    protected static @NotNull BarColor getBarColor(@NotNull String matchText) {
        BarColor color = BarColor.PURPLE;
        Matcher matcher = getRegexMatcher("PINK|BLUE|RED|GREEN|YELLOW|PURPLE|WHITE", matchText);
        if (matcher.find()) {
            try {
                color = BarColor.valueOf(matcher.group());
            } catch (IllegalArgumentException ignored) {}
        }
        return color;
    }

    protected static @NotNull BarStyle getBarStyle(@NotNull String matchText) {
        BarStyle style = BarStyle.SOLID;
        Matcher matcher = getRegexMatcher("6|10|12|20", matchText);
        if (matcher.find()) {
            try {
                style = BarStyle.valueOf("SEGMENTED_" + matcher.group());
            } catch (IllegalArgumentException ignored) {}
        }
        return style;
    }
}
