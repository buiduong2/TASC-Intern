package com.profile_service.dto.res;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddressInfo {
    private Long id;
    private String details;
    private String city;
    private String area;
    private String firstName;
    private String lastName;
    private String phone;
    private Long profileId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
