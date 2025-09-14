package com.backend.user.dto.req;

import java.util.Set;

import org.hibernate.validator.constraints.Length;

import com.backend.common.validation.EnumValue;
import com.backend.user.model.UserStatus;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserAdminReq {

    @NotNull
    @Length(min = 4, max = 30)
    private String fullName;

    @Length(min = 4, max = 30)
    private String email;

    @EnumValue(enumClass = UserStatus.class)
    private String status;

    @NotEmpty
    private Set<Long> roleIds;
}
