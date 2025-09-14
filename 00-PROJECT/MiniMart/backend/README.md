# Báo cáo dự án thực tập - Mini Mart

## Câu hỏi

- Tại khi đăng nhập vào facebook tại sao 1 năm sau nó vẫn lưu đăng nhập

## 1. Giới thiệu

Mini Mart là ứng dụng web E-commerce dạng demo, được xây dựng trong thời gian thực tập với mục tiêu áp dụng các kiến thức đã học về **Java, Spring Boot, JPA, Spring Security** và thiết kế cơ sở dữ liệu.

-   Tập trung vào quản lý sản phẩm, đơn hàng và kho, có tính năng quản lý nhập hàng để phục vụ tính toán lợi nhuận

## 2. Các chức năng

### Product

-   Tạo cập nhật xóa, xem chi tiết
-   Mỗi sản phảm chỉ có một hình ảnh đại diện
-   Mõi sản phẩm thuộc 1 danh mục (1 cấp)
-   Không có biến thể hay thuộc tính động để lọc
-   Có các trạng thái (ACTIVE, DRAFT, ARCHIVED)

-   Quản lý thẻ (Tag) để gán cho sản phẩm

### Category

-   Quản lý danh mục sản phẩm 1 cấp
-   Danh mục có thể kèm theo hình ảnh địa diện
-   Trạng thái danh mục (ACTIVE, DRAFT, ARCHIVED)

### Inventory

-   Purchase/ purchaseItem: quản lý các lần nhập hàng từ nhà cung cấp
-   FIFO: sử dụng remaining_quantity để xuất kho theo lô hàng
-   Stock: lưu tổng số lượng hiện tại trong kho (1 kho duy nhất)
-   Tính `giá vốn hàng bán` (cost_price) dựa trên các lần nhập
-   Từ đó tính ra `lợi nhuận` khi so sánh với giá bán

### Order

-   Quản lý đơn hàng với nhiều sản phẩm
-   quản lý trạng thái đơn hàng
-   Quản lý thanh toán
-   Khi tạo đơn hàng sẽ allocate stock (trừ dần PurchaseItem FIFO)

### User

-   Custtomer: thông tin khác hàng
-   User: thông tin tài khoản người dùng trong hệ thống
-   Quản lý phân quyền role . User- role
-   ADdress: qunar lý địa chỉ của khách có nhiều address

## 2.1. Ứng dụng Query theo nhiều cách

- projection
    - Sử dụng với User

- JdbcTemplate Dùng cho

## 3. Luồng nghiệp vụ chính

### nhập hàng vào kho

-   0. Tạo các thông tin cơ bản về sản phẩm nếu chưa có

    -   Tạo ra một Stock với quantity = 0 tương ứng;

-   1. Tạo phiếu nhập

    -   Nhập vào là hoạt động không phê duyệt

-   2. Thêm các sản phẩm (PurchaseItem)

    -   `product_id`
    -   `quantity`
    -   `cost_price` -> cơ sở tính toán lợi nhuận
    -   Từ đó ta có `remainingQuantity`

-   3. Cập nhật tồn kho Stock (cache)
    -   Với mỗi sản phẩm vừa thay đổi số lượng -> Cập nhật tồn kho Stock `SUM(purchaseItem.remainingQuantity)`

### Luồng tạo order (PENDING)

-   1. Nhận thông tin Order từ Khách

    -   Customer
    -   OrderAddress
    -   ShippingMethod
    -   PaymentMethod
    -   List OrderItems

-   2. Tạo các bản ghi tương ứng

    -   status = PENDING
    -   payment.status = PENDING
    -   lưu total snapshot

-   3. Tạo các OrderItem

    -   Tìm các sản phẩm tương ứng (phải có Status = ACTIVE)
    -   Lấy ra giá sell (bằng giá niêm yết trên web)

-   4. Ghi nhận Stock Allocation

    -   Tìm các PurchaseItem còn sản phẩm và tiến hành FIFO
    -   Phân bổ số lượng
    -   Ghi nhận StockAllocation để dễ dàng truy vết và hoàn trả

-   5. Tính ra avg Cost cho OrderItem để theo dõi lợi nhuận tạm thời

-   6. cập nhật Stock

    -

-   Như vậy tránh được oversell. Nhung lại có thể dính Order ảo . Ma quái phá hoại

### Luồng CANCLED order user

-   Của user

    -   Quyền sở hữu phải thuộc về User
    -   Status === PENDING mới cho hủy
    -   CARD + amounPaid > 0 . Không được hủy
    -   CASH chưa thành toán cho phép hủy

-   Service:

    -   findBy Id
    -   Kiểm tra trạng thái

    -   Update trạng thái
    -   Release Stock Allocation

## AUth

-   Đăng kí đăng nhập   
    - Cung cấp Token để request các lần tiếp theo

-   Đổi mật khẩu
    - Ép buộc tất cả các phiên đăng nhập trước đều vo hiêu họa

-   RefreshToken    
    - Làm mới accessToken, Giữ nguyên refreshToken

-   logout 
    - Vô hiệu hóa Token trong phiên hiện tại


## Luồng  Update Purchase Item

- **Kịch bản cần update**

- Nhập liệu sai
    - UPDATE quantity || remaining_quantity || cost_price

- Khi cần điều chỉnh kho so với thực tế
    - UPDATE remianing_quantity

- Điều chỉnh chi phí
    - UPDATE cost_price

- Hoàn trả bớt hàng cho supplier
    - UPDATE quantity + remaining_quantity

- **Kịch bản delete**
- Nhập liệu saio
    - Nhập nhầm product_Id hoặc purchase_id . ép buộc xóa đi tạo cái khác
- Hủy ko nhận một mặt hàng đơn lẻ

- **Khó khăn**

- Không thể cập nhật cứng remaining_quantity: bởi vì trên client UI ko phản ánh chính xác số lượng thực tế. Mà nó chỉ phản ánh thời điểm load lên mà thôi không thể xử lý một cách `absolute` được 

- **Hướng giải quyết**

- Tạo ra 3 API: API 1 cho nhập liệu sai: `PUT /api/purchase-items/{id}`
    - newQuantity (absolute), newCostPrice
    - Số lượng Quantity nhập vào ko đứng thực tế -> remaining + quantity
- Điều chỉnh kho so với thực tế: 
    - `POST /api/purchase-items/{id}/adjustments`
    - adjustmentDelta (delta) , UPDATE remainingQuantity
    - Mặt hàng remaining bị thất thoát -> Điều chỉnh remaning -> quantity(đã nhập) giữ nguyên
    
- return 
    - returnQuantity: (delta)
    - Trả hàng lại cho nhà sản xuất, hoặc lấy thêm đơn hàng -> Giảm remaining + quantity