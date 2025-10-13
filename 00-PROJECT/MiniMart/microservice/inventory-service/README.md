## Tạo mới một PurchaseItem

-   sau khi tạo Product Id ko được phép cập nhật nữa
-   ProductID phải thực sự tồn tại

-   **Phương án replicate**

    -   Ưu tiên: `nhất quán mạnh` + `Hiệu suất cao`
    -   Cơ chế Xác Thực : Truy vấn cục bộ` Bảng tra cứu (Lookup Table)`
    -   Đồng bộ Dữ liệu: Sử dụng Events (ProductCreated, ProductDeleted) để thêm/xóa productId trong Local Cache.

    -   Tập trung vào ` Quản lý Tính nhất quán Dữ liệu (Data Consistency Management)., đồng bộ dữ liệu Cache`
    -   Trường hợp Không hợp lệ: Product không tồn tại/đã bị xóa tại thời điểm tạo mới PurchaseItem.

    -   Các Vấn đề Cần Xử lý:
        -   `1. Độ trễ Event (Race Condition):` -> có thể có fallback API CALL
        -   `2. Event Bị Lỗi/Mất` -> đồng bộ định kỳ (Reconciliation)

-   **Phương án Saga**

    -   Ưu tiên: `Khả dụng (Availability)` và `Khả năng chịu lỗi cao`
    -   Tính Nhất Quán :`Nhất quán sau cùng`
    -   Cơ chế Xác Thực : Chờ đợi xác thực qua Event
    -   Tập trung vào: `kiểm tra sự tồn tại` và `hành động đền bù`
    -   Trường hợp Không hợp lệ: `Product không tồn tại` → Kích hoạt chuỗi hành động Đền bù.
    -   Các Vấn đề Cần Xử lý:

        -   `1. Quản lý Trạng thái Tạm thời (PENDING):`
        -   ` 2. Thiết kế Đền bù (Compensation):`
        -   Quản lý trạng thái tạm thời (các quy trình nghiệp vụ khác chỉ `xử lý` các `Purchase` có trạng thái Confirm)
        -   Thiết kế hoạt động đền bù (Cẩn thận phải đền bù lan truyền)

    -   Vấn đề khác:
        -   Ô nhiêm Entity (chứa các trường liên quan đến Saga)
        -   UX ko hay ho khi tạo xong ko nhận được thông báo thất bại mà phải chờ
        -   Nếu sử dụng webSocket thì cũng ô nhiêm (Notification Service)

## Stock

- Tồn kho Khả dụng (ATP)=totalQuantity−committedAllocation−pendingReservation≥0