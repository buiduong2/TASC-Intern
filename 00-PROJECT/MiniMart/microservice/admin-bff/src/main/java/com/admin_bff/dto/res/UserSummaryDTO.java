package com.admin_bff.dto.res;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSummaryDTO {

    private long userId;
    private String username;
    private String email;
    private String authSource;
    private String phone;
    private String status;

    private long orderCount;

    private LocalDateTime createdAt;
}
