package com.backend.user.security;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.backend.user.model.Role;
import com.backend.user.model.RoleName;
import com.backend.user.model.User;

import lombok.Getter;

@Getter
public class CustomUserDetail implements UserDetails {

    private long userId;

    private Set<SimpleGrantedAuthority> authorities;

    private String password;

    private String username;

    private boolean active;

    private long tokenVersion;

    public CustomUserDetail(User user) {
        this.password = user.getPassword();
        this.username = user.getUsername();
        this.userId = user.getId();
        this.authorities = user.getRoles()
                .stream()
                .map(Role::getName)
                .map(RoleName::toString)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        this.active = user.isActive();
        this.tokenVersion = user.getTokenVersion();

    }

    public CustomUserDetail(long userId, List<String> roles, String username, long tokenVersion) {
        this.userId = userId;
        this.authorities = roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
        this.username = username;
        this.tokenVersion = tokenVersion;
        this.active = true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.active;
    }

    @Override
    public boolean isEnabled() {
        return this.active;
    }

}
