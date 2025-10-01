package com.authentication_service.dto.res;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private long id;
    private String username;
    private String fullName;
    private List<String> roles;
}
