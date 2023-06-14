package me.ed333.plugin.advancedannouncement.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// deal time unit
public class TimeHandler {
    private static final Pattern TIME_INTERVAL_PATTERN = Pattern.compile("^(\\d+)(s|min|h|d|week|month|year)$");

    public static int parse(String timeInterval) {
        Matcher matcher = TIME_INTERVAL_PATTERN.matcher(timeInterval);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid time interval: " + timeInterval);
        }
        int value = Integer.parseInt(matcher.group(1));
        String unit = matcher.group(2);
        switch (unit) {
            case "s":
                return value;
            case "min":
                return value * 60;
            default:
                throw new IllegalArgumentException("Invalid time unit: " + unit);
        }
    }
}
