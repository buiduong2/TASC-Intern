package com.admin_bff.dto.res;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAuthDTO {
    private Long id;

    private Long userId;

    private String username;

    private String email;

    private String authSource;

    private String status;

    private LocalDateTime createdAt;
}
