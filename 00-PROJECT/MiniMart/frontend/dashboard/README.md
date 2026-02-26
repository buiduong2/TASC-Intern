##  Mục tiêu dashboard

- 1. Quan sát hệ thống (monitoring)
- 2. Điều hành nghiệp vụ (order/ Inventory/ Payment)
- 3. Phân tích dữ liệu (reporting)
- 4. Debug luồng saga khi có lỗi

### 2. Kiến trúc Angular đề xuất

```
src/app/
│
├── core/
│   ├── auth/
│   ├── guards/
│   ├── interceptors/
│   ├── layout/
│   ├── config/
│   └── core.module.ts
│
├── shared/
│   ├── components/
│   ├── directives/
│   ├── pipes/
│   ├── ui/
│   └── utils/
│
├── features/
│   ├── dashboard/
│   ├── users/
│   ├── roles/
│   ├── products/
│   ├── inventory/
│   ├── orders/
│   ├── payments/
│   ├── reporting/
│   └── saga-monitor/
│
├── app.routes.ts
└── main.ts
```

- Standalone component
- Route-level lazy loading
- Smart / Presentational pattern
### 3. Sitemap Dashboard
```
/admin
   ├── dashboard
   ├── users
   ├── roles
   ├── products
   ├── categories
   ├── inventory
   ├── orders
   ├── payments
   ├── reporting
   ├── saga-monitor
```

# 📊 4. Trang Dashboard chính (Overview)

## Hiển thị:

### 🎯 KPI Cards

* Tổng doanh thu hôm nay
* Tổng đơn hàng hôm nay
* Pending orders
* Tỉ lệ thanh toán thất bại
* Số sản phẩm sắp hết hàng

### 📈 Charts

* Revenue theo ngày / tháng
* Order status distribution (Pie chart)
* Top 10 sản phẩm bán chạy
* Tỉ lệ Payment success / fail

Dữ liệu lấy từ:

* `Reporting Service`
* `Admin-BFF`

---

# 👥 5. User Management

## Tính năng:

* Search + filter theo status
* Lock / unlock
* Reset password
* Force change password
* Gán role

### Table columns:

```
ID | Email | Full Name | Role | Status | Last Login | Actions
```

Phân quyền UI dựa trên Role từ:
Spring Security

---

# 🛍 6. Product Management

## Trang Products

* Table + filter:

  * Status: ACTIVE / DRAFT / ARCHIVED
  * Category
  * Tag

* Modal edit:

  * Thay đổi giá
  * Thay đổi ảnh
  * Update status

👉 Khi update:

* Gửi request → Admin-BFF
* Backend phát event
* Cache dirty update

Bạn có thể thêm:

🧠 Hiển thị `lastUpdatedAt`
→ Cho admin thấy độ trễ cache (rất hay khi phỏng vấn)

---

# 📦 7. Inventory Management

## Trang này cực quan trọng vì bạn có Saga + Allocation

Hiển thị:

```
Product | Total Stock | Reserved | Available | Incoming
```

Có thể thêm:

* View Purchase history
* View StockAllocation theo OrderId

👉 Khi có oversell attempt
→ Hiển thị alert từ SagaTracker

---

# 📑 8. Order Management (Quan trọng nhất)

## Table

```
OrderId | Customer | Total | PaymentStatus | OrderStatus | CreatedAt | Actions
```

## Detail Page

Hiển thị:

* Timeline trạng thái:

  * CREATED
  * CONFIRMED
  * SHIPPED
  * COMPLETED
  * CANCELED

* Payment status

* Stock allocation

* TraceId (debug)

🔥 Bạn có thể làm:

* Status machine UI (chỉ cho phép tiến tới)
* Disable reverse state

---

# 💳 9. Payment Management

Hiển thị:

```
PaymentId | OrderId | Amount | Status | GatewayTxnId | TxnRef
```

Cho phép:

* Manual refund
* Retry payment

Có thể thêm:

* Badge màu cho:

  * PENDING
  * PARTIAL
  * PAID
  * REFUND_PENDING
  * REFUNDED

---

# 📊 10. Reporting Page

Từ:
`Reporting Service`

Hiển thị:

* Revenue line chart
* Order count bar chart
* Inventory turnover
* Payment success rate

Bạn nên dùng:

* ngx-charts hoặc echarts

---

# 🧠 11. Saga Monitor (Điểm ăn tiền phỏng vấn)

Trang đặc biệt:

```
OrderId | Step | Status | LastUpdate | ErrorMessage
```

Hiển thị:

* Inventory reserved?
* Payment success?
* Compensation triggered?

Nếu có lỗi:

* Highlight đỏ
* Cho phép retry step

Trang này thể hiện:
👉 Bạn hiểu distributed system

---

# 🔐 12. Auth Flow Angular

* Login
* Refresh token interceptor
* Auto logout nếu 401
* Route guard theo Role

Flow:

```
Login
↓
Store access + refresh
↓
Interceptor gắn token
↓
401 → gọi refresh
↓
Nếu fail → logout
```

---

# 🧱 13. State Management

Bạn có thể:

### Option 1 (đơn giản):

RxJS + service state

### Option 2 (chuẩn enterprise):

NgRx

Nếu đi phỏng vấn:

* Biết NgRx là điểm cộng
* Nhưng dùng quá sớm sẽ overkill

---

# 🚀 14. Lộ trình build

### Giai đoạn 1

* Layout
* Sidebar
* Routing
* Login

### Giai đoạn 2

* Product CRUD
* Order list + detail

### Giai đoạn 3

* Dashboard KPI
* Reporting charts

### Giai đoạn 4

* Saga monitor
* Advanced filtering
* Caching indicator

---

# 🎯 15. Điều quan trọng nhất

Dashboard của bạn nên thể hiện:

* Bạn hiểu event-driven
* Bạn hiểu Saga
* Bạn hiểu consistency trade-off
* Bạn hiểu caching

Chứ không chỉ là Angular UI.

---

# Nếu bạn muốn mình giúp sâu hơn

Mình có thể:

* Thiết kế cụ thể folder structure cho Angular
* Viết mẫu code Product feature chuẩn
* Thiết kế state management pattern
* Thiết kế UI table reusable generic
* Thiết kế data contract từ Admin-BFF

---
