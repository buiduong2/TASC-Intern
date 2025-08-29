package com.query;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import com.index.LogMetaCache;
import com.model.LogLevel;
import com.model.LogMeta;
import com.utils.Helpers;

/**
 * Tiến hành Query lấy ra LogMeta
 */
public class LogQuery {

    private final LogMetaCache cache;

    public LogQuery(LogMetaCache cache) {
        this.cache = cache;
    }

    List<LogMeta> execute(QueryCondition condition) {

        LinkedHashSet<LogMeta> result = cache.getLinkedLogMetas();

        if (condition.getAfter() != null && condition.getBefore() != null) {
            long start = Helpers.parseTimestampLong(condition.getAfter());
            long end = Helpers.parseTimestampLong(condition.getBefore());
            result.retainAll(cache.getByTimestampRange(start, end));

        }

        if (condition.getService() != null) {
            result.retainAll(cache.getByService(condition.getService()));
        }

        if (condition.getLevel() != null) {
            LogLevel level = LogLevel.valueOf(condition.getLevel());
            result.retainAll(cache.getByLevel(level));
        }

        return new ArrayList<>(result);
    }
}
