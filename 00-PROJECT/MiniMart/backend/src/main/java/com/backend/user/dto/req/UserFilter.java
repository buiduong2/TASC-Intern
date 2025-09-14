package com.backend.user.dto.req;

import java.time.LocalDateTime;
import java.util.List;

import com.backend.common.validation.EnumValue;
import com.backend.user.model.RoleName;
import com.backend.user.model.UserStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserFilter {

    private List<@EnumValue(enumClass = UserStatus.class) String> status;

    private List<@EnumValue(enumClass = RoleName.class) String> roleName;

    private String keyword;

    private LocalDateTime createdDateFrom;

    private LocalDateTime createdDateTo;
}
