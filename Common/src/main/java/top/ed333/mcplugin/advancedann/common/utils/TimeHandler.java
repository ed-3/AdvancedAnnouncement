package top.ed333.mcplugin.advancedann.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// deal time unit
public class TimeHandler {
    private static final Pattern TIME_INTERVAL_PATTERN = Pattern.compile("^(\\d+)(s|min|h|d|week|month|year)$");

    // return millisecond
    public static int parse(String timeInterval) {
        Matcher matcher = TIME_INTERVAL_PATTERN.matcher(timeInterval);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid time interval: " + timeInterval);
        }
        int value = Integer.parseInt(matcher.group(1));
        String unit = matcher.group(2);
        switch (unit) {
            case "s":
                return value * 1000;
            case "min":
                return value * 60 * 1000;
            default:
                throw new IllegalArgumentException("Invalid time unit: " + unit);
        }
    }
}
