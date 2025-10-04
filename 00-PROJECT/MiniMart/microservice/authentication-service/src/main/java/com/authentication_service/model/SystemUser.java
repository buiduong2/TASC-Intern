package com.authentication_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@DiscriminatorValue(SystemUser.AUTH_SOURCE)
@Table(name = "system_users")
public class SystemUser extends User {

    public static final String AUTH_SOURCE = "SYS";

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    private long tokenVersion;
}
