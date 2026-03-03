package com.office_request_demo.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.office_request_demo.entities.Status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class RequestDTO {

    private Long id; // Không validate ở đây

    private String title;

    private String reason;

    private LocalDate startDate;

    private LocalDate endDate;

    private Status status;

    private String createdBy;
    private LocalDateTime createdAt;

}