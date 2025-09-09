package com.backend.user.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.backend.common.model.Audit;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    private Long id;

    private String fullName;

    @Column(updatable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(updatable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @OneToOne(mappedBy = "user")
    private Customer customer;

    @ManyToMany
    private List<Role> roles;

    private boolean active = true;

    private long tokenVersion;

    @Embedded
    private Audit audit;

    public boolean hasAnyRole(RoleName... roles) {
        Set<RoleName> roleNames = new HashSet<>(Arrays.asList(roles));
        return this.roles.stream().anyMatch(r -> roleNames.contains(r.getName()));
    }

}
