# Báo cáo dự án thực tập - Mini Mart

## 1. Giới thiệu

Mini Mart là ứng dụng web E-commerce dạng demo, được xây dựng trong thời gian thực tập với mục tiêu áp dụng các kiến thức đã học về **Java, Spring Boot, JPA, Spring Security** và thiết kế cơ sở dữ liệu.

## 2. Đặc điểm hệ thống

-   Quản lý sản phẩm (Product, Category, Tag, Image).
-   Quản lý đơn hàng (Order, OrderItem, Payment, Shipping).
-   Quản lý nhập hàng (Purchase, PurchaseItem).
-   Quản lý tồn kho (Stock 1–1 Product, snapshot quantity).
-   FIFO xuất kho dựa vào PurchaseItem.remaining_quantity + created_at.
-   Tính toán doanh thu – chi phí – lợi nhuận dựa vào OrderItem.sell_price và OrderItem.cost_price.
-   Audit fields (created_at, updated_at, created_by_id, updated_by_id) cho hầu hết bảng chính.

## 3. Đặc điểm thiết kế

-   Bán hàng cho guest -> Order -> customer_id
-   Product 1-1 Stock -> Chỉ có 1 Kho duy nhất
-   OrderItem (sell_price, cost_price) => tính toán
-   Product 1-1 Image -> Mỗi sản phẩm chỉ 1 ảnh
-   Customer 1-N Address -> Mỗi user có thể có nhiều địa chỉ

## Luồng nghiệp vụ

-   1. Nhập hàng

    -   Tạo `Purchase` + nhiều `PurchaseItem`
        -> Update `Stock.quantity += total quantity` nhập

-   2. Bán hàng (order)

    -   `Order` + nhiều `OrderItem`
    -   Kiểm tra tồn kho `Stock`
    -   Trừ tồn kho FIFO `PurchaseItem`
    -   ghi lại `sell_price` và `cost_price` tại thời điểm

-   3. Báo cáo
    -   Doanh thu = SUM(OrderItem.sell_price \* quantity)
    -   Giá vốn = SUM(OrderItem.cost_price \* quantity)
    -   Lợi nhuận = Doanh thu - Giá vốn

## Các chức năng 