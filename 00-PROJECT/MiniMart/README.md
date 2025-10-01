# Dự án Thực tập công ty TASC

ocker-compose cho môi trường dev (DB, Redis, RabbitMQ).

# 2. ✅ Checklist Tiến Độ

-   Bắt đầu: 10/9/2025
-   Kết thúc: 18/11/2025

| Tuần | Công việc chính                                    | Trạng thái |
| ---- | -------------------------------------------------- | ---------- |
| 1    | Security (JWT), skeleton, seed dữ liệu, FE Login   | ⬜         |
| 2    | Purchase/PurchaseItem + Stock lô                   | ⬜         |
| 3    | Order + FIFO allocation + Cancel                   | ⬜         |
| 4    | Báo cáo Doanh thu/COGS/Lợi nhuận                   | ⬜         |
| 5    | Catalog hoàn thiện (1 ảnh/product, category 1 cấp) | ⬜         |
| 6    | Redis cache + invalidation                         | ⬜         |
| 7    | Payment flow (chuẩn bị VNPay) + cứng hoá security  | ⬜         |
| 8    | VNPay sandbox tích hợp                             | ⬜         |
| 9    | RabbitMQ events + cảnh báo tồn thấp (FE)           | ⬜         |
| 10   | Microservice step-1 + gateway + tài liệu OAuth2    | ⬜         |

## 1. Giới thiệu

-   Dự án Web bán hàng thương mai điện tử.

## 1. Mục tiêu

-   Thành thạo `Spring Boot, JPA/Hibernate`
-   Làm quen và áp dụng `Redis cache` để tối ưu hiệu năng
-   Nắm được `kiến trúc microservice` và cách refactor từ monolith
-   Hiểu và áp dụng `Message Queue (RabbitMQ/Kafka)` trong giao tiếp dịch vụ.
-   Tích hợp `cổng thanh toán VNPay` để xử lý thanh toán trực tuyến.
-   Củng cố kỹ năng `xây dựng API, bảo mật (JWT, sau này OAuth2), viết test, và phát triển FE đơn giản`

## 2. Phạm vi chức năng

### Quản lý sản phẩm (Catalog)

-   CRUD Product/Category/Tag.
-   Mỗi sản phẩm chỉ có `1 ảnh duy nhất.`
-   Category chỉ có `1 cấp` (không phân cấp sâu).
-   Quản lý trạng thái sản phẩm (ACTIVE, DRAFT, ARCHIVED).

### Quản lý kho (Inventory)

-   Tạo phiếu nhập hàng (Purchase)
-   Quản lý từng `lô hàng (PurchaseItem)`: số lượng, giá nhập, số lượng còn lại (`remaining_quantity`).
-   Tự động cập nhật tồn kho tổng (Stock).
-   Xuất hàng theo `FIFO` để tính `giá vốn (COGS).`

### Quản lý đơn hàng (order)

-   Tạo đơn hàng (Order) + các OrderItem.
-   Quản lý vòng đời đơn hàng: PENDING → PAID → SHIPPED → COMPLETED → CANCELED.
-   Khi đơn được đặt, hệ thống `allocate tồn kho theo FIFO.`
-   Hủy đơn: hoàn trả tồn kho

### Thanh toán (Payment)

-   Giai đoạn đầu: thanh toán nội bộ (mock).
-   Sau này: tích hợp `VNPay sandbox` → kiểm tra callback, IPN, checksum.
-   Quản lý trạng thái Payment (PENDING, PAID, FAILED).

### Báo cáo

-   Doanh thu, giá vốn (COGS), lợi nhuận gộp.
-   Lọc theo khoảng thời gian, theo sản phẩm.

### Bảo mật & người dùng

-   Đăng ký, đăng nhập với `JWT access + refresh token.`
-   Quản lý role (ADMIN, STAFF, CUSTOMER).
-   Sau này nâng cấp OAuth2 Authorization Server.

### Hạ tầng bổ sung

-   Redis Cache: cache danh sách sản phẩm, category, tồn kho khả dụng.
-   Message Queue (RabbitMQ): phát sự kiện (order.create, order.canceled, stock.low).
-   Microservice: sau khi hệ thống monolith ổn định sẽ tách dần theo domain (Auth, Catalog, Order, Payment).

### Công nghệ sử dụng

-   Backend: Spring Boot, Spring Data JPA, Spring Security, JWT, Redis, RabbitMQ.
-   Database: PostgreSQL
-   Frontend: React
-   Khác: D

# 2. 📌 Roadmap 10 Tuần (2.5 Tháng) – Dự án Monolith → Microservice

## Tuần 1: Security cơ bản + Skeleton + Seed cơ sở

**Mục tiêu**

-   Khởi tạo monolith (Spring Boot + JPA, cấu trúc theo domain).
-   Auth thường: đăng ký/đăng nhập, **JWT access + refresh**, roles (ADMIN/STAFF/CUSTOMER).
-   **Seed dữ liệu bằng Java code** (CommandLineRunner): Role/User mẫu, ShippingMethod, vài Category/Product mẫu.
-   (Tuỳ chọn) Module “data-import” để **cào dữ liệu** và bơm vào DB.

**FE**

-   Khởi tạo FE (React/Vite hoặc Vue). Trang Login + layout dashboard.

**Deliverables**

-   Swagger/OpenAPI & README (cách chạy BE/FE, cách bật seeder/importer).

---

## Tuần 2: Purchase & PurchaseItem → Ghi tồn kho

**Mục tiêu**

-   API phiếu nhập (Purchase) + PurchaseItem.
-   Lưu lô: `cost_price`, `remaining_quantity`; cập nhật tồn tổng (Stock).

**FE**

-   Trang “Nhập hàng”: tạo phiếu, thêm dòng hàng, xem tồn tổng và theo lô.

---

## Tuần 3: Order core (PENDING) + FIFO allocation

**Mục tiêu**

-   API Order/OrderItem, tính `total` snapshot (sale_price tại thời điểm đặt).
-   **Allocate** tồn theo **FIFO** khi “xác nhận/paid nội bộ”.
-   Hủy order → **hoàn lại lô** tương ứng.

**FE**

-   Trang tạo đơn: chọn sản phẩm, số lượng; hiển thị tồn khả dụng; xác nhận/hủy.

---

## Tuần 4: COGS & Báo cáo lợi nhuận cơ bản

**Mục tiêu**

-   Khi đơn “được thanh toán” (nội bộ), ghi **COGS** theo FIFO vào OrderItem.
-   API báo cáo: Doanh thu, COGS, **Lợi nhuận gộp**, filter theo thời gian/sản phẩm.

**FE**

-   Trang báo cáo đơn giản (bảng + tổng số).

---

## Tuần 5: Catalog hoàn thiện (1 ảnh/product) + Admin UX

**Mục tiêu**

-   CRUD Product/Category/Tag; ràng buộc **1 ảnh/product**, category 1 cấp.
-   Tối ưu filter/sort/pagination; status (ACTIVE/DRAFT/ARCHIVED).

**FE**

-   Trang quản trị sản phẩm/category: tạo/sửa/xóa, **upload 1 ảnh/product**.

---

## Tuần 6: Redis Cache – đợt 1

**Mục tiêu**

-   Tích hợp **Redis cache** cho: danh sách Product, Category, tồn khả dụng theo product.
-   **Invalidation** khi CRUD/nhập hàng/đơn hàng.

**FE**

-   Kiểm tra tốc độ hiển thị list (trước/sau cache), cảm nhận UX nhanh hơn.

---

## Tuần 7: Củng cố Security + Thiết kế Payment

**Mục tiêu**

-   Cứng hoá security hiện có (role guard, revoke/rotate refresh—nếu kịp).
-   Thiết kế **Payment model/flow** (PENDING/PAID/FAILED) để chuẩn bị **VNPay**.
-   Chuẩn bị endpoint: tạo “payment intent”, return URL, notify URL (chưa tích hợp thật).

**FE**

-   Nút “Thanh toán” (mock) đổi trạng thái nội bộ để kiểm thử flow.

---

## Tuần 8: Tích hợp **VNPay sandbox**

**Mục tiêu**

-   Redirect sang VNPay; xử lý **returnUrl** và **IPN** (checksum, idempotency).
-   Đồng bộ Payment.status → Order.status.

**FE**

-   Hoàn thiện flow: Nhấn thanh toán → VNPay → quay lại hiển thị kết quả.

---

## Tuần 9: Message Queue (RabbitMQ) & Event hóa

**Mục tiêu**

-   Phát event: `order.paid`, `order.canceled`, `stock.low`.
-   Consumer ví dụ: audit/log/notification/report (mock).
-   Đặt **event contract** để chuẩn bị tách service.

**FE**

-   Dashboard hiển thị cảnh báo “tồn thấp” (polling đơn giản).

---

## Tuần 10: Microservice (bước 1) + Hướng OAuth2

**Mục tiêu**

-   **Tách 1 service**: Catalog (Product/Category) **hoặc** Auth; thêm API Gateway.
-   Chuẩn bị tài liệu **nâng cấp OAuth2** sau này (Authorization Server/Keycloak).
-   Docker-compose: DB + Redis + RabbitMQ + (gateway + services).

**FE**

-   Cấu hình base URL qua gateway; test end-to-end.

---

---