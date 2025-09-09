package com.backend.user.dto.req;

import org.hibernate.validator.constraints.Length;

import com.backend.user.validation.UserUniqueField;
import com.backend.user.validation.UserUniqueField.Column;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterReq implements PasswordConfirmable {

    @NotEmpty
    @UserUniqueField(column = Column.USERNAME)
    @Length(max = 100)
    private String username;

    @NotEmpty
    private String password;

    @NotEmpty
    private String confirmPassword;

    @NotEmpty
    @Email
    @UserUniqueField(column = Column.EMAIL)
    private String email;

    @NotEmpty
    @Length(max = 100)
    private String firstName;

    @NotEmpty
    @Length(max = 100)
    private String lastName;

    @NotEmpty
    @Length(max = 14)
    private String phone;
}
