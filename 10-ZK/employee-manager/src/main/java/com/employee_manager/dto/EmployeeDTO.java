package com.employee_manager.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class EmployeeDTO {

    private Long id;
    private String fullName;
    private String job;
    private Boolean gender;
    private Instant birthDate;
    private String avatarSrc;
}