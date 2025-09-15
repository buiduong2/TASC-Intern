package com.backend.user.dto.res;

import java.time.LocalDateTime;

import com.backend.user.model.UserStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAdminDTO {
    private Long id;
    private String fullName;
    private String username;
    private String email;
    private UserStatus status;
    private LocalDateTime createdAt;
}
