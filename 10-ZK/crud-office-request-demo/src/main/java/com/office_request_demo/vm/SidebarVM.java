package com.office_request_demo.vm;

import java.util.List;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.Executions;

import com.office_request_demo.ui.config.SidebarPageConfig;
import com.office_request_demo.ui.model.MenuItem;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SidebarVM {

    private SidebarPageConfig pageConfig = new SidebarPageConfig();

    private MenuItem selectedMenu;

    public List<MenuItem> getMenus() {
        return pageConfig.getMenus();
    }

    @Command
    public void navigate(@BindingParam("item") MenuItem page) {
        Executions.sendRedirect(page.getUri());
    }
}
