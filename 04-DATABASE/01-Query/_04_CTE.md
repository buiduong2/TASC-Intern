## + CTE (Common Table Expression)

-   **Định nghĩa**

    -   Là một `query tạm thời` được định `nghĩa trước câu lệnh SELECT/INSERT/UPDATE/DELETE` để sử dụng lại trong câu lệnh đó

-   **Lợi ích**

    -   Làm câu lệnh `dễ đọc hơn`
    -   `Tái sử dụng` dữ liệu trung gian
    -   Hỗ trợ `đệ quy` (Recursive CTE) trong các tình huống như cây thư mục, tổ chức nhân sự

-   **Cú pháp**

```sql
WITH cte_name AS (
    SELECT column1, column2
    FROM table_name
    WHERE condition
)
SELECT *
FROM cte_name
WHERE some_other_condition;
```

```sql
-- Lấy danh sách khách hàng có tổng đơn > 400
WITH total_orders AS (
    SELECT customer_id, SUM(amount) AS total
    FROM orders
    GROUP BY customer_id
)
SELECT *
FROM total_orders
WHERE total > 400;

```

-   **Đệ quy CTE**

-   CTE đệ quy dùng khi chúng tâ cần `tính dữ liệu theo dạng cây hoặc phân cấp`

```sql
WITH RECURSIVE subordinates AS (
    SELECT employee_id, manager_id
    FROM employees
    WHERE manager_id = 1   -- cấp gốc

    UNION ALL

    SELECT e.employee_id, e.manager_id
    FROM employees e
    INNER JOIN subordinates s ON e.manager_id = s.employee_id
)
SELECT *
FROM subordinates;

```

-   **So sánh với SubQuery**

-   Đọc code: CTE đọc dễ hơn nhiều
-   Tái sử dụng : CTE có thể tái sử dụng
-   ĐỆ quy : CTE hỗ trợ đệ quy
-   Hiệu năng: CTE phụ thuộc DB đôi khi tốt hơn
    - Đôi khi một số Engine họ có thể tối ưu code bằng cách complie CTE thành một subquery
    - Hoặc CTE có thể tách thành từng bước khiến DB có thể hiểu và tối ưu hơn
    - Lưu kết quả vào bảng tạm. Tránh lặp lại nhiều lần => nhanh hơn
    - 

-   **Multi-CTE**

-   ta có thể định nghĩa CTE liên tiếp

```sql
WITH cte1 AS (
    SELECT * FROM table1
),
cte2 AS (
    SELECT * FROM cte1 WHERE condition
)
SELECT * FROM cte2;

```
