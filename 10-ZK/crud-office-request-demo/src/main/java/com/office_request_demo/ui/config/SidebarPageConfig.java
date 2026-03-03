package com.office_request_demo.ui.config;

import java.util.List;

import com.office_request_demo.ui.model.MenuItem;

public class SidebarPageConfig {

    private final SidebarPageRegistry registry = new SidebarPageRegistry();

    public List<MenuItem> getMenus() {
        return registry.getPages()
                .values()
                .stream().toList();
    }

}
