package com.backend.user.dto.res;

import com.backend.user.model.RoleName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RoleAdminDTO {

    private Long id;
    private RoleName name;
    private String description;
}
