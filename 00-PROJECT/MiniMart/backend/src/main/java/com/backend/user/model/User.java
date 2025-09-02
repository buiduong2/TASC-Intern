package com.backend.user.model;

import java.util.List;

import com.backend.common.model.Audit;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(columnNames = "email") })
public class User {

    @Id
    @GeneratedValue
    private Long id;

    private String fullName;

    private String username;

    private String password;

    private String email;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @OneToOne(mappedBy = "user")
    private Customer customer;

    @ManyToMany
    private List<Role> roles;

    @Embedded
    private Audit audit;
}
