package me.ed333.plugin.advancedannouncement.utils;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderRegex {
    public static final String MAGIC_CODE_REGEX_STRING = "&[K-Ok-oRr]";
    public static final String LEGACY_CODE_REGEX_STRING = "&[0-9A-Fa-fK-Ok-oRr]";
    public static final String COLOR_PLACEHOLDER_REGEX_STRING = "\\{(#[A-Fa-f0-9]{6}(->#[A-Fa-f0-9]{6})?|rainbow)((,&[K-Ok-oRr])+)?\\}|&[0-9A-Fa-fK-Ok-oRr]";
    public static final String HEX_COLOR_CODE_REGEX_STRING = "#[A-Fa-f0-9]{6}";
    public static final String HEX_COLOR_PLACEHOLDER_REGEX_STRING = "\\{#[A-Fa-f0-9]{6}((,&[K-Ok-oRr])+)?\\}";
    public static final String GRADIENT_COLOR_PLACEHOLDER_REGEX_STRING = "\\{#[A-Fa-f0-9]{6}->#[A-Fa-f0-9]{6}((,&[K-Ok-oRr])+)?\\}";
    public static final String RAINBOW_PLACEHOLDER_REGEX_STRING = "\\{rainbow((,&[K-Ok-oRr])+)?\\}";
    public static final String COMPONENT_PLACEHOLDER_REGEX_STRING = "@[A-Za-z0-9-_]+@";

    public static @NotNull Pattern compilePattern(String regex) {
        return Pattern.compile(regex);
    }

    public static @NotNull Matcher getRegexMatcher(String regex, String matchText) {
        return compilePattern(regex).matcher(matchText);
    }

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
