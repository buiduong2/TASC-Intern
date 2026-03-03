package com.office_request_demo.mapper;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.office_request_demo.dto.RequestDTO;
import com.office_request_demo.entities.Request;
import com.office_request_demo.entities.Status;
import com.office_request_demo.ui.model.RequestForm;

@Component
public class RequestMapper {

    // ==========================
    // FORM <-> DTO
    // ==========================

    public RequestForm dtoToForm(RequestDTO dto) {
        if (dto == null)
            return null;

        return RequestForm.builder()
                .title(dto.getTitle())
                .reason(dto.getReason())
                .startDate(toDate(dto.getStartDate()))
                .endDate(toDate(dto.getEndDate()))
                .status(dto.getStatus() != null ? dto.getStatus().name() : null)
                .build();
    }

    public RequestDTO formToDto(RequestForm form) {
        if (form == null)
            return null;

        return RequestDTO.builder()
                .title(form.getTitle())
                .reason(form.getReason())
                .startDate(toLocalDate(form.getStartDate()))
                .endDate(toLocalDate(form.getEndDate()))
                .status(form.getStatus() != null
                        ? Status.valueOf(form.getStatus())
                        : Status.PENDING)
                .build();
    }

    // ==========================
    // DTO <-> ENTITY
    // ==========================

    public RequestDTO entityToDto(Request entity) {
        if (entity == null)
            return null;

        return RequestDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .reason(entity.getReason())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .status(entity.getStatus())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public Request dtoToEntity(RequestDTO dto) {
        if (dto == null)
            return null;

        return Request.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .reason(dto.getReason())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .status(dto.getStatus() != null ? dto.getStatus() : Status.PENDING)
                .createdBy(dto.getCreatedBy())
                .createdAt(dto.getCreatedAt())
                .build();
    }

    // ==========================
    // UPDATE MERGE (IMPORTANT)
    // ==========================

    public void updateEntityFromDto(RequestDTO dto, Request entity) {
        if (dto == null || entity == null)
            return;

        entity.setTitle(dto.getTitle());
        entity.setReason(dto.getReason());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setStatus(dto.getStatus());
    }

    // ==========================
    // DATE CONVERSION
    // ==========================

    private Date toDate(LocalDate localDate) {
        if (localDate == null)
            return null;
        return Date.from(localDate
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant());
    }

    private LocalDate toLocalDate(Date date) {
        if (date == null)
            return null;
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}