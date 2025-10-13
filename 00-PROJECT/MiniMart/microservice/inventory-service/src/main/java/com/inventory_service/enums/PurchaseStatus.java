package com.inventory_service.enums;

public enum PurchaseStatus {
    DRAFT, // Chưa hoàn thiện - Không nhìn thấy bởi người dùng + service
    ACTIVE, // Hoạt động
    INACTIVE, // Không còn nhìn thấy bởi người dùng cuối
    ARCHIVED, // Không tương tác với nghiệp vụ các service
    PENDING_DELETION; // Chuẩn bị xóa
}
