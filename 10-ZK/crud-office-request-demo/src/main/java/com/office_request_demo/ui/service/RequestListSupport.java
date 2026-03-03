package com.office_request_demo.ui.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.office_request_demo.entities.Status;
import com.office_request_demo.ui.model.RequestSelectItem;

import lombok.Getter;

@Component
public class RequestListSupport {

    @Getter
    private List<RequestSelectItem> selectStatuses = List.of(
            RequestSelectItem.builder().label("-- Chọn Status --").value(null).build(),
            RequestSelectItem.builder().label(Status.APPROVED.name()).value(Status.APPROVED.name()).build(),
            RequestSelectItem.builder().label(Status.REJECTED.name()).value(Status.REJECTED.name()).build(),
            RequestSelectItem.builder().label(Status.PENDING.name()).value(Status.PENDING.name()).build());

    public RequestSelectItem getDefaultSelectStatus() {
        return selectStatuses.get(0);
    }
}
