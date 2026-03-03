package com.office_request_demo.vm;

import java.util.ArrayList;

import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import com.office_request_demo.dto.RequestOverviewDTO;
import com.office_request_demo.services.RequestService;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class DashboardVM {

    private RequestOverviewDTO overview;

    @WireVariable
    private RequestService requestService;

    @Init
    public void init() {

        this.overview = requestService.getOverview();
        if (this.overview.getMetrics() == null) {
            this.overview.setMetrics(new ArrayList<>());
        }
    }

}
