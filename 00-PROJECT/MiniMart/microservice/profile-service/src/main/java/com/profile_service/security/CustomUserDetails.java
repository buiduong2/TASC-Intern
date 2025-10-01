package com.profile_service.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomUserDetails implements UserDetails {
    private Collection<? extends GrantedAuthority> authorities;
    private Long id;

    public CustomUserDetails(String id, String roles) {
        this.id = Long.parseLong(id);
        this.authorities = Arrays.stream(roles.split(","))
                .map(role -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toSet());

    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return String.valueOf(id);
    }

}
