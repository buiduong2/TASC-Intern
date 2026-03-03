package com.office_request_demo.vm;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import com.office_request_demo.dto.RequestDTO;
import com.office_request_demo.services.RequestService;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class RequestListVM {

    @WireVariable
    private RequestService requestService;

    private Page<RequestDTO> page;

    @Init
    public void init() {

        this.syncPage();
    }

    public void syncPage() {
        this.page = new PageImpl<>(this.requestService.findAll());
    }

    @Command
    public void createItem() {
        Executions.sendRedirect("/request/create.zul");
    }

    @Command
    @NotifyChange("page")
    public void deleteItem(@BindingParam("event") RequestDTO dto) {

        this.requestService.delete(dto.getId());
        this.syncPage();

    }

    @Command
    public void editItem(@BindingParam("event") RequestDTO dto) {
        Executions.sendRedirect("/request/edit.zul?id=" + dto.getId());
    }
}
