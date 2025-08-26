## 3. Queue Interface

## Nêu các đặc điểm của Queue Interface, Dequeue Interface

-   **Queue**

-   Khái niệm

    -   Là một Interface trong `java.util`
    -   extends Collection
    -   `Đại diện cho một hàng đợi` (queue), nơi các phần tử được xử lý theo trật tự nhất định
    -   Thường dùng mô hình `FIFO` (first In first out), Nhưng cũng có loại Queue đặc biệt như (PriorityQueue,DeQueue)
    -   Nếu là DeQueue thì hỗ trợ cả FILO như stack

-   Đặc điểm:

    -   Có thứ tự
        -   Các phần tử được lưu theo thứ tự `thêm vào`(insert order) hoặc theo `độ ưu tiên` (priorityQueue)

    -   Thao tác cơ bản
        -   Thêm phần tử ở cuối
        -   Xem phần tử ở đầu, hoặc xóa phần tử ở đầu
    -   Không cho phép thao tác ngẫu nhiên theo chỉ số
        -   Chúng ta luôn xử lý `từ đàu hoặc cuối` (deQueue)
    -   Hỗ trợ các loại Queue đặc biệt
        -   `PriotityQueue` -> thứ tự dựa trên độ ưu tiên
        -   `DeQueue` -> thêm/xóa được ở cả 2 đầu
    -   Không chấp nhận null trong một implementtation
        -   `PriotityQueue` ko chấp nhận null vì nó cần một comparable
    -

## Kể ra các class triển khai từ Queue Interface, Dequeue Interface , phân biệt trường hợp sử dụng tương ứng

- **Queue**

- `LinkedList` -> Hàng đợi đơn giản, cần thêm/xóa nhanh cả ở đầu và cuối

- `PriotityQueue` -> Hàng đợi mà muốn phần tử ưu tiên được xử lý trước (theo độ ưu tiên)

- `ArrayDeQueue`-> Hàng đợi ưu tiên dựa trên mảng -> khi cần deQueue nhanh

- `ConcurrentLinkedQueue`
    - Thread-safe không đồng bộ hóa blocking
    - Hàng đợi môi trường đa luồng nhiều đọc ghi
    - Không khóa các thao tác thêm và xóa khi không thể

- `LinkedBlockingQueue`
    - Thread-safe, blocking queue ( dừng thread khi queue đầy / rỗng)
    - Dùng khi có nhiều thread cùng thêm và lấy dữ liệu không đồng bộ lẫn nhau
    - Khóa các thao tác cho đến khi có thể sử dụng

- `PriorityBlockingQueue`
    - Thread-safe, hàng đợi ưu tiên
    - Đa luồng, xử lý gióng như PriorityQueue 
    - Khóa các thao tác cho đến khi có thể làm được