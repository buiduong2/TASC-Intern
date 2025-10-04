## Phân tách nghiệp vụ

### Danh động từ cốt lõi:

#### 1. Customer

-   `🔍 1. Khám Phá & Tìm kiếm (Discovery & Search)`

    -   xem sản phẩm, lọc theo category, tag, status.

-   `🛒 2. Mua Sắm (Shopping)`

    -   Thêm sản phẩm vào giỏ hàng (Cart – có thể tạm trên client hoặc lưu vào DB riêng).
    -   Xem giỏ hàng.
    -   Chỉnh sửa giỏ hàng: thay đổi số lượng, xóa sản phẩm.
    -   Ước tính chi phí: tính tổng tiền tạm tính.

-   `📑 3. Đặt hàng (Ordering)`

    -   Chọn địa chỉ giao hàng từ danh sách CustomerAddress.
    -   Nhập địa chỉ mới khi cần (sẽ lưu vào OrderAddress).
    -   Chọn phương thức vận chuyển (ShippingMethod).
    -   Chọn phương thức thanh toán (PaymentMethod).
    -   Xác nhận đơn hàng (tạo Order và các OrderItem).

-   `💳 3. Thanh Toán & Giao dịch (Payment & Transaction)`

    -   Thực hiện thanh toán:
    -   Xem lịch sử thanh toán (Payment, PaymentTransaction).
    -   Theo dõi trạng thái thanh toán: PENDING, PARTIAL, PAID, REFUND_PENDING, REFUNDED

-   `👤 4. Quản lý Tài khoản (Account Management)`

    -   Đăng ký, đăng nhập, đăng xuất.
    -   Cập nhật thông tin cá nhân (Customer profile: firstName, lastName, phone).
    -   Đổi mật khẩu.
    -   Quản lý email, username, trạng thái tài khoản (ACTIVE/INACTIVE).

-   `📦 5. Quản Lý Đơn Hàng (Order Management)`

    -   Xem danh sách đơn hàng của mình.
    -   Xem chi tiết đơn hàng: sản phẩm, địa chỉ giao hàng, phương thức vận chuyển, trạng thái (OrderStatus).
    -   Hủy đơn hàng khi còn ở trạng thái PENDING/CONFIRMED (chưa SHIPPED)
    -   Theo dõi vận chuyển (ShippingMethod + OrderStatus).

-   `🏠 6. Quản lý Địa chỉ (Address Management)`
    -   Thêm địa chỉ mới (họ tên, số điện thoại, chi tiết, thành phố, khu vực).
    -   Sửa địa chỉ có sẵn.
    -   Xóa địa chỉ.
    -   Đặt địa chỉ mặc định (isDefault).
    -   Khi đặt hàng:
        -   Chọn từ địa chỉ mặc định hoặc một địa chỉ khác trong danh sách.

### 2. Admin

-   `👤 1. Quản lý Người dùng (User Management)`

    -   Cập nhật thông tin người dùng.
    -   Khóa / mở khóa tài khoản (ACTIVE / INACTIVE).
    -   Đặt lại mật khẩu / buộc đổi mật khẩu.

-   `🛡️ 2. Quản lý Quyền (Role Management)`

    -   Gán vai trò cho user.
    -   Gỡ vai trò khỏi user.
    -   Tạo / sửa / xóa vai trò.
    -   Cập nhật mô tả vai trò.

-   `🛍️ 3. Quản lý Sản phẩm & Danh mục (Product & Category Management)`

    -   Thêm / sửa / xóa sản phẩm.
    -   Cập nhật trạng thái sản phẩm (ACTIVE, DRAFT, ARCHIVED).
    -   Tạo / sửa / xóa danh mục.
    -   Cập nhật trạng thái danh mục.
    -   Tạo / sửa / xóa tag.

-   `📦 4. Quản lý Kho & Cung ứng (Stock & Purchase Management)`

    -   Tạo phiếu nhập hàng (Purchase).
    -   Thêm sản phẩm vào phiếu nhập (PurchaseItem).
    -   Cập nhật số lượng tồn kho (Stock).
    -   Theo dõi tồn kho theo sản phẩm.

-   `📑 5. Quản lý Đơn hàng (Order Management)`

    -   Xem danh sách đơn hàng.
    -   Xem chi tiết đơn hàng.
    -   Cập nhật trạng thái đơn (CONFIRMED, SHIPPED, COMPLETED, CANCELED).
    -   Hủy đơn hàng thủ công.
    -   Cập nhật ghi chú/message của đơn.

-   `💳 6. Quản lý Thanh toán & Giao dịch (Payment & Transaction Management)`

    -   Xem danh sách thanh toán.
    -   Xem chi tiết giao dịch.
    -   Yêu cầu hoàn tiền (REFUND_PENDING).
    -   Đối soát giao dịch theo txnRef, gatewayTxnId.

-   `📊 7. Báo cáo & Phân tích (Reporting & Analytics)`
    -   Báo cáo doanh thu theo ngày/tháng.
    -   Thống kê đơn hàng theo trạng thái.
    -   Báo cáo tồn kho & nhập hàng.
    -   Báo cáo tỉ lệ thanh toán thành công/thất bại.
    -   Thống kê sản phẩm bán chạy / tồn kho chậm luân chuyển.

### 3. Hệ thống

-   `⚙️ 1. Quản lý Luồng Đơn hàng & Kho (Order & Stock Flow)`

    -   Tự động **phân bổ tồn kho** cho OrderItem khi đơn được xác nhận (CONFIRMED).
    -   Tự động **giải phóng tồn kho** khi đơn bị hủy (CANCELED).
    -   Cập nhật số lượng tồn kho còn lại trong `Stock` và `PurchaseItem.remainingQuantity`.
    -   Đồng bộ trạng thái StockAllocation với trạng thái Order/OrderItem.

-   `🔐 2. Quản lý Xác thực & JWT (Authentication & Token Management)`

    -   Phát hành `ACCESS` và `REFRESH` token khi user đăng nhập.
    -   Làm mới token qua `REFRESH`.
    -   Thu hồi token khi user logout (thêm vào `JwtBlacklist`).
    -   Vô hiệu toàn bộ session khi user đổi mật khẩu hoặc logout-all (tăng `User.tokenVersion`).
    -   Kiểm tra token trong request: chữ ký, hạn sử dụng, `JwtBlacklist`, `tokenVersion`.
    -   Xóa token trong blacklist khi đã hết hạn (`expiredAt`).
    -   Quản lý key ký JWT và cung cấp JWKS cho các service nội bộ.
    -   Ghi log toàn bộ phát hành, refresh, revoke token để audit.

-   `📡 3. Tác vụ Hệ thống khác (System Automation)`
    -   Tự động tính toán Payment status dựa trên PaymentTransaction (ví dụ: cộng dồn amountPaid → PAID hoặc PARTIAL).
    -   Thực hiện đồng bộ dữ liệu audit: `createdAt`, `updatedAt`, `createdBy_id`, `updatedBy_id`.

## Bảng Phân Vùng Nghiệp Vụ Microservice (13 Services - EDA & BFF)

-   **Base Services**: Khi ghi sử dụng EDA

    -   Auth Service
    -   Profile Service
    -   Product Service
    -   Inventory Service
    -   Order Service
    -   Payment Service

-   **BFF (Backend for Frontend) & Composition Services**: Khi đọc/tổng hợp dữ liệu

    -   Customer-BFF
    -   Admin-BFF
    -   Reporting Service

-   **Hệ thống & Infrastructure Services**: Các dịch vụ hỗ trợ
    -   API Gateway
    -   Message Broker
    -   Config Server
    -   Eureka Server

### 1. Dịch vụ Cơ sở (Base Services) & Kiến trúc Hướng Sự kiện (EDA)

| STT | Tên Microservice    | Trách nhiệm Cốt lõi                                                                                                                                                   | Các Bảng Dữ liệu Thuộc sở hữu                               |
| :-: | :------------------ | :-------------------------------------------------------------------------------------------------------------------------------------------------------------------- | :---------------------------------------------------------- |
|  1  | `Auth Service`      | Quản lý `Authentication` và `Authorization`; Phát hành/refresh/revoke `JWT`; Quản lý `User`, `Role`.                                                                  | `User`, `Role`, `User_Role`, `JwtBlacklist`                 |
|  2  | `Profile Service`   | Quản lý `Customer Profile` và `CustomerAddress`. `Phát sự kiện` khi thông tin cá nhân/địa chỉ thay đổi.                                                               | `Customer`, `CustomerAddress`                               |
|  3  | `Product Service`   | Quản lý `Sản phẩm` và `Danh mục`. `Lắng nghe sự kiện tồn kho` để duy trì `Snapshot Available Stock` (đọc tối ưu).                                                     | `Product`, `Category`, `ProductImage`, `Tag`, `Product_Tag` |
|  4  | `Inventory Service` | Quản lý `Tồn kho`, `Nhập hàng`, `Phân bổ/giải phóng tồn kho`. `Tham gia Saga`: Lắng nghe `OrderCreatedEvent` để phân bổ tồn kho. `Phát sự kiện` khi tồn kho thay đổi. | `Stock`, `Purchase`, `PurchaseItem`, `StockAllocation`      |
|  5  | `Order Service`     | Quản lý vòng đời `Đơn hàng`. `Khởi tạo Saga`: Phát `OrderCreatedEvent`. `Lắng nghe sự kiện Saga` (từ Inventory, Payment) để cập nhật trạng thái/snapshot.             | `Order`, `OrderItem`, `OrderAddress`, `ShippingMethod`      |
|  6  | `Payment Service`   | Xử lý và ghi nhận `Thanh toán` và `Giao dịch`. `Tham gia Saga`: Lắng nghe `OrderCreatedEvent` và `Phát sự kiện` `PaymentCompletedEvent`.                              | `Payment`, `PaymentTransaction`                             |

### 2. BFF (Backend for Frontend) & Composition Services

| STT | Tên Microservice    | Trách nhiệm Cốt lõi                                                                                                                     | Các Bảng Dữ liệu Thuộc sở hữu        |
| :-: | :------------------ | :-------------------------------------------------------------------------------------------------------------------------------------- | :----------------------------------- |
|  1  | `Customer-BFF`      | `Composition ĐỌC` dữ liệu cho Khách hàng (Cart Review, Order Detail). `Cổng vào GHI` khởi tạo Order Saga. Tối ưu hóa API cho Client.    | Không sở hữu bảng dữ liệu            |
|  2  | `Admin-BFF`         | `Composition ĐỌC/GHI` dữ liệu cho Dashboard Admin (Quản lý Order, User, Báo cáo). Thực hiện các thao tác CRUD quản trị.                 | Không sở hữu bảng dữ liệu            |
|  3  | `Reporting Service` | `Read-Model chuyên biệt` tổng hợp dữ liệu từ các sự kiện để tính toán và phục vụ các báo cáo phức tạp (Doanh thu, Tồn kho luân chuyển). | `Report_Models` (tối ưu cho báo cáo) |

### 3. Hệ thống & Infrastructure Services

| STT | Tên Microservice | Trách nhiệm Cốt lõi                                                                                                         | Các Bảng Dữ liệu Thuộc sở hữu |
| :-: | :--------------- | :-------------------------------------------------------------------------------------------------------------------------- | :---------------------------- |
|  1  | `API Gateway`    | Điểm vào duy nhất. Xác thực JWT. `Định tuyến` đến BFFs và các Base Services (cho phép gọi trực tiếp các endpoint đơn giản). | Không sở hữu bảng dữ liệu     |
|  2  | `Message Broker` | `Trái tim của EDA`. Đảm bảo việc truyền tải các sự kiện tin cậy giữa các Microservice (Ví dụ: Kafka/RabbitMQ).              | Không sở hữu bảng dữ liệu     |
|  3  | `Config Server`  | Cung cấp cấu hình tập trung cho toàn bộ microservice.                                                                       | Không sở hữu bảng dữ liệu     |
|  4  | `Eureka Server`  | Đăng ký, discovery và theo dõi tình trạng của các microservice.                                                             | Không sở hữu bảng dữ liệu     |
