## Vai trò của Order-Service trong Saga Creation

-   Khởi động Saga
-   Lưu trữ trạng thái Order (DB) và Saga tracker (bảng OrderSagaTracker).

-   Phát ra các sự kiện khởi đầu (`OrderCreationRequestedEvent`) và kế tiếp `OrderInitialPaymentRequestedEvent`

- Lắng nghe các sự kiện `ProductValidationPassedEvent` `InventoryReservedConfirmedEvent` , `PaymentSucceededEvent`

```
User → OrderService
   └── OrderCreationRequestedEvent
        ↓
ProductService → ProductValidationPassedEvent
InventoryService → InventoryReservedConfirmedEvent
        ↓
OrderService (khi đủ 2) → OrderInitialPaymentRequestedEvent
        ↓
PaymentService → PaymentRecordPreparedEvent → PaymentSucceededEvent
        ↓
OrderService → cập nhật Order = CONFIRMED / PAID
        ↓
(Shipping manual) → OrderCompletedEvent
        ↓
InventoryService → InventoryAllocationConfirmedEvent
        ↓
OrderService → cập nhật costPrice → mark StockFulfilled = SUCCESS
```