package com.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.log.Log;

public class LogParsers {

    final Pattern LOG_PATTERN;
    final DateTimeFormatter formatter;
    final static int TIMESTAMP_INDEX = 1;
    final static int LEVEL_INDEX = 2;
    final static int SERVICE_INDEX = 3;
    final static int MESSAGE_INDEX = 4;

    public LogParsers(String logRegex, String timestampRegex) {
        LOG_PATTERN = Pattern.compile(logRegex);
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    public Log parserToLogIgnoreTimestamp(String line) {
        if (Helpers.isNullOrBlank(line)) {
            return null;
        }
        Matcher matcher = LOG_PATTERN.matcher(line);
        if (matcher.matches()) {
            String level = matcher.group(LEVEL_INDEX);
            String service = matcher.group(SERVICE_INDEX);
            String message = matcher.group(MESSAGE_INDEX);
            Log log = new Log(null, level, service, message);
            return log;
        } else {
            throw new RuntimeException("Log Regex not match: " + line);
        }
    }

    public long parseLineToTimestamp(String line) {
        if (Helpers.isNullOrBlank(line)) {
            throw new IllegalArgumentException("Line must not be null nor empty");
        }
        Matcher matcher = LOG_PATTERN.matcher(line);
        if (matcher.matches()) {
            String timestamp = matcher.group(TIMESTAMP_INDEX);
            return parseTimestampLong(timestamp);
        }
        throw new IllegalArgumentException("Line not match");
    }

    public long parseTimestampLong(String timestamp) {
        if (timestamp == null || timestamp.isEmpty()) {
            throw new IllegalArgumentException("Timestamp is null or empty");
        }

        LocalDateTime dateTime = LocalDateTime.parse(timestamp, formatter);

        return dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
    }
}
