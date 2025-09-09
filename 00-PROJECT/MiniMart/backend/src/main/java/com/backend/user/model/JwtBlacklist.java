package com.backend.user.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class JwtBlacklist {
    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    private TokenType type;

    private LocalDateTime expiredAt;

    private LocalDateTime createdAt;
}
