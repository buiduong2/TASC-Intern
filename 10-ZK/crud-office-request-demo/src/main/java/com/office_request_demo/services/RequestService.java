package com.office_request_demo.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Tuple;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.office_request_demo.dto.RequestDTO;
import com.office_request_demo.dto.RequestOverviewDTO;
import com.office_request_demo.entities.Request;
import com.office_request_demo.entities.Status;
import com.office_request_demo.mapper.RequestMapper;
import com.office_request_demo.repositories.RequestRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;

    private final RequestMapper requestMapper;

    public RequestOverviewDTO getOverview() {

        long total = requestRepository.count();

        List<Tuple> rows = requestRepository.countGroupByStatus();

        RequestOverviewDTO dto = new RequestOverviewDTO();
        dto.setTotalRequests(total);

        List<RequestOverviewDTO.RequestMetricDTO> metrics = new ArrayList<>();

        for (Tuple row : rows) {

            String statusStr = row.get("status", String.class);
            Long count = ((Number) row.get("count")).longValue();

            Status status = Status.valueOf(statusStr);

            double rate = 0;
            if (total > 0) {
                rate = (double) count / total * 100;
            }

            RequestOverviewDTO.RequestMetricDTO metric = new RequestOverviewDTO.RequestMetricDTO();

            metric.setStatus(status);
            metric.setCount(count);
            metric.setRate(rate);
            metric.setFormattedRate(String.format("%.2f%%", rate));

            metrics.add(metric);
        }

        dto.setMetrics(metrics);

        return dto;
    }

    /* ================= CREATE ================= */

    private void validateBusiness(RequestDTO dto) {

        if (dto.getStartDate() != null &&
                dto.getEndDate() != null &&
                dto.getEndDate().isBefore(dto.getStartDate())) {

            throw new IllegalArgumentException("End date must be after or equal to start date");
        }
    }

    @Transactional
    public RequestDTO create(RequestDTO dto) {

        validateBusiness(dto);

        Request request = requestMapper.dtoToEntity(dto);
        request.setCreatedAt(LocalDateTime.now());

        return requestMapper.entityToDto(requestRepository.save(request));
    }

    /* ================= READ ALL ================= */
    public List<RequestDTO> findAll() {
        return this.requestRepository.findAll().stream().map(requestMapper::entityToDto).toList();
    }

    /* ================= READ BY ID ================= */
    public RequestDTO findById(long id) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        return requestMapper.entityToDto(request);
    }

    /* ================= UPDATE ================= */
    @Transactional
    public RequestDTO update(RequestDTO dto) {

        Request existing = requestRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Request not found"));

        existing.setTitle(dto.getTitle());
        existing.setReason(dto.getReason());
        existing.setStartDate(dto.getStartDate());
        existing.setEndDate(dto.getEndDate());
        existing.setStatus(dto.getStatus());

        return requestMapper.entityToDto(existing);
    }

    /* ================= DELETE ================= */

    @Transactional
    public void delete(long id) {
        requestRepository.deleteById(id);
    }

    @Transactional
    public void delete(List<Long> ids) {
        requestRepository.deleteAllById(ids);
    }

}