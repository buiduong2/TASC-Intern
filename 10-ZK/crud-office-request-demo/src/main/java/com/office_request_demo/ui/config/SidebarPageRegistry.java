package com.office_request_demo.ui.config;

import java.util.LinkedHashMap;
import java.util.Map;

import com.office_request_demo.ui.enums.PageKey;
import com.office_request_demo.ui.model.MenuItem;

public class SidebarPageRegistry {

    private final Map<PageKey, MenuItem> pages = new LinkedHashMap<>();

    public SidebarPageRegistry() {

        pages.put(PageKey.DASHBOARD,
                new MenuItem("Dashboard", "/dashboard.zul", "z-icon-home"));

        pages.put(PageKey.CREATE_REQUEST,
                new MenuItem("Create Request", "/request/create.zul", "z-icon-plus"));

        pages.put(PageKey.MY_REQUESTS,
                new MenuItem("My Requests", "/request/list.zul", "z-icon-list"));

        pages.put(PageKey.MANAGE_REQUESTS,
                new MenuItem("Manage Requests", "/request/list.zul", "z-icon-tasks"));

        pages.put(PageKey.PROFILE,
                new MenuItem("Manage Requests", "/profile.zul", "z-icon-profile"));
    }

    public MenuItem get(PageKey key) {
        return pages.get(key);
    }

    public Map<PageKey, MenuItem> getPages() {
        return pages;
    }
}
