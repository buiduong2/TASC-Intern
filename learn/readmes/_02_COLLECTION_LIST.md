## - Nêu ra các đặc điểm List Interface

-   **Khái niệm**
-   `List` là một _interface_ trong gói `java.util`
-   Nó kế thừa từ `Collection` và định nghĩa một `tập hợp có thứ tự (ordered Collection)`, cho phép `chứa các phần tử trùng lặp`

-   **Đặc điểm**
-   Có thứ tự
    -   Các phần tử giữ nguyên thứ tự được thêm vào
-   Hỗ trợ truy cập bằng chỉ số(index)
    -   `List` cho phép truy cập thêm sửa xóa, cập nhật phần tử thông qua chỉ số (giống như mảng)
-   Chấp nhận các phần tử trùng lặp
-   Kế thừa từ Collection nên có các method của Collection

## - Kể ra các class triển khai từ List Interface

-   **ArrayList**
-   Dựa trên mảng động
-   truy cập nhanh qua `index` O(1)
-   Thêm/ Xóa ở cuối nhanh, nhưng thêm / xóa ở giữa tốn chi phí dịch chuyển phần tử
-   Không đồng bộ

    -   Dùng khi cần `truy cập nhanh, ít thao tác chèn/xóa ở giữa`

-   **LinkedList**
-   Dựa trên `danh sách liên kết kép` (double LinkedList)
-   truy cập theo index chậm (O(n))
-   Thêm /xóa ở đầu , cuối hoặc giữa nhanh O(1) nếu đã có con trỏ
-   Không đồng bộ

    -   Dùng khi cần ` nhiều thao tác chèn/xóa` hơn là truy cập theo index

-   **Vector**
-   Giống `ArrayList` nhưng đồng bộ `Synchronized`
-   THêm xóa/ truy cập đều an toàn khi dùng trong nhiều luồng (thread-safe)
-   Tuy nhiên chậm hơn `ArrayList`

-   **Stack**
-   kế thừa từ `Vector`
-   Cấu trúc `ngăn xếp` (LIFO - Last In First out)
-   Có thêm các phương thức đặc trưng

    -   `pust()` -> thêm phần tử
    -   `pop()` -> Lấy và xóa phần tử trên cùng
    -   `peek()` xem phần tử trên cùng xong xóa

    -   Dùng khi cần mô phỏng stack

## - Phân biệt rõ trường hợp sử dụng của từng class đó

-   **ArrayList**
    -   Truy cập nhiều, ít thêm/xóa
    -   VD: Như lưu danh bạ điện thoại (có thứ tự, dẽ tra cứu theo index)
-   **LinkedList**
    -   Thêm/xóa nhiều, truy cập ít
    -   VD : Quản lý todo-list (thêm xóa, bất cứ lúc nào)
-   **Vector**
    -   Cần danh sách thread-safe (ít dùng hiện nay)
-   **Stack**
    -   Bài toán LIFO (undo/redo, biểu thức toán học)
    -   VD: Quản lý các thao tác soạn thảo văn bản (nhấn Ctrl+Z để undo), thực hiện mô phỏng đệ quy

## Concurrent Collections

- Tất cả các triển khai của collection trong `java.util.concurrent` đều là Thread Safe. Những triển khai này sử dụng các triển khai locking/schnorized cho các thread safety

- Điều quan trọng
    - Chi phí hiệu suất của triển khai trên là: locking và synchonized. Nói chung, nếu chỉ có một thread truy cập vào Collection. chúng ta không bao giờ nên sử dụng concurrent triển khai mà sử dụng các loại non-synchonized COllection
    -  Tất cả các Linked triển khai của Queue, là hiệu quả so với LinkedList (Vì linkedlist implmenet List nên nó nặng hơn)

    - Đối với kịch bản hai hàng đợi producer-consumer. sử dụng blockingQueue. nếu nhiều thread cùng truy cập khi đó sử dụng các triển khai của ConcurrentXYZQueue 
