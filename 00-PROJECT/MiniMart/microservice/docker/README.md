## Deploy Road Map

### 🚀 GIAI ĐOẠN 1 – AUTH - MINIMAL CODE

- Postgres
- config-server
- Eureka
- Auth-Service
- Gateway
- (Optional) Redis
- (Không) KAFKA
- (KHÔNG) Zipkin

### ✅ Hoàn thành

- Angular gọi `/api/auth/login`
- Gateway forward được
- Auth trả JWT
- Không lỗi connect DB
- Service đăng kí Eureka thành công
-

#### 🎯 MỤC TIÊU HOÀN THÀNH

```
Angular → Gateway → Auth → Postgres
```

- Không Kafka
- Không Zipkin
- Không Saga
- Không Inventory

À 👍 đúng rồi, em nghĩ như vậy là **chuẩn production mindset hơn nhiều**.

Checklist trước đó là theo “logic code”.
Còn bây giờ anh viết lại theo **logic triển khai hạ tầng → service → tích hợp** đúng kiểu DevOps thật sự.

---

# 🚀 CHECKLIST GIAI ĐOẠN 1 – DEPLOY THEO THỨ TỰ HẠ TẦNG

Mục tiêu cuối cùng:

```text
Angular → Gateway → Auth → Postgres
```

Nhưng phải build từ dưới lên.

---

# 🧱 PHASE 1 — INFRASTRUCTURE FIRST (ĐỘC LẬP HOÀN TOÀN)

## ✅ Bước 1 — Postgres chạy độc lập

Trong docker-compose chỉ để:

- postgres

### Checklist

- [ ] Container postgres start OK
- [ ] Port mapping 5433:5432 hoạt động
- [ ] Có thể connect bằng DBeaver / pgAdmin
- [ ] Tạo DB `authdb`
- [ ] Restart container không mất DB (volume nếu cần)

👉 Khi bước này chưa ổn → không được đi tiếp.

---

## ✅ Bước 2 — Eureka chạy độc lập

Thêm:

- eureka-server

Checklist:

- [ ] [http://localhost:8761](http://localhost:8761) mở được
- [ ] Không crash khi start
- [ ] Không phụ thuộc service khác

👉 Eureka phải chạy trước khi Auth xuất hiện.

---

## ✅ Bước 3 — Config Server

Thêm:

- configuration-server

Checklist:

- [ ] [http://localhost:8888](http://localhost:8888) mở được

- [ ] Truy cập được config:

    ```
    http://localhost:8888/auth-service/default
    ```

- [ ] Không lỗi git repo config

👉 Nếu Config lỗi, Auth sẽ fail ngay khi boot.

---

# 🧩 PHASE 2 — SERVICE LAYER (TỪ DƯỚI LÊN)

## ✅ Bước 4 — Auth chạy độc lập (chưa Gateway)

Thêm:

- auth-service

Checklist:

- [ ] Auth start không crash
- [ ] Log không có lỗi DB
- [ ] Auth đăng ký vào Eureka
- [ ] Vào Eureka thấy AUTH-SERVICE
- [ ] Test trực tiếp:

    ```
    http://localhost:<auth-port>/actuator/health
    ```

👉 Lúc này Gateway chưa cần.

---

# 🌐 PHASE 3 — ENTRY LAYER

## ✅ Bước 5 — Gateway

Thêm:

- gateway-server

Checklist:

- [ ] Gateway start OK

- [ ] Gateway đăng ký Eureka

- [ ] Route lb://AUTH-SERVICE hoạt động

- [ ] Test:

    ```
    POST http://localhost:8080/api/auth/login
    ```

- [ ] Gateway forward được

---

# 🎯 PHASE 4 — CLIENT INTEGRATION

## ✅ Bước 6 — Angular

Checklist:

- [ ] baseUrl = [http://localhost:8080](http://localhost:8080)
- [ ] Login thành công
- [ ] Nhận JWT
- [ ] Không CORS lỗi

---

# 🔥 THỨ TỰ CHUẨN TRIỂN KHAI

```text
1️⃣ Database
2️⃣ Service Discovery (Eureka)
3️⃣ Config Server
4️⃣ Core Service (Auth)
5️⃣ Gateway
6️⃣ Client
```

---

# 🧠 Tư duy đúng của em bây giờ là:

> Hạ tầng phải chạy độc lập trước
> Rồi mới đến service phụ thuộc

Đó là tư duy của người làm hệ thống thật.

---

# 📌 Lưu ý cực kỳ quan trọng

Trong docker-compose:

KHÔNG tin `depends_on` hoàn toàn.

Service có thể start nhưng DB chưa sẵn sàng.

Nếu gặp lỗi này → phải thêm:

- retry mechanism
- hoặc healthcheck chặt hơn

---

# 🏁 GIAI ĐOẠN 1 HOÀN THÀNH KHI

✔ Postgres chạy ổn định
✔ Eureka + Config độc lập
✔ Auth đăng ký Eureka
✔ Gateway forward thành công
✔ Angular login được

---
