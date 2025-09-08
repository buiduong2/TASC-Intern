package com.backend.order.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderAddressReq {

    @NotEmpty
    private String firstName;

    @NotEmpty
    private String lastName;

    @Email
    @NotEmpty
    private String email;

    @NotEmpty
    private String phone;

    @NotEmpty
    private String details;

    @NotEmpty
    private String city;

    @NotEmpty
    private String area;
}
