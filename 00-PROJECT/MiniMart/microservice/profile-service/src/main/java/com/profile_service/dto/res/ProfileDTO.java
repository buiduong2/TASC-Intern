package com.profile_service.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProfileDTO {
    private Long id;

    private Long userId;

    private String phone;

}
