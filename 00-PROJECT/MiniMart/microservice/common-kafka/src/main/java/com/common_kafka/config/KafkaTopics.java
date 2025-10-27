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
     * Topic cho các Event thay đổi trạng thái chính thức của Order.
     * Events: OrderCanceledEvent, OrderCompletedEvent, OrderReadyForShipmentEvent.
     * Publisher: OrderService.
     * Consumer: Shipping/Notification Service, Analytic Service, v.v.
     */
    public static final String SALES_ORDER_EVENTS = "sales.order-events";

    /**
     * Topic cho các Command/Request khởi tạo Order Saga.
     * Events: OrderCreationRequestedEvent (Command từ API/Frontend).
     * Publisher: Client/API Gateway.
     * Consumer: OrderService (để bắt đầu Saga).
     */
    public static final String SALES_ORDER_INIT_COMMANDS = "sales.order-init-commands";

    /**
     * Topic cho các Command/Request khởi tạo luồng Hủy Order.
     * Events: OrderCancellationRequestedEvent (Command từ API/Frontend).
     * Publisher: Client/API Gateway.
     * Consumer: OrderService (để bắt đầu Compensation Saga hủy).
     */
    public static final String SALES_ORDER_CANCEL_COMMANDS = "sales.order-cancel-commands";

    /**
     * Topic chứa các Event được phát ra khi Order Saga THẤT BẠI hoặc bị HỦY, yêu
     * cầu các dịch vụ hoàn tác.
     * Events: OrderCreationCompensatedEvent (hoặc
     * OrderCancellationRequestedEvent/FailedEvent tùy luồng).
     * Publisher: OrderService.
     * Consumer: InventoryService (giải phóng kho), PaymentService (hủy ghi nhận
     * thanh toán).
     */
    public static final String SALES_ORDER_COMPENSATION = "sales.order-compensation";

    /**
     * Topic cho các Event liên quan đến giỏ hàng.
     * Events: (Không có trong danh sách file, ví dụ: CartUpdatedEvent).
     * Publisher: Cart Service.
     * Consumer: OrderService (để tạo Order), Analytic Service.
     */
    public static final String SALES_CART_EVENTS = "sales.cart-events";

    // ------------------------------------------------------------------------
    // TOPICS LIÊN QUAN ĐẾN CATALOG & PRODUCT
    // ------------------------------------------------------------------------

    /**
     * Topic dành riêng cho yêu cầu và kết quả kiểm tra/xác thực Product trong luồng
     * Order Saga.
     * Events: OrderCreationRequestedEvent (yêu cầu), ProductValidationPassedEvent
     * (kết quả), ProductValidationFailedEvent (kết quả).
     * Publisher: OrderService (yêu cầu), ProductService (kết quả).
     * Consumer: ProductService (lắng nghe yêu cầu), OrderService (lắng nghe kết
     * quả).
     */
    public static final String CATALOG_PRODUCT_VALIDATION = "catalog.product-validation";

    /**
     * Topic cho các Event thay đổi Master Data của Product.
     * Events: (Không có trong danh sách file, ví dụ: ProductCreatedEvent,
     * ProductPriceUpdatedEvent).
     * Publisher: ProductService.
     * Consumer: Catalog Service, Search Service, v.v.
     */
    public static final String CATALOG_PRODUCT_EVENTS = "catalog.product-events";

    // ------------------------------------------------------------------------
    // TOPICS LIÊN QUAN ĐẾN SUPPLY CHAIN (INVENTORY)
    // ------------------------------------------------------------------------

    /**
     * Topic cho các Event liên quan đến việc đặt trước hàng trong kho (Reservation)
     * và kết quả.
     * Events: ProductValidationPassedEvent (yêu cầu),
     * InventoryReservedConfirmedEvent (kết quả), InventoryReservationFailedEvent
     * (kết quả).
     * Publisher: ProductService (yêu cầu giữ chỗ), InventoryService (kết quả).
     * Consumer: InventoryService (lắng nghe yêu cầu giữ chỗ), OrderService (lắng
     * nghe kết quả).
     */
    public static final String SUPPLY_INVENTORY_RESERVATION = "supply.inventory-reservation";

    /**
     * Topic chung cho tất cả giao tiếp liên quan đến Phân bổ/Trừ kho chính thức
     * (Allocation).
     * Events: OrderStockAllocationRequestedEvent (yêu cầu),
     * InventoryAllocationConfirmedEvent (kết quả).
     * Publisher: OrderService (yêu cầu), InventoryService (kết quả).
     * Consumer: InventoryService (lắng nghe yêu cầu phân bổ), OrderService (lắng
     * nghe kết quả).
     */
    public static final String SUPPLY_INVENTORY_ALLOCATION = "supply.inventory-allocation";

    /**
     * Topic dành riêng cho các Event bù trừ (Compensation) của Inventory.
     * Events: OrderCancellationRequestedEvent (yêu cầu),
     * OrderCreationCompensatedEvent (yêu cầu).
     * Publisher: OrderService.
     * Consumer: InventoryService (chỉ lắng nghe yêu cầu hoàn tác/giải phóng kho).
     */
    public static final String SUPPLY_INVENTORY_COMPENSATION = "supply.inventory-compensation";

    // ------------------------------------------------------------------------
    // TOPICS LIÊN QUAN ĐẾN FINANCE & PAYMENT
    // ------------------------------------------------------------------------

    /**
     * Topic ĐỘC QUYỀN cho việc khởi tạo Payment Record.
     * Chứa Request và Response trực tiếp để tiếp tục Order Saga.
     * Events: OrderInitialPaymentRequestedEvent (yêu cầu),
     * PaymentRecordPreparedEvent (phản hồi).
     * Publisher: OrderService (yêu cầu), PaymentService (phản hồi).
     * Consumer: PaymentService (lắng nghe yêu cầu), OrderService (lắng nghe phản
     * hồi).
     */
    public static final String FINANCE_PAYMENT_REQUEST = "finance.payment-request";

    /**
     * Topic cho TẤT CẢ các Event trạng thái và luồng dữ liệu khác của Domain
     * Payment.
     * Events: PaymentSucceededEvent, PaymentFailedEvent.
     * Publisher: PaymentService.
     * Consumer: OrderService (xử lý kết quả cuối cùng/lỗi), Fraud Service,
     * Reporting Service, v.v.
     */
    public static final String FINANCE_PAYMENT_EVENTS = "finance.payment-events";
}