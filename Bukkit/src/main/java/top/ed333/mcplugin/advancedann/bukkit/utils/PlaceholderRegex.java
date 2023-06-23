package top.ed333.mcplugin.advancedann.bukkit.utils;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderRegex {
    public static class TextRegex {
        public static final String MAGIC_CODE_REGEX_STRING = "&[K-Ok-oRr]";
        public static final String LEGACY_CODE_REGEX_STRING = "&[0-9A-Fa-fK-Ok-oRr]";
        public static final String COLOR_PLACEHOLDER_REGEX_STRING = "\\{(#[A-Fa-f0-9]{6}(->#[A-Fa-f0-9]{6})?|rainbow)((,&[K-Ok-oRr])+)?\\}|&[0-9A-Fa-fK-Ok-oRr]";
        public static final String HEX_COLOR_CODE_REGEX_STRING = "#[A-Fa-f0-9]{6}";
        public static final String HEX_COLOR_PLACEHOLDER_REGEX_STRING = "\\{#[A-Fa-f0-9]{6}((,&[K-Ok-oRr])+)?\\}";
        public static final String GRADIENT_COLOR_PLACEHOLDER_REGEX_STRING = "\\{#[A-Fa-f0-9]{6}->#[A-Fa-f0-9]{6}((,&[K-Ok-oRr])+)?\\}";
        public static final String RAINBOW_PLACEHOLDER_REGEX_STRING = "\\{rainbow((,&[K-Ok-oRr])+)?\\}";
        public static final String COMPONENT_PLACEHOLDER_REGEX_STRING = "@[A-Za-z0-9-_]+@";

        public static @NotNull Matcher getHEX_COLOR_CODE_MATCHER(String matchText) {
            return getRegexMatcher(HEX_COLOR_CODE_REGEX_STRING, matchText);
        }

        public static @NotNull Matcher getCOMPONENT_PLACEHOLDER_MATCHER(String matchText) {
            return getRegexMatcher(COMPONENT_PLACEHOLDER_REGEX_STRING, matchText);
        }

        public static @NotNull Matcher getCOLOR_PLACEHOLDER_MATCHER(String matchText) {
            return getRegexMatcher(COLOR_PLACEHOLDER_REGEX_STRING, matchText);
        }

        public static @NotNull Matcher getMAGIC_CODE_MATCHER(String matchText) {
            return getRegexMatcher(MAGIC_CODE_REGEX_STRING, matchText);
        }
    }

    public static class BossBarRegex {
        public static final String PLACEHOLDER_REGEX_STRING = "\\{progress:(true|false)\\}|\\{(stay|update|delay):(0|[1-9])(\\.\\d)?\\}|(\\{color:[A-Za-z]+\\})|\\{segment:(6|10|12|20)\\}";
        public static final String STAY_PLACEHOLDER_REGEX_STRING = "\\{stay:(0|[1-9])(\\.\\d)?\\}";
        public static final String COLOR_PLACEHOLDER_REGEX_STRING = "\\{color:[A-Za-z]+\\}";
        public static final String PROGRESS_PLACEHOLDER_REGEX_STRING = "\\{progress:(true|false)\\}";
        public static final String UPDATE_PLACEHOLDER_REGEX_STRING = "\\{update:(0|[1-9])(\\.\\d)?\\}";
        public static final String DELAY_PLACEHOLDER_REGEX_STRING = "\\{delay:(0|[1-9])(\\.\\d)?\\}";
        public static final String SEGMENT_PLACEHOLDER_REGEX_STRING = "\\{segment:(6|10|12|20)\\}";

        public static @NotNull Matcher getPLACEHOLDER_REGEX_MATCHER(String matchText) {
            return PlaceholderRegex.getRegexMatcher(PLACEHOLDER_REGEX_STRING, matchText);
        }

        public static @NotNull BarColor getBarColor(@NotNull String matchText) {
            BarColor color = BarColor.PURPLE;
            Matcher matcher = PlaceholderRegex.getRegexMatcher("PINK|BLUE|RED|GREEN|YELLOW|PURPLE|WHITE", matchText);
            if (matcher.find()) {
                try {
                    color = BarColor.valueOf(matcher.group());
                } catch (IllegalArgumentException ignored) {}
            }
            return color;
        }

        public static @NotNull BarStyle getBarStyle(@NotNull String matchText) {
            BarStyle style = BarStyle.SOLID;
            Matcher matcher = PlaceholderRegex.getRegexMatcher("6|10|12|20", matchText);
            if (matcher.find()) {
                try {
                    style = BarStyle.valueOf("SEGMENTED_" + matcher.group());
                } catch (IllegalArgumentException ignored) {}
            }
            return style;
        }
    }

    public static class ActionBarRegex {
        public static final String ACTION_BAR_PLACEHOLDER_REGEX_STRING = "\\{(stay|delay):(0|[1-9])(\\.\\d)?\\}";
        public static final String STAY_SEC_REGEX_STRING = "\\{stay:(0|[1-9])(\\.\\d)?\\}";
        public static final String DELAY_TO_NEXT_REGEX_STRING = "\\{delay:(0|[1-9])(\\.\\d)?\\}";

        public static @NotNull Matcher getACTION_BAR_PLACEHOLDER_REGEX_MATCHER(String matchText) {
            return PlaceholderRegex.getRegexMatcher(ACTION_BAR_PLACEHOLDER_REGEX_STRING, matchText);
        }
    }

    public static @NotNull Pattern compilePattern(String regex) {
        return Pattern.compile(regex);
    }

    public static @NotNull Matcher getRegexMatcher(String regex, String matchText) {
        return compilePattern(regex).matcher(matchText);
    }

    public static double getSec(@NotNull String matchText) {
        double result = 0.0;
        Matcher matcher = PlaceholderRegex.getRegexMatcher("(0|[1-9])(\\.\\d)?", matchText);
        if (matcher.find()) {
            result = Double.parseDouble(matcher.group());
        }
        return result;
    }

    public static boolean getBool(@NotNull String matchText) {
        boolean result = false;
        Matcher matcher = PlaceholderRegex.getRegexMatcher("true|false", matchText);
        if (matcher.find()) {
            result = Boolean.parseBoolean(matcher.group());
        }
        return result;
    }
}
