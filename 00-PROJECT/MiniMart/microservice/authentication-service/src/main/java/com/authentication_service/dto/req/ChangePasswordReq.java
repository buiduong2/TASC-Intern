package com.authentication_service.dto.req;

import org.hibernate.validator.constraints.Length;

import com.authentication_service.validation.PasswordConfirmable;
import com.authentication_service.validation.PasswordMatches;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@PasswordMatches
public class ChangePasswordReq implements PasswordConfirmable {

    @NotNull
    @Length(min = 8, max = 100)
    private String password;

    @NotNull
    @Length(min = 8, max = 100)
    private String confirmPassword;

    @NotNull
    @Length(min = 8, max = 100)
    private String oldPassword;
}
