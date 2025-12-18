##

## Service pending quá lâu

-   Dùng `cronjob định kỳ (10 phút)` kiểm tra các đơn pending quá lâu trong OrderSagaTracker. Nếu quá hạn → gửi lại sự kiện hoặc đánh dấu timeout, ghi log vào saga_failure_queue.

## Hai đơn hàng cùng đặt cùng sản phẩm, tránh oversell thế nào?

-   InventoryService chịu trách nhiệm; dùng pessimistic locking (SELECT … FOR UPDATE) hoặc optimistic lock / reservation token. Có thể sắp xếp productId tăng dần để tránh deadlock

## Payment thành công nhưng Inventory fail thì sao?

-   Payment được gọi sau khi reserve thành công. Nếu vẫn xảy ra lỗi, Saga compensation kích hoạt → gửi RefundCommand đến PaymentService → hoàn tiền hoặc alert thủ công.

## Xử lý trùng event

-   Dùng OrderSagaTracker để lock theo orderId, kiểm tra status trước khi xử lý; nếu step đã SUCCESS thì bỏ qua. Ngoài ra log eventId vào bảng processed_events.

## ProductService trả giá sai (price mismatch) thì sao?

-   ProductService là source of truth. Khi nhận order, OrderService gọi xác nhận lại giá. Nếu sai lệch → hủy đơn + log lỗi hoặc cho vào pending review. Có thể thêm priceVersion / signature để tránh giả mạo.

## Hệ thống có cho phép đảo ngược trạng thái đơn hàng không?

-   Hiện tại là one-way state (trạng thái chỉ tiến lên). Nếu cần đảo ngược → phải tách state machine riêng và thiết kế Saga bù trừ ngược (compensating saga) cho mỗi bước đảo

## Làm sao truy vết lỗi của một đơn hàng (từ Order → Payment → Inventory)?

-   Dùng traceId gắn vào mọi event → tra trong ELK hoặc Zipkin. Ngoài ra, OrderSagaTracker lưu step-by-step để xác định lỗi nằm ở đâu.

## Nếu gửi sự kiện Kafka bị lỗi hoặc mất kết nối, làm sao không mất event?

-   Chưa dùng Outbox, nhưng có callback trong KafkaTemplate.send(). Khi fail → lưu sự kiện vào retry queue / DB, có cronjob retry định kỳ. Đảm bảo at-least-once delivery.

## Khi Payment đã thành công mà khách hủy đơn hàng, xử lý thế nào?

-   OrderService gửi RefundCommand cho PaymentService, PaymentService dùng transactionId hoàn tiền. Nếu fail → log vào saga_failure_queue hoặc alert manual refund.

## Nếu dùng Local Cache + Redis 2-level thì rủi ro gì, xử lý ra sao?

-   Rủi ro: stale cache / data lệch pha. Giải pháp: TTL ngắn, lắng nghe event update để invalid cache, so sánh version/timestamp trước khi ghi đè, tắt local cache với SKU hot.

### Kiến trúc

## Vì sao dùng callback + retry queue thay vì Outbox pattern?

-   ưu tiên đơn giản, chưa cần Outbox.
-   Đang dùng cơ chế tương đương

## Outbox pattern có gửi event từng cái hay theo batch

-   thống nhỏ → từng event, production → batch 100–200 event/lần để tăng throughput. Hệ thống lớn hơn có thể dùng CDC (Debezium) để tự động publish từ DB log

## Có cơ chế nào đảm bảo idempotency giữa các service?

-   Mỗi event có eventId và sagaId, được ghi vào processed_events. Trước khi xử lý event mới → check tồn tại eventId. Ngoài ra lock theo orderId trong SagaTracker.

## Nếu interviewer hỏi “tại sao chưa làm Outbox?”, trả lời sao?

-   Hiện tại team em ưu tiên tính đơn giản, traffic chưa lớn. Em hiểu Outbox pattern giải quyết atomic commit giữa DB và event, nên khi hệ thống scale em sẽ chuyển sang hướng đó

#### Tổng hợp

-   `Xử lý đơn hàng bị pending`

    -   cronjob định kỳ (10p/lần)
    -   gửi lại sự kiện hoặc đánh dấu timeout.

-   `Tránh oversell khi 2 đơn cùng đặt 1 sản phẩm`

    -   pessimistic locking
    -   Sắp xếp lock theo productId để tránh deadlock.

-   `Payment success nhưng Inventory fail`

    -   sau khi reserve thành công.
    -   Saga compensation kích hoạt, gửi RefundCommand sang PaymentService để refund/void.

-   `Duplicate message`

    -   OrderSagaTracker
    -   Ngoài ra log eventId vào processed_events để idempotent.

-   `Price mismatch`

    -   source of truth.
    -   cancel order, log lỗi hoặc chuyển vào pending review.

-   `Hỗ trợ đảo ngược trạng thái đơn hàng`

    -   Hiện tại là luồng 1 chiều.
    -   tách state machine riêng + thêm saga bù trừ ngược (compensating saga) cho mỗi bước.

-   `Truy vết lỗi`

    -   Mỗi event gắn traceId để tra trong ELK / Zipkin.
    -   Kết hợp thông tin trong OrderSagaTracker để biết lỗi nằm ở service nào.

-   `Kafka send bị lỗi / mất kết nối`

    -   Dùng callback trong KafkaTemplate.send().
    -   Nếu fail → ghi event vào retry queue / event_retry_store. Cronjob retry sau (10p).

-   `10️⃣ Hủy đơn sau khi đã thanh toán`

    -   OrderService gửi RefundCommand cho PaymentService. PaymentService gọi gateway refund qua transactionId. Nếu refund fail → log vào saga_failure_queue và alert thủ công.

-   `11️⃣ Khi chưa dùng Outbox pattern`

    -   cơ chế tương đương
    -   atomic giữa DB và event , sau này sẽ áp dụng

-   `12️⃣ Outbox gửi từng event hay batch?`

    -   gửi từng event (đơn giản) hoặc batch 100–200/lần để tăng throughput.

    -   Hệ thống lớn có thể dùng CDC (Debezium) để tự động publish.

-   `🧩 13️⃣ Idempotency giữa các service`

    -   Mỗi event có eventId duy nhất, lưu trong processed_events.
    -   Trước khi xử lý, check eventId đã tồn tại chưa.
    -   Thêm check logic + lock orderId trong SagaTracker.

-   `🕵️‍♂️ 14️⃣ Monitoring & alert`
    -   Dùng ELK/Zipkin để theo dõi trace. Cronjob cảnh báo nếu đơn pending >10 phút hoặc retry quá số lần.
    Log lỗi vào error_log.

-  `15️⃣ 2-level cache (Local + Redis)`
    - stale cache / lệch dữ liệu.

    - TTL ngắn, lắng nghe event để invalid cache, kiểm tra version/timestamp trước khi ghi đè.

- `16️⃣ Khi bị hỏi "tại sao chưa làm Outbox"`
    - Hiện tại team ưu tiên đơn giản, traffic chưa quá lớn
    - Em hiểu Outbox pattern, sẽ triển khai

- 
