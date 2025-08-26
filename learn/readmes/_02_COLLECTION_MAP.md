## 5. Map Interface

## Nêu ra các đặc điểm Map Interface

-   **Khái niệm**

-   `Map` là một `interface` trong `java.util`
-   Đại diện `cho cặp key-value` mỗi `key duy nhất` ánh xạ `với một value`
-   Key không được phép trùng lặp, nhưng `value có thể trùng nhau`

-   **Đặc điểm chính**
-   Key là duy nhất
-   Value có thể trùng
-   Không có các chỉ số (index)
-   Thao tác cơ bản

    -   Thêm/ xóa/ cập nhất các cặp key-value
    -   lấy ra một value theo key
    -   xóa một entry theo key
    -   kiểm tả sự tồn tại của key
    -   Lấy ra tập values, keys, hoặc entry

-   Các triển khai của nó thì duy trì thứ tự khác nhau

    -   HashMap: khong duy trì thứ tự
    -   LinkedHashMap: duy trì thứ tự chèn
    -   TreeMap: duy trì thứ tự theo Comparator

-   Thread-safe
    -   `HashMap` -> không thread-safe
    -   `ConcurrentHashMap` -> thread-safe hiệu quả trong đa luồng
    -   `Collections.synchonizedMap` -> đồng bộ hóa toàn bộ Map
-

## Kể ra các class triển khai từ Map Interface

-   **EnumMap**

-   **HashMap**

-   Dựa trên `HashTable` key duy nhất, không duy trì thứ tự, cho phép `null key và null value`

-   **LinkedHashMap**
-   `Duy trì thứ tự chèn`, dựa trên HashTable +linkedList cho phép null key và value

-   **TreeMap**

-   Dựa trên `red-black tree` sắp xếp key tự nhiên hoặc theo `Comparator` không cho null key

-   **ConcurrentHashMap**

-   Thread-safe, lock phân đoạn , không cho phép null key/value

    -   Chia map thành segment / bucket, mỗi segment chứa nhiều key . Thao tác với key nào thì khóa key đó

-   **WeakHashMap**

    -   Key là weak reference → garbage collector có thể xóa entry khi key không còn tham chiếu
        -   một Object (đã được Hash THành Key) bị trở thành weak reference (biến tham chiếu đã bị xóa) thì Map nhận thấy key bị trình rọn rác rọn mất. Vậy WeakHashMap cũng xóa cái key-value đó luôn

-   **EnumMap**

    -   Chỉ dùng key là `enum` hiệu suất cao. không cho null key

-   **IdentityHashMap**
    -   Việc so sánh giữa 2 phần tử là sử dụng `==` thay vì method `equals()`

## Phân biệt rõ trường hợp sử dụng của từng class đó

-   **EnumMap**
-   **HashMap**
    -   Không cần duy trì thứ tự thêm key vào
    -   Cách TH dùng
        -   Dùng trong Cache URL API
        -   Đếm tần xuất xuất hiện
-   **LinkedHashMap**

    -   Cần duy trì thứ tự thêm key vào. theo thứ tự chèn
    -   Cách TH dùng
        -   lưu trang, hình ảnh hoặc resource, loại bỏ trang ít truy cập nhất khi bộ nhớ đầy

-   **TreeMap**
    -   Nếu cần duy trì thứ tự các key theo một thứ tự cần thiết được sắp xếp
    -   Đếm lượt truy cập theo ngày
    -   TOp N sản phẩm bán chạy nhất
-   **ConcurrentHashMap**
    -   Làm việc cho đa luồng hiệu quả cao hơn Collection.synchonizedMap()
-   **WeakHashMap**
    -   Cache mà không muốn giữ key lâu dài
    -   Key/values được tham chiếu được sử dụng nhiều lần trong một sự kiện nhưng sau đó thì không sử dụng nữa
    -   TH Sử dụng
        -   Có thể lưu một session của một người dùng vòa trong một Map. và dùng Value của nó để làm key của các weakMap có thông tin liên quan đến session duy nhất dó thôi. Khi mà người dùng không còn truy cập nữa. Thì các thông tin khác tự động được xóa đi
-   **EnumMap**
    -   Nếu Key chỉ là một trong các giá trị của một ENUM
        -   Thường dùng trong các lưu các metadata để code trở lên clean hơn thay vì fix cứng. Mà ta sẽ tận dụng các ENUM
-   **IdentityHashMap**
    -   Khi cần so sánh `theo reference` không phải nội dung
        -TH sử dụng - Thông thường khi làm việc với các loại graph . Ta cần duyệt qua chúng. Ta có thể sử dụng reference luôn với các Object để kiểm tra và ghi lại vị trí của chúng cho nhanh
