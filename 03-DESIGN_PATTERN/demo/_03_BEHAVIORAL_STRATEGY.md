## Behavioral Patterns

-   là một nhóm các DP thuộc nhóm hành vi (Behavioral) tập trung vào việc `xác định **cách các đối tượng tương tác** - **phân chia trách nhiệm** cho nhau`. thay vì chỉ mô tả cấu trúc của các đối tươngj (như nhóm cấu trúc), nhóm hành vi giải quyết các vấn đề liên quan đến giao tiếp giữa các đối tươngj để thực hiện một tác vụ nào đó một cách hiệu quả và linh hoạt

-   `Mục tiêu chính của nhóm này là giảm sự phụ thuộc giữa các đối tươngj, làm cho hệ thống trở lên mềm dẻo, dễ mở rộng và bảo trì hơn.`
    -   Tách rời các thuật toán khỏi đối tượng sử dụng
    -   Phân tán trách nhiệm một cách hợp lý
    -   Thiết lập kênh giao tiếp hiệu quả giữa cvsc đối tượng

```md
-   Định lý:

    -   Lập trình cho một interface, không phải là một triển khai của nó

-   Kết quả:
    -   thay đổi hành vi của một đối tượng một cách động
    -   Một class không cần phải biết chi tiết về cách triển khai các hành vi của nó
```

## Strategy

-   **Định nghĩa**

-   là một mẫu thiết kế thuộc `nhóm hành vi`, tập trung vào việc `đóng gói các thuật toán` để chúng có thể thay thế cho nhau. Thay vì viết các thuật toán trực tiếp vào một lớp - Strategy cho phép các giải thuật độc lập khởi client mà sử dụng nó , làm cho chúng có khả năng thay thế, và dễ dàng mở rộng tạo ra các thuật toán có cách triển khai khác cho client khác.

-   **Điểm mạnh**

    -   Đóng gói hành vi:
    -   Linh hoạt và có thể thay thế
    -   Giảm sự phụ thuộc
    -   Dễ dàng mở rộng:

-   **Mục tiêu**
    -   cho phép một vài phần của ứng dụng mà khác biệt độc lập với các phần còn lại - Để khi thay đổi 1 thứ không ảnh hưởng đến những cái không cần thay đổi -
        -   ít hậu quả không mong muốn hơn khi mà thay đổi code và theo dẻo dai cho ứng dụng
        -   Và chúng ta có thể thêm các hành vi mới vào hoặc sửa đổi các hành vi cũ mà không chạm đến bất kì class nào mà nó sử dụng các hành vi đó
    -   Nhưng nó cũng cho phép chúng ta thay đổi hành vi tại runtime
-   **Cách triển khai**

    -   Xác định khía cạnh của ứng dụng mà nó khác biệt và đóng gói
    -   chia cắt chúng khỏi cái mà nó luôn như vậy.
    -   chúng ta có thể thay đổi và mở rộng các phần đó mà không ảnh hưởng đến những cái mà nó không cần thay đổi

-   **Khi nào cần sử dụng**

    -   Khi mà các hành vi của một đối tượng thay đổi xuyên suốt các class.
        -   ít hậu quả không mong muốn hơn khi mà thay đổi code và theo dẻo dai cho ứng dụng
    -   Khi có nhiều thuật toán cho cùng một tác vụ (paymentMethod)
    -   Khi muốn tách rời các thuật toán khỏi đối tượng sử dụng (Single Responsibility)

-   **Định lý tỏng lập trình**

    -   `Ưu tiên Composition (thành phần) hơn là Inheritance (kế thừa)`
        -   Nếu chúng ta sử dụng kế thừa. Thì chúng ta đang triển khai theo dạng implementatino. Như vậy Chúng ta bị khóa vào các triển khai cụ thể.
    -   ` Xác định khía cạnh của ứng dụng mà nó khác biệt và đóng gói - chia cắt chúng khỏi cái mà nó luôn như vậy. Như vậy sau này chúng ta có thể thay đổi và mở rộng các phần đó mà không ảnh hưởng đến những cái mà nó không cần thay đổi`

    -   `Không cần biết bạn làm việc ở đâu, bạn đang xây dựng cái gì hoặc ngôn ngữ mà chúng bạn đang xây dựng , một hằng số trong lập trình mà luôn đi cùng chúng ta. **CHANGE**`
