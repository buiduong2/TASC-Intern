## Console

```bash
kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic order.status.created \
  --from-beginning \
  --property print.key=true \
  --property print.headers=true \
  --property print.partition=true \
  --property print.timestamp=true

docker exec -it kafka  kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic order.status.created --from-beginning
docker exec -it kafka kafka-topics.sh --bootstrap-server localhost:9092 --delete --topic order.status.created
docker exec -it kafka kafka-topics.sh --bootstrap-server localhost:9092 --create --topic order.status.created --partitions 1 --replication-factor 1







```

## Orrder

-   Update : OrderUpvent

-   Status: OrderCreatedEvent

-   OrderDeleteEvent

-   ReportService:

    -   `OrderCompltedEvent` : cập số tiền

    -   OrderUpvent -> lắng nghe
    -   OrderCreatedEvent -> nghe

-   Analichtic....
    -> người hàng `OrderCompltedEvent`
    -> `OrderCreatedEvent`

-

## Tên Channel

-   Key: tất cả đều là `orderId`

-   `sales.order-events`

-   `sales.cart-events`

-   `catalog.product-events`

-   `supply.inventory-reservation`

-   `supply.inventory-allocation`

-   `finance.payment-events`

## Tên Event

-   `OrderCreationRequestedEvent` -> PENDING

-   `ProductValidationPassedEvent` -> snapshot unitPrice

-   `InventoryReservedConfirmedEvent` -> Xác nhạn đặt trước Stock thành công

-   `OrderInitialPaymentRequestedEvent` -> Order yêu cầu khởi tạo Payment

-   `PaymentRecordPreparedEvent` -> Xác nhận bản ghi cho thanh toán đã được tạo

-   `PaymentSucceededEvent` -> Thanh toán thành công

-   `OrderReadyForShipmentEvent` ->

-   `OrderCompletedEvent` -> Đơn hàng hoàn tất (thay đổi Status thủ công sau giao hàng)

-   `InventoryAllocationConfirmedEvent` -> Sản phẩm đã được phân phối (giảm Stock thực tế).

-   `OrderCanceledEvent` -> Bù trừ/Rollback: Event chung khi Order bị hủy do người dùng hoặc do lỗi Saga

-   Event compenstate

-   `ProductValidationFailedEvent` -> Thất bại: Sản phẩm không hợp lệ, ngừng Saga.

-   `InventoryReservationFailedEvent`-> Thất bại: Stock không đủ hoặc lỗi đặt trước, ngừng Saga.

-   `PaymentFailedEvent` -> Thất bại: Thanh toán không thành công, ngừng Saga. (Hoặc nếu đã CONFIRM, chuyển sang quy trình xử lý lỗi).

## MÔ tả sơ

-   Việc Shipping là thủ công hệ thống ko có logic gì cả

-   Stock luôn đảm bảo số lượng luôn nhỏ hơn (độ trễ) hoặc = Số lượng có thật SUM(activePurchaseItem.remainingQuantity)

-   thanh toán có thể đến bất kì lúc nào miễn là trước khi COMPLETED Order (nhân viên quản lý có thể đợi thanh toán nếu là CARD trước khi thực sự đặt SHIP)

-   Việc compenstation của Order sẽ bắn ra và các Service liên quan sẽ có độ ưu tiên cho việc bù trừ `InventoryService -> `

## Luongf nghiệp vụ

-   Các bên tham gia. OrderService, PaymentService, ProductService, InventoryService

### Happy Path

-   Luồng khi khởi tạo Order Happy Path

    -   B1: OrderService: (Order) Tạo Order `PREPARING` -> bắn ra sự kiện `OrderCreationRequestedEvent` -> mang theo các OrderItemReq (quantity, productId)
    -   B2: ProductService: lắng nghe (`OrderCreationRequestedEvent`) -> Tiến hành Kiểm tra các sản phẩm có tồn tại và Đang Active hay không.  
         -> thành công: bắn ra sự kiện `ProductValidationPassedEvent` -> mang theo thông tin về (productId, unitPrice, quantity) các sản phẩm
        -> Thất bại: Bắn ra sự kiện `ProductValidationFailedEvent`

    -   B3: (`ProductValidationPassedEvent`)
        B3.1: - OrderService: Lắng nghe (`ProductValidationPassedEvent`) -> trích xuất ra UnitPrice (cập nhật các OrderItem unit Price) . Tính toán ra giá total Order = total orderItem Price + shipping Price - -> thành công (bắn ra sự kiện kafka để đảm bảo độ tin cậy). Kiểm tra xem đã đủ trạng thái (đã lắng nghe sự kiện `InventoryReservedConfirmedEvent` chưa để bắt đầu tiếp nếu ko dừng lại)

        B3.2: - InventoryService: Lắng nghe (`ProductValidationPassedEvent`) -> trích xuất (productd, quantity) -> Kiểm tra các đơn hàng trong kho có đủ stock hay không. Tiến hành đặt trước các sản phẩm Thay đổi dữ liệu entity Stock (reversedQuantity) - Thành công: bắn ra sự kiện: `StockProvisionalReservedEvent`

    -   B4: Sau khi `UnitPriceConfirmedEvent` + `StockProvisionalReservedEvent`

        -   OrderService: Tiến hành bắn ra sự kiện tiếp theo `InitialPaymentEvent`

    -   B5: PaymentService lắng nghe `InitialPaymentEvent` -> tiến hành tạo ra một bản ghi Payment (chứa thông tin số tiền cần thanh toán, Và phương thức thanh toán (chỉ là metadata))

        -   -> Thành công : bắn ra sự kiện (`PaymentPreparedEvent`)

    -   B6: OrderService lắng nghe và thay đổi Status thành `CONFIRM` (Lúc này Order đã sẵn sàng với các logic nghiệp vụ khác thao tác khác)

-   Khi Order trở thành COMPLETED (thủ công thay đổi sau khi giao hàng thành công)

    -   B1: OrderService chuyển Order sang trạng thái `COMPLETED` -> bắn ra sự kiện `ORderCompletedEvent`
    -   B2: inventoryService (lắng nghe) `OrderCompletedEvent` -> tiến hành phân phối các sản phẩm trong kho FIFO `PurchaseItem -> Allocation -> productId+ quantity` -> Bắn ra sự kiện `StockAllocationConfirmedEvent` chứa thông tin về (productId + costPrice + quantity tương ứng)

    -   B3: OrderService lắng nghe : `StockAllocationConfirmedEvent` triết xuất thông tin và tính toán ra avgCostPrice của OrderItem và Order tổng

-   Khi Payment thanh toán thành công:

    -   B1; Payment, thay đổi payment bản thân thành `PAID ` bắn ra sự kiện `PaymentSucceededEvent`
    -   B2: orderService lắng nghe và thay đổi Order.paymentStatus = PAID

#### Unhappy Path

-   Khi người dùng vừa tạo Order xong tiến hành hủy luôn

-   Admin tiến hành hủy Order vì lý do ngoại quan

-   Bước VAlidationProduct thất bại

-   StockProvisionalReservedEvent thất bại

## Saga Order

```
┌─────────────────────────────── Saga Order Flow ───────────────────────────────┐

User
 │
 │ 1️⃣  Create Order
 ▼
OrderService
 ├─ Tạo Order, OrderItems, OrderSagaProgress(status=PENDING)
 ├─ Gửi event →  [OrderCreatedEvent]
 ▼
Kafka
 └─ Phát tán OrderCreatedEvent

 ┌──────────────────────────────────────────────────────────────────────────────┐
 │                              PHASE 1: PRODUCT CHECK                          │
 └──────────────────────────────────────────────────────────────────────────────┘

ProductService
 ├─ Nhận OrderCreatedEvent
 ├─ Kiểm tra Product hợp lệ?
 │    ├─ ❌ Không hợp lệ → gửi [ProductValidationFailedEvent]
 │    └─ ✅ Hợp lệ → gửi [ProductValidationPassedEvent]

Kafka
 ├─ Phát ProductValidationFailedEvent → OrderService rollback ngay
 └─ Phát ProductValidationPassedEvent → Cho 2 service cùng lắng nghe

 ┌──────────────────────────────────────────────────────────────────────────────┐
 │                      PHASE 2: PARALLEL VALIDATION                            │
 └──────────────────────────────────────────────────────────────────────────────┘

 ┌──────────────┐                     ┌────────────────┐
 │ OrderService │                     │ InventoryService │
 ├──────────────┤                     ├────────────────┤
 │ Nhận ProductValidationPassedEvent │ Nhận ProductValidationPassedEvent
 │ → Bắt đầu ghi unitPrice            │ → Validate và Reserve Stock
 │   (unitPriceConfirmed=PENDING)     │   (stockReserved=PENDING)
 │ → Thành công →                     │ → Thành công →
 │   update unitPriceConfirmed=SUCCESS│   update stockReserved=SUCCESS
 │ → Thất bại →                       │ → Thất bại →
 │   update unitPriceConfirmed=FAILED │   update stockReserved=FAILED
 │   + bắn sự kiện rollback           │   + bắn sự kiện rollback
 └──────────────┘                     └────────────────┘

 ┌──────────────────────────────────────────────────────────────────────────────┐
 │                    PHASE 3: COORDINATION & PAYMENT                           │
 └──────────────────────────────────────────────────────────────────────────────┘

OrderService
 ├─ Theo dõi OrderSagaProgress
 ├─ Khi cả hai step (unitPrice, stock) đều SUCCESS →
 │    → Gửi event [PaymentInitiatedEvent]
 ▼
Kafka
 └─ Phát PaymentInitiatedEvent

PaymentService
 ├─ Nhận PaymentInitiatedEvent
 ├─ Tạo Payment (meta data)
 ├─ update paymentProcessed=SUCCESS
 └─ Gửi [PaymentProcessedEvent]

 ┌──────────────────────────────────────────────────────────────────────────────┐
 │                    PHASE 4: STOCK FULFILLMENT (ASYNC)                        │
 └──────────────────────────────────────────────────────────────────────────────┘

InventoryService
 ├─ Background job: phân phối FIFO cho PurchaseItem thật
 ├─ Khi xong → Gửi [StockFulfilledEvent]
 ▼
Kafka
 └─ Phát StockFulfilledEvent

OrderService
 ├─ Nhận StockFulfilledEvent
 ├─ update stockFulfilled=SUCCESS
 ├─ Nếu tất cả step SUCCESS → mark Order COMPLETED
 └─ Kết thúc Saga

 ┌──────────────────────────────────────────────────────────────────────────────┐
 │                     PHASE 5: COMPENSATION (ROLLBACK)                         │
 └──────────────────────────────────────────────────────────────────────────────┘

Bất kỳ bước nào FAIL:
 ├─ Service phát [OrderSagaCompensationTriggeredEvent]
 ├─ Các service liên quan cùng rollback song song:
 │   - OrderService → cancel order
 │   - InventoryService → release stock
 │   - PaymentService → cancel payment
 └─ OrderSagaProgress.markCompensated=true

└────────────────────────────── END SAGA ────────────────────────────────────────┘

```

## Saga Order Cancelation

-   Mô tả: Khi người dùng cancel. Lập tức chuyển đổi trạng thái đang yêu cầu hủy . Không compenstate ngược status sau khi đã yêu cầu hủy (có lỗi tự xử lý thủ công). Sẽ có một cronjob tiến hành kiểm tra các Order xem có order nào cần xử lý . Lúc này khi nhận được các request liên quan đến yêu cầu hủy. Lập tức kiểm tra (SagaTracker hoặc timeout quá giới hạn). lập tức phát sự kiện bù trừ

-   Payment Service:
    -   Tiến hành : đánh dấu canceled . Phát event `PaymentCompensationCompletedEvent`
    -   nếu đã thanh toán: khởi tạo yêu cầu hoàn tiền. Nếu là COD. hoặc PAYMETN GATEWAY THì cố gắng xử lý hoàn tiền tự động. Hoặc hoàn tiền thủ công nếu thất bại ( tạo bản ghi yêu cầu hoàn tiền)

- Inventory Service 
    - Đã giữ chỗ 

    - Đã chuyển giao
