## Vai trò của Order-Service trong Saga Creation

-   Khởi động Saga
-   Lưu trữ trạng thái Order (DB) và Saga tracker (bảng OrderSagaTracker).

-   Phát ra các sự kiện khởi đầu (`OrderCreationRequestedEvent`) và kế tiếp `OrderInitialPaymentRequestedEvent`

- Lắng nghe các sự kiện `ProductValidationPassedEvent` `InventoryReservedConfirmedEvent` , `PaymentSucceededEvent`

```
┌──────────────────────────────────────────────────────────────────────┐
│                         ORDER SAGA FLOW (HAPPY PATH)                 │
└──────────────────────────────────────────────────────────────────────┘

[1️⃣ OrderService]
  ─ publish OrderCreationRequestedEvent
      ▼
[2️⃣ ProductService]
  ─ validate product → publish ProductValidationPassedEvent
      ▼
[3️⃣ InventoryService]
  ─ reserve stock → publish InventoryReservedConfirmedEvent
      ▼
[4️⃣ OrderService]
  ─ both product+inventory ready → publish OrderInitialPaymentRequestedEvent
      ▼
[5️⃣ PaymentService]
  ─ create payment record → publish PaymentRecordPreparedEvent
      ▼
[6️⃣ OrderService]
  ─ payment record prepared → publish OrderStockAllocationRequestedEvent
      ▼
[7️⃣ InventoryService]
  ─ allocate actual stock → publish InventoryAllocationConfirmedEvent
      ▼
[8️⃣ OrderService]
  ─ mark order as STOCK_FULFILLED
  ─ optionally → publish OrderReadyForShipmentEvent

```