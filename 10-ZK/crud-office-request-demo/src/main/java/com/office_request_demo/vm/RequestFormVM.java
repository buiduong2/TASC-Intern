package com.office_request_demo.vm;

import java.util.List;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.QueryParam;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import com.office_request_demo.dto.RequestDTO;
import com.office_request_demo.mapper.RequestMapper;
import com.office_request_demo.services.RequestService;
import com.office_request_demo.ui.model.RequestForm;
import com.office_request_demo.ui.model.RequestSelectItem;
import com.office_request_demo.ui.service.RequestListSupport;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class RequestFormVM {

    @WireVariable
    private RequestListSupport requestListSupport;

    @WireVariable
    private RequestMapper requestMapper;

    @WireVariable
    private RequestService requestService;

    private RequestForm form;
    private List<RequestSelectItem> selectStatuses;
    private RequestSelectItem selectedStatus;
    private Long id;

    public boolean isEditMode() {
        return id != null;
    }

    @Init
    public void init(@QueryParam("id") Long id) {
        this.id = id;
        this.selectStatuses = requestListSupport.getSelectStatuses();

        if (id != null) {
            RequestDTO dto = requestService.findById(id);
            this.form = requestMapper.dtoToForm(dto);
        } else {
            this.form = new RequestForm();
        }

        this.selectedStatus = requestListSupport.getDefaultSelectStatus();
    }

    @Command
    public void onSelectStatus() {
        this.form.setStatus(this.selectedStatus.getValue());
    }

    @Command
    public void save() {

        RequestDTO dto = requestMapper.formToDto(form);

        if (id == null) {
            RequestDTO created = requestService.create(dto);
            this.id = created.getId();
        } else {
            dto.setId(id);
            requestService.update(dto);
        }

    }

    @Command
    public void cancel() {
        Executions.sendRedirect("/request/list.zul");
    }

    @Command
    public void saveAndClose() {
        RequestDTO dto = requestMapper.formToDto(form);

        if (id == null) {
            requestService.create(dto);
        } else {
            dto.setId(id);
            requestService.update(dto);
        }

        Executions.sendRedirect("/request/list.zul");
    }
}
