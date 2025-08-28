package com.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.exception.LogFormatException;
import com.model.LogLevel;
import com.model.LogMeta;

public class LogParsers {
    private static final Pattern LOG_PATTERN = Pattern
            .compile("^\\[(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2})\\] \\[([A-Z]+)\\] \\[(\\S+)\\] - (.+)\\s*$");

    public static void parseMeta(LogMeta log, String line) {

        Matcher matcher = LOG_PATTERN.matcher(line);
        if (matcher.matches()) {

            String timestamp = matcher.group(1);
            String level = matcher.group(2);
            String service = matcher.group(3);

            LogParsers.readTimestamp(log, timestamp);
            LogParsers.readLevel(log, level);
            LogParsers.readService(log, service);

        }
    }

    public static void readTimestamp(LogMeta log, String timestamp) {
        log.setTimestamp(Helpers.parseTimestampLong(timestamp));
    }

    public static void readLevel(LogMeta log, String str) {
        LogLevel level = LogLevel.valueOf(str);
        if (level == null) {
            throw new LogFormatException("Level Not found");
        }
        log.setLevel(level);

    }

    public static void readService(LogMeta log, String service) {
        log.setSerivce(service);
    }

    public static String parseMessage(String line) {
        Matcher matcher = LOG_PATTERN.matcher(line);
        if (matcher.matches()) {
            return matcher.group(4);
        }
        return null;
    }

}
