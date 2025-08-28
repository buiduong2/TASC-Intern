package com.model;

/**
 * Metadata về log, không chứa message
 */
public class LogMeta {
    private long startOffset;
    private long endOffset;

    private long timestamp;
    private LogLevel level;
    private String serivce;

    public long getStartOffset() {
        return startOffset;
    }

    public void setStartOffset(long startOffset) {
        this.startOffset = startOffset;
    }

    public long getEndOffset() {
        return endOffset;
    }

    public void setEndOffset(long endOffset) {
        this.endOffset = endOffset;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public LogLevel getLevel() {
        return level;
    }

    public void setLevel(LogLevel level) {
        this.level = level;
    }

    public String getSerivce() {
        return serivce;
    }

    public void setSerivce(String serivce) {
        this.serivce = serivce;
    }

    @Override
    public String toString() {
        return "LogMeta [startOffset=" + startOffset + ", endOffset=" + endOffset + ", timestamp=" + timestamp
                + ", level=" + level + ", serivce=" + serivce + "]";
    }

}