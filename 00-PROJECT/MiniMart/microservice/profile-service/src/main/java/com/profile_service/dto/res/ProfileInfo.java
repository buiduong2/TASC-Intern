package com.profile_service.dto.res;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProfileInfo {
    private Long id;
    private String firstName;
    private String lastName;
    private String phone;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
