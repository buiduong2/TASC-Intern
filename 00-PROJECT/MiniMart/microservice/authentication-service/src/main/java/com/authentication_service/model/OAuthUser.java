package com.authentication_service.model;

import com.authentication_service.enums.OAUthProvider;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@DiscriminatorValue("OAUTH")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "providerUserId", "provider" }))
public class OAuthUser extends User {

    private String providerUserId;

    private String username;

    private String email;

    @Enumerated(EnumType.STRING)
    private OAUthProvider provider;
}
