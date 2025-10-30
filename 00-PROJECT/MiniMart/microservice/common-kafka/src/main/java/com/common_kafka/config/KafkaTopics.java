package com.common_kafka.config;

public final class KafkaTopics {

    // Ngăn chặn việc khởi tạo class
    private KafkaTopics() {
        throw new UnsupportedOperationException("This is a constant class and cannot be instantiated.");
    }

    // ================= SALES =================
    public static final String SALES_ORDER_EVENTS = "sales.order-events";

    public static final String SALES_CART_EVENTS = "sales.cart-events";

    // ================= CATALOG =================
    public static final String CATALOG_PRODUCT_VALIDATION = "catalog.product-validation";

    public static final String CATALOG_PRODUCT_VALIDATION_EVENTS = "catalog.product-validation-events";

    // ================= SUPPLY =================
    public static final String SUPPLY_INVENTORY_RESERVATION = "supply.inventory-reservation";

    public static final String SUPPLY_INVENTORY_ALLOCATION = "supply.inventory-allocation";

    public static final String SUPPLY_INVENTORY_RESERVATION_EVENTS = "supply.inventory-reservation-events";

    public static final String SUPPLY_INVENTORY_ALLOCATION_EVENTS = "supply.inventory-allocation-events";

    // ================= FINANCE =================
    public static final String FINANCE_PAYMENT_COMMAND = "finance.payment-command";

    public static final String FINANCE_PAYMENT_EVENTS = "finance.payment-events";
}