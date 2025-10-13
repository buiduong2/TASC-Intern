package com.product_service.enums;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum ProductStatus {
    DRAFT, // Chưa hoàn thiện - Không nhìn thấy bởi người dùng + service
    ACTIVE, // Hoạt động
    INACTIVE, // Không còn nhìn thấy bởi người dùng cuối
    ARCHIVED, // Không tương tác với nghiệp vụ các service
    PENDING_DELETION; // Chuẩn bị xóa

    private static final Set<ProductStatus> adminEditableStatuses = Arrays.stream(ProductStatus.values())
            .collect(Collectors.toSet());
    private static final Set<ProductStatus> transactableStatuses = Set.of(ACTIVE, INACTIVE);
    private static final Set<ProductStatus> publiclyVisibleStatuses = Set.of(ACTIVE);

    public static Set<ProductStatus> getPubliclyVisibleStatuses() {
        return publiclyVisibleStatuses;
    }

    public static Set<ProductStatus> getTransactableStatuses() {
        return transactableStatuses;
    }

    public static Set<ProductStatus> getAdminEditableStatuses() {
        return adminEditableStatuses;
    }
}
