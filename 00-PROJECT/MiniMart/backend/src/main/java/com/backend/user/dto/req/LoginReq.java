package com.backend.user.dto.req;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginReq {

    @NotBlank
    @Length(min = 4, max = 100)
    private String username;

    @NotBlank
    @Length(min = 8, max = 100)
    private String password;
}
