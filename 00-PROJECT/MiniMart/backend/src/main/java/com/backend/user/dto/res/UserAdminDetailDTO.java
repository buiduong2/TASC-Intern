package com.backend.user.dto.res;

import java.time.LocalDateTime;
import java.util.List;

import com.backend.user.model.RoleName;
import com.backend.user.model.User;
import com.backend.user.model.UserStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAdminDetailDTO {
    private Long id;
    private String fullName;
    private String username;

    private String email;
    private UserStatus status;

    private List<RoleDTO> roles;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private User createdBy;
    private User updatedBy;

    @Getter
    @Setter
    public static class RoleDTO {
        private Long id;
        private RoleName name;
    }
}
