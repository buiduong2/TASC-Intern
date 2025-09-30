## Phân tách nghiệp vụ

### Danh động từ cốt lõi:

## Bảng Phân Vùng Nghiệp Vụ Microservice (6 Service)

|  STT  |   Tên Microservice    | Trách nhiệm Cốt lõi                                                                                                                      | Các Bảng Dữ liệu Thuộc sở hữu                                                |
| :---: | :-------------------: | :--------------------------------------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------- |
| **1** | **Identity Service**  | Quản lý **Xác thực (Authentication)**, **Danh tính** (`User`), và **Phân quyền** (`Role`) cho toàn bộ hệ thống (Admin, Staff, Customer). | `User`, `Role`, `User_Role`, `JwtBlacklist`                                  |
| **2** |  **Profile Service**  | Quản lý **Hồ sơ (Profile)** chi tiết của khách hàng và thông tin địa chỉ giao nhận riêng của họ. **(Không sở hữu Order)**                | `Customer`, `CustomerAddress`                                                |
| **3** |  **Catalog Service**  | Quản lý toàn bộ **Sản phẩm**, **Danh mục**, **Hình ảnh**, và **Tags** (Dữ liệu tĩnh của sản phẩm).                                       | `Product`, `Category`, `ProductImage`, `CategoryImage`, `Tag`, `Product_Tag` |
| **4** | **Inventory Service** | Quản lý **Tồn kho** (`Stock`), các giao dịch **Nhập hàng** (`Purchase`), và cơ chế **Phân bổ Tồn kho** (`StockAllocation`).              | `Stock`, `Purchase`, `PurchaseItem`, `StockAllocation`                       |
| **5** |   **Order Service**   | Quản lý vòng đời của **Đơn hàng** (tạo, cập nhật trạng thái, tính tổng tiền). Liên kết trực tiếp với **User ID**.                        | `Order`, `OrderItem`, `OrderAddress`, `ShippingMethod`                       |
| **6** |  **Payment Service**  | Xử lý và ghi nhận mọi hoạt động **Thanh toán** và **Giao dịch** liên quan đến tiền tệ và cổng thanh toán.                                | `Payment`, `PaymentTransaction`                                              |
