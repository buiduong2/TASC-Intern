package com.office_request_demo.dto;

import java.time.LocalDate;
import java.util.List;

import com.office_request_demo.entities.Status;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestOverviewDTO {
    private long totalRequests;

    private List<RequestMetricDTO> metrics;

    private LocalDate fromDate;
    private LocalDate toDate;

    @Getter
    @Setter
    public static class RequestMetricDTO {

        private Status status;
        private long count;
        private double rate;
        private String formattedRate;
    }
}
