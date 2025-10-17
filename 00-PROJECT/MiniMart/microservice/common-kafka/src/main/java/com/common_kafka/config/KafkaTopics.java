package com.common_kafka.config;

public final class KafkaTopics {

    // Ngăn chặn việc khởi tạo class
    private KafkaTopics() {
        throw new UnsupportedOperationException("This is a constant class and cannot be instantiated.");
    }

    // ------------------------------------------------------------------------
    // TOPICS LIÊN QUAN ĐẾN SALE (ORDER & CART)
    // ------------------------------------------------------------------------

    /**
     * Topic cho các Event chính của Order Saga (ví dụ: OrderCreationRequestedEvent,
     * OrderReadyForPaymentEvent).
     */
    public static final String SALES_ORDER_EVENTS = "sales.order-events";

    public static final String SALES_ORDER_COMMAND = "sales.order-command";

    /**
     * Topic cho các Event liên quan đến giỏ hàng (ví dụ: CartUpdatedEvent).
     */
    public static final String SALES_CART_EVENTS = "sales.cart-events";

    // ------------------------------------------------------------------------
    // TOPICS LIÊN QUAN ĐẾN CATALOG & PRODUCT
    // ------------------------------------------------------------------------

    /**
     * Topic cho các Event của Product (ví dụ: ProductValidationPassedEvent,
     * ProductCreatedEvent).
     */
    public static final String CATALOG_PRODUCT_EVENTS = "catalog.product-events";

    // ------------------------------------------------------------------------
    // TOPICS LIÊN QUAN ĐẾN SUPPLY CHAIN (INVENTORY)
    // ------------------------------------------------------------------------

    /**
     * Topic cho các Event liên quan đến việc đặt trước hàng trong kho
     * (Reservation).
     */
    public static final String SUPPLY_INVENTORY_RESERVATION = "supply.inventory-reservation";

    /**
     * Topic cho các Event liên quan đến việc phân bổ/trừ kho chính thức
     * (Allocation/Debit).
     */
    public static final String SUPPLY_INVENTORY_ALLOCATION = "supply.inventory-allocation";

    // ------------------------------------------------------------------------
    // TOPICS LIÊN QUAN ĐẾN FINANCE & PAYMENT
    // ------------------------------------------------------------------------

    /**
     * Topic cho các Event liên quan đến thanh toán (ví dụ: PaymentSucceededEvent,
     * PaymentFailedEvent).
     */
    public static final String FINANCE_PAYMENT_EVENTS = "finance.payment-events";
}
