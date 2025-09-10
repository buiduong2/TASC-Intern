package com.backend.user.dto.req;

import org.hibernate.validator.constraints.Length;

import com.backend.user.validation.PasswordMatches;
import com.backend.user.validation.UserUniqueField;
import com.backend.user.validation.UserUniqueField.Column;
import com.backend.user.validation.ValidPhone;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@PasswordMatches
public class RegisterReq implements PasswordConfirmable {

    @NotEmpty
    @UserUniqueField(column = Column.USERNAME, message = "Username already exists")
    @Length(min = 5, max = 100)
    private String username;

    @NotEmpty
    @Length(min = 8, max = 100)
    private String password;

    @NotEmpty
    @Length(min = 8, max = 100)
    private String confirmPassword;

    @NotEmpty
    @Email
    @UserUniqueField(column = Column.EMAIL, message = "Email already exists")
    private String email;

    @NotEmpty
    @Length(max = 100)
    private String firstName;

    @NotEmpty
    @Length(max = 100)
    private String lastName;

    @NotEmpty
    @ValidPhone
    private String phone;
}
