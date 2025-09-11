package com.backend.user.dto.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordReq implements PasswordConfirmable {

    private String password;

    private String confirmPassword;

    private String oldPassword;
}
