package com.authentication_service.dto.res;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAuthSummary {
    private Long id;

    private Long userId;

    private String username;

    private String email;

    private String authSource;

    private String status;

    private LocalDateTime createdAt;
}
