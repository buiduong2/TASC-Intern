## 2. Các loại JOIN

-   **Định nghĩa**

    -   Kết hợp dữ liệu từ nhiều bảng khác nhau dựa trên những điều kiện cụ thể. (ON ...)

-   **CROSS JOIN**

    -   Tạo tích Tổ hợp, Số lượng bằng x bản ghi nhân với y bản ghi (6\*3 = 18 row) (không cần điều kiện ON) trả về mọi hàng tử bảng này kết hợp

    -   Mục đích: đôi khi test tốc độ của database
    -   Có một cú pháp khác cho CROSS JOIN là `FROM toys, boys`
    -   `INNER JOIN` là một `CROSS JOIN` với các kết quả được xóa đi bởi điều kiện trong Query

    -   Thực tế;
        -   Tìm kiếm tất cả các biến thể có thể có dựa vào một (VD có 3 bảng một bảng thông tin và 2 bảng biến thể. Vậy ta chưa biết biến thể nào còn thiếu)
        -   test database
        -

-   **INNER JOIN - JOIN**

    -   Kết hợp bản ghi của cả 2 bảng bằng việc sử dụng toán tử so sánh trong một điều kiện
    -   Các Cột trả về chỉ nơi mà những hàng nó khớp với điều kiện

    -   THực tế:
        -   Hiển thị các dữ liệu liên quan: danh sách các khách hàng đã mua

-   **FULL OUTER JOIN**

    -   trả về tất cả các hàng của một bảng, cùng với thông tin khớp từ bảng còn lại, Nếu không có thì để rỗng

    -   Sự khác biệt giữa `Outer JOIN` và `INNER JOIN`

        -   OUTER JOIN sẽ cho chúng ta một row bất kể có so khớp với bảng còn lại hay không
        -   NULL value bảo với chúng ta rằng không khớp

    -   VD thực tế:
        -   Đôi khi cần xóa các hàng mà ko khớp với hàng còn lại

```java

```

-   Tất cả vẫn truy vấn tích phân trước khi lọc

-   **LEFT JOIN**

    -   Lấy `tất cả các hàng của bảng bên trái` và so khới chúng với các row của `bảng bên phải`
    -   Luôn trả về hàng bên trái `ít nhất 1 lần`
    -   TABLE đứng bên trái LEFT JOIN thì là bảng bên trái và ngược lại
    -   Hữu ích khi mà left table và right table có mối quan hệ 1- nhiều
    -   Cần lưu ý là nó có thể xuất hiện nhiều `row` của bảng bên trái kết hợp với bản bên phải

    -   Thực tế
        -   Lấy danh sách dữ liệu thực tế của người dùng và các thông tin khác từ bảng khác của người dùng đó
        -   Báo cáo tổng quan : không mua thì vẫn phải hiển thị là 0
        -

-   **RIGHT JOIN**

-   Tại sao lại đẻ ra LEFT và RIGHT: vì việc thay đổi keyword LEFT và RIGHT lại dễ hơn việc thay đổi thứ tự các table trong SQL
- 