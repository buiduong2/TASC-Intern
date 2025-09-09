package com.backend.user.dto.req;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginReq {
    
    @NotEmpty
    @Length(min = 8, max = 100)
    private String username;

    @NotEmpty
    @Length(min = 8, max = 100)
    private String password;
}
