package com.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import com.model.LogLevel;
import com.model.LogMeta;
import com.utils.LogParsers;

/**
 * phục vụ truy vấn nhanh
 */
public class LogMetaCache {
    private Map<LogLevel, Set<LogMeta>> levelCache;
    private Map<String, Set<LogMeta>> serviceCache;
    private ConcurrentSkipListMap<Long, LogMeta> timestampCache;
    private List<LogMeta> logMetas;

    public LogMetaCache() {
        logMetas = new ArrayList<>();
        levelCache = new ConcurrentHashMap<>();
        serviceCache = new ConcurrentHashMap<>();
        timestampCache = new ConcurrentSkipListMap<>();
    }

    public int getTotalLine() {
        return logMetas.size();
    }

    public LinkedHashSet<LogMeta> getLinkedLogMetas() {
        return new LinkedHashSet<>(logMetas);
    }

    public Collection<LogMeta> getByTimestampRange(long start, long end) {
        SortedMap<Long, LogMeta> sortedMap = timestampCache.subMap(start, true, end, true);
        return sortedMap.values();
    }

    public Set<LogMeta> getByService(String serviceName) {
        return serviceCache.getOrDefault(serviceName, new HashSet<>());
    }

    public Set<LogMeta> getByLevel(LogLevel loglevel) {
        return levelCache.getOrDefault(loglevel, new HashSet<>());
    }

    public void addToCache(LogMeta logMeta, String line) {
        LogParsers.parseMeta(logMeta, line);
        levelCache.computeIfAbsent(logMeta.getLevel(), k -> ConcurrentHashMap.newKeySet())
                .add(logMeta);

        serviceCache.computeIfAbsent(logMeta.getSerivce(), k -> ConcurrentHashMap.newKeySet())
                .add(logMeta);

        timestampCache.put(logMeta.getTimestamp(), logMeta);

    }

    public void setLogMetas(List<LogMeta> logMetas) {
        this.logMetas = logMetas;
    }

}
