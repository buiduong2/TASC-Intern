package com.backend.user.dto.req;

import java.util.List;

import com.backend.user.model.UserStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserAdminReq {
    private String fullName;
    private String email;
    private UserStatus status;
    private List<Long> roleIds;
}
