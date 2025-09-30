package com.authentication_service.security;

import com.authentication_service.model.SystemUser;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class SystemUserDetails extends CustomUserDetails {

    private String password;
    private String username;

    public SystemUserDetails(SystemUser user) {
        super(user);
        this.password = user.getPassword();
        this.username = user.getUsername();
    }

}
