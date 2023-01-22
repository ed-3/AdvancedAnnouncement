package me.ed333.plugin.advancedannouncement.utils;

import me.ed333.plugin.advancedannouncement.ConfigKeys;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            case "h":
                return value * 3600;
            case "d":
                return value * 86400;
            case "week":
                return value * 86400 * 7;
            case "month":
                return value * 86400 * 30;
            case "year":
                return value * 86400 * 365;
            default:
                throw new IllegalArgumentException("Invalid time unit: " + unit);
        }
    }

    public static long getTimeSecRemain(@NotNull Date toDate) {
        Date now = new Date(System.currentTimeMillis());
        long timeSecBetween = (toDate.getTime() - now.getTime())/1000;
        if (timeSecBetween <= 0) {
            return 0;
        } else {
            return timeSecBetween;
        }
    }

    private static final SimpleDateFormat sdf = new SimpleDateFormat(ConfigKeys.DATE_FORMAT);
    public static Date toDate(String dateStr) throws ParseException {
        return sdf.parse(dateStr);
    }
}
