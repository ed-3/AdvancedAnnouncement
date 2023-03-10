package me.ed333.plugin.advancedannouncement.instances.announcement;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActionBarPlaceholderRegex {
    public static final String ACTION_BAR_PLACEHOLDER_REGEX_STRING = "\\{(stay|delay):(0|[1-9])(\\.\\d)?\\}";
    public static final String STAY_SEC_REGEX_STRING = "\\{stay:(0|[1-9])(\\.\\d)?\\}";
    public static final String DELAY_TO_NEXT_REGEX_STRING = "\\{delay:(0|[1-9])(\\.\\d)?\\}";

    public static @NotNull Pattern compilePattern(String regex) {
        return Pattern.compile(regex);
    }

    public static @NotNull Matcher getRegexMatcher(String regex, String matchText) {
        return compilePattern(regex).matcher(matchText);
    }

    public static @NotNull Matcher getACTION_BAR_PLACEHOLDER_REGEX_MATCHER(String matchText) {
        return getRegexMatcher(ACTION_BAR_PLACEHOLDER_REGEX_STRING, matchText);
    }

    public static @NotNull Matcher getDELAY_TO_NEXT_REGEX_MATCHER(String matchText) {
        return getRegexMatcher(DELAY_TO_NEXT_REGEX_STRING, matchText);
    }

    public static @NotNull Matcher getSTAY_SEC_REGEX_MATCHER(String matchText) {
        return getRegexMatcher(STAY_SEC_REGEX_STRING, matchText);
    }

    protected static double getSec(@NotNull String matchText) {
        double result = 0.0;
        Matcher matcher = getRegexMatcher("(0|[1-9])(\\.\\d)?", matchText);
        if (matcher.find()) {
            result = Double.parseDouble(matcher.group());
        }
        return result;
    }
}
