## 5. Memory

## Thế nào là cấp phát tĩnh

-   **Cấp phát tĩnh (Static Allocation)**

-   Là việc cấp phát bộ nhớ ngay tại thời điểm biên dịch (compile time).

-   Kích thước bộ nhớ phải được biết trước.

-   Thời gian sống: Gắn liền với toàn bộ chương trình (nếu là biến toàn cục/static) hoặc với scope (nếu là biến cục bộ).

-   Vị trí lưu trữ: thường nằm trên stack (biến cục bộ) hoặc method area (biến static).

-   Giải phóng: Tự động khi ra khỏi scope hoặc khi chương trình kết thúc.

-   tính toán chỗ cho bộ nhớ chứ chưa cấp phát

-   Nhìn vào kiểu dữ liệu để cấp phát chẳng hạn, array

-   Không thể thay đổi bộ nhớ nữa vì có tính toán rồi

```java
void myMethod() {
    int x = 10; // Biến x được cấp phát trên stack
    // ...
}

class MyClass {
    static int y = 20; // Biến y được cấp phát trong bộ nhớ tĩnh
}
```

-   **Cấp phát động (Dynamic Allocation)**

-   Là việc cấp phát bộ nhớ trong lúc chương trình đang chạy (runtime).
-   Kích thước có thể thay đổi tùy theo dữ liệu lúc chạy.
-   Thời gian sống: Do chương trình quyết định, không cố định.
-   Vị trí lưu trữ: thường nằm trên heap.
-   Giải phóng: trong Java thì Garbage Collector quản lý, không cần lập trình viên giải phóng thủ công.

```java
// Đối tượng obj được cấp phát trên heap
MyObject obj = new MyObject();

// Mảng arr được cấp phát trên heap với kích thước động
int[] arr = new int[100];
```

## - Phân biệt bộ nhớ heap và bộ nhớ stack ?

-   **Bộ nhớ Stack**

-   Dùng cho:

    -   Biến cục bộ (local variable).
    -   Tham chiếu (reference) trỏ tới object trên Heap.
    -   Quản lý lời gọi hàm (method call).

-   Đặc điểm:

    -   Lưu theo kiểu LIFO (Last In – First Out).
    -   Mỗi lần gọi hàm, Java tạo một stack frame để lưu tham số + biến cục bộ.
    -   Khi hàm kết thúc, frame bị pop ra → bộ nhớ được giải phóng tự động
    -   Nhanh, nhưng không linh hoạt (kích thước cố định, không thay đổi trong runtime).

-   **Bộ nhớ Heap**

-   Dùng cho:

    -   Tất cả các object được tạo bằng `new`.
    -   Các mảng (new int[10], new String[5]).
    -   String trong String Pool (một vùng đặc biệt của Heap).

-   Đặc điểm:
    -   Kích thước lớn, cấp phát linh hoạt tại runtime.
    -   Không bị xóa khi thoát khỏi hàm (chỉ mất khi không còn tham chiếu nào trỏ tới nó).
    -   Quản lý bởi Garbage Collector trong Java.
    -   Truy cập chậm hơn Stack một chút, vì phải thông qua con trỏ/tham chiếu
