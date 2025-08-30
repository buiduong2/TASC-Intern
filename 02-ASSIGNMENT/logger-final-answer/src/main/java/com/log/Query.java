package com.log;

public class Query {
    private String service;
    private String level;
    private String keyword;
    private String after;
    private String before;

    public String getService() {
        return service;
    }

    public void setService(String serviceName) {
        this.service = serviceName;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String levelSearch) {
        this.level = levelSearch;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keywordSearch) {
        this.keyword = keywordSearch;
    }

    public String getAfter() {
        return after;
    }

    public void setAfter(String afterSearch) {
        this.after = afterSearch;
    }

    public String getBefore() {
        return before;
    }

    public void setBefore(String beforeSearch) {
        this.before = beforeSearch;
    }

}