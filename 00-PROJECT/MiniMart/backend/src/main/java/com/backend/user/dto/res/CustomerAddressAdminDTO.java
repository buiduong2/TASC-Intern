package com.backend.user.dto.res;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerAddressAdminDTO {
    private Long id;
    

    private String details;
    private String city;
    private String area;
}
