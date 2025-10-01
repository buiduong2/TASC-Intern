package com.profile_service.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressCreateReq {

    @NotBlank
    private String details;

    @NotBlank
    private String city;

    @NotBlank
    private String area;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Pattern(regexp = "^[0-9]{9,12}$")
    private String phone;
}