package com.backend.user.dto.res;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRes {

    private String accessToken;

    private String refreshToken;

    private UserDTO user;

    @Getter
    @Setter
    public static class UserDTO {
        private long id;
        private String username;
        private String fullName;
        private List<String> roles;
    }
}
