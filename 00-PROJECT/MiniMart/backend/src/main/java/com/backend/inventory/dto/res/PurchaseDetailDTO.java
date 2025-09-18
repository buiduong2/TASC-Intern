package com.backend.inventory.dto.res;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PurchaseDetailDTO {

    private long id;

    private String supplier;

    private int totalQuantity;

    private double totalCostPrice;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private UserDTO createdBy;

    private UserDTO updatedBy;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class UserDTO {
        private Long id;

        private String fullName;
    }
}
