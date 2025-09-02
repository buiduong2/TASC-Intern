package com.backend.user.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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

    @OneToOne(mappedBy = "user")
    private Customer customer;

    @OneToMany
    private List<Role> roles;
}
