## 3. Sub Query

```sql
SELECT column1, column2
FROM table1
WHERE columnX operator
    (SELECT columnY
     FROM table2
     WHERE ...);
```

-   Là một câu lệnh SELECT `lồng trong một câu lệnh SQL Khác`
-   Nó sẽ `trả về giá trị` hoặc `một tập hợp bản ghi` dùng để lọc, tính toán hoặc so sánh trong query chính

-   **Sub query trả về 1 giá trị**

-   Trả về `một giá trị duy nhất` dùng `=` hoặc so sánh

```sql
-- Timf khách hàng nào có tổng số lượng đơn hàng lớn nhất
SELECT name
FROM customer
WHERE id = (
    SELECT customer_id
    FROM orders
    GROUP BY customer_id
    ORDER BY SUM(total_amount) DESC
    LIMIT 1
)
```

-   TRả về `một danh sách` dùng với `IN` hoặc `NOT IN`

```sql
-- Tìm khách hàng chưa mua hàng nào
SELECT name
FROM customer
WHERE id IN (
    SELECT DISTINCT customer_id
    FROM orders

)
```

-   **SubQUery trong FROM (inline view/ dervied table)**

-   Sub Query tạo ra `bảng tạm`, từ đó query truy vấn tiếp

```sql
-- Tìm tổng số đơn hagnf khách. Sau đó lọc các đơn hàng mà nhiều hơn 1
SELECT customer_id, total_orders
FROM  (
    SELECT customer_id, COUNT(*) AS total_orders
    FROM orders
    GROUP BY customer_id
) AS order_counts
WHERE total_orders > 1
```

-   **Sub Query trong SELECT**

-   SubQuery trả về một giá trị tính toán

```sql
SELECT name,
    (
        SELECT COUNT(*)
        FROM orders
        WHERE orders.customer_id = customer_id
     ) AS total_orders
FROM customers
```

-   **Thực tế sử dụng**

-   So sánh dữ liệu khác bảng

    -   không cần lấy về dữ liệu bên đó nhưng cần tính toán
    -   Tính toán bảng tạm thời
    -   Kết hợp với JOIN hoặc EXISTS

-   **So sánh với JOIN**

    -   Đôi khi JOIN ko thể thay thế được `VD subQUery trong SELECT`
    -   SubQuery dễ viết. Rõ nghĩa

    -   Về hiệu năng:
        -   JOIN thường cho hiệu năng cao hơn . Còn tùy vào logic -> vì không cần tạo bảng tạm
        -   Việc JOIN sẽ tránh việc SCAN không cần thiết. JOIN `scan 1 lần duy nhát`

-   **Tối ưu**

    -   Trong 1 mệnh đề SELECT nếu ta có sub query. mà SELECT của outer Query trả về 1000 kết quả. Thì có thể SUbquery phải chạy 1000 lần

    -   Nhưng nếu SubQuery ko phụ thuộc vào bảng bên ngoài. DB có thể `tính một lần` và tái sử dụng
    - 
