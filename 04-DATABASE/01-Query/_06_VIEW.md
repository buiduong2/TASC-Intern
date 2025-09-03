## VIEW

-   View là một bảng ảo (không lưu dữ liệu), chứa câu lệnh `SELECT đã được đặt tên`
-   Khi chúng ta `SELECT` từ VIEW. Postgres `mỏ rộng (inline)` view thành truy vấn gốc rồi tối ưu hóa như một câu SQL thông thường

-   **Lợi ích**

-   Tái sử dụng truy vấn phức tạp:
-   Che dấu dữ liệu
-   Ràng buộc cập nhật - (Update Insert qua View phải thỏa mãn điều kiện lọc)
-   Ổn định API dữ liệu - Ứng dụng chỉ gọi VIEW, Phía DB đổi cấu trúc bảng vẫn không ảnh hưởng đến App

-   **Cú pháp**

```sql
-- Tạo view
CREATE VIEW view_name [(col1, col2, ...)] AS
SELECT ...
[WITH [CASCADED | LOCAL] CHECK OPTION]; -- tùy chọn

-- Thay thế view (không đổi số/kiểu cột)
CREATE OR REPLACE VIEW view_name AS
SELECT ...;

-- Xoá view (có thể phải CASCADE nếu có object phụ thuộc)
DROP VIEW IF EXISTS view_name [CASCADE];

-- Quyền truy cập
GRANT SELECT ON view_name TO some_role;
REVOKE SELECT ON view_name FROM some_role;
```

-   **View có cập nhật được không**
-   Postgre SQL hỗ trợ `auto-updatable view` nếu truy vấn đủ `đơn giản`

    -   Từ 1 `bảng`
    -   Không có `DISTINCT` `GROUP BY`, `HAVING`, `UNION` `OFFSET/LIMIT`, hàm tập hợp
    -   Mỗi cột của VIEW ánh xạ trực tiếp đến một bảng

-   Khi đó chúng ta có thể `INSERT/UPDATE/DELETE` qua VIEW
-   Dùng thêm `CHECK OPTION` để `chặn` việc sửa chữa nếu dữ liệu làm mất diều kiện `WHERE` của view

-   Nếu View `không updatable` (do join/aggregate) ta vẫn có thể làm được bằng `INSTEAD OF trigger` trên view (Viết trigger để định tuyến thao tác sang các bảng gốc)

-   **Bảo mật và phan quyền**

    -   Bạn `GRANT` trên view cho CLient/report - `không` cần cấp quyền trên bảng gốc của họ
    -   VIew giúp `ẩn cột , lọc hàng` kết hợp với `CHECKOPTION` để đảm bảo các thay đổi luôn nằm trong ô kính đã định
    -   Tránh "hàm rò rỉ" (leaky function) khi dùng view để che dữ liệu; dùng hàm an toàn hoặc tách logic

-   **Hiệu năng**

    -   View được `inline` -> thường `không chậm hơn` truy vấn gốc, planner vẫn pushdown điều kiện khi có thể
    -   Nhưng `Lồng quá nhiều View` có thể làm truy vấn khó đọc/ khó tối ưu
    -   Dùng `EXPLAIN (ÂNALYZE, BUFFER)` để kiếm chứng
    -   Nếu cần `cache/ kết quả tĩnh` dùng `MATERIALIZED`

-   **Materialized View**

    -   `MV` lưu `kết quả` của câu SELECT thành dữ liệu vật lý -> đọc nhanh
    -   Phải `REFRESH` khi dữ liệu gốc đổi

```sql
REFRESH MATERIALIZED VIEW mv_name;

--Hoặc không khóa đọc
REFRESH MATERIALIZED VIEW CONCURRENTLY mv_name;

```

-   Có thể tạo `index` trên MV để tăng tốc thêm

-   **MỘt số sử dụn VIEW**
    -   TRánh `SELECT *` luôn liệt kê cột
    -   Đặt ten cột, alias ổn định
    -   Lọc dữ liệu cần ghi -> `CHECK OPTION`
    -   Dùng `MV` cho báo cáo nặng/ ít thay đổi + lập lịch `REFRESH`
    -   Kiểm tra `EXPLAIN ANALYZE` thay vì đoán hiệu năng
    -   Dùng View để phân quyền đọc `GRANT` trên view thay vì bảng gốc
    -   Tránh lồng quá sâu. Quá nhiều tầng VIEW -> Khó tối ưu/ khó để Debug

## Câu hỏi

-   **Inline View** là sao

    -   Khi mà `SELECT` từ View. PostgreSQL `không thật sự chạy view` như một `bảng riêng biệt` rồi mới JOIN. Mà nó `mở rộng (expand)` view trở thành chính cái `SELECT` gốc được định nghĩa trong VIew

    ```sql
    -- Tạo bảng
    CREATE TABLE products (
    id   SERIAL PRIMARY KEY,
    name TEXT,
    price NUMERIC
    );

    INSERT INTO products (name, price) VALUES
    ('Laptop', 1000),
    ('Mouse',  20),
    ('Monitor', 200);

    -- Tạo VIEW lọc sản phẩm giá > 100
    CREATE VIEW v_expensive_products AS
    SELECT id, name, price
    FROM products
    WHERE price > 100;

    -- Chạy thử VIew
    SELECT * FROM v_expensive_products WHERE name LIKE 'M%';

    -- Điều Postgre sẽ làm:

    SELECT id, name, price
    FROM products
    WHERE price > 100 AND name LIKE 'M%';

    -- Chứ ko phải là như thế này
    WITH v_expensive_products AS ( -- Không lầm việc như thế này
        SELECT id, name, price -- Không lầm việc như thế này
        FROM products -- Không lầm việc như thế này
        WHERE price > 100 -- Không lầm việc như thế này
    ) -- Không lầm việc như thế này
    SELECT * -- Không lầm việc như thế này
    FROM v_expensive_products -- Không lầm việc như thế này
    WHERE name LIKE 'M%' -- Không lầm việc như thế này
    ```

    -   Nghĩa là câu `WHERE` trong truy vấn VIEW của chúng ta `được đẩy vào trong truy vấn gốc`

-   **Plalner và shutdown là cái gì**

    -   `Planner là Optimizer`

        -   Cong việc của Planner là khi nhận được câu truy vấn `SELECT ...` postgre sẽ `không chạy ngay` mà qua nhiều giai đoạn

        -   1. `Parsing`
               -> SQL -> cây cú pháp
        -   2. Rewriting:

            -   exapand view -> inline view (thay rule)

        -   3. Plaing / optiomizer
            -   Planner sẽ phân tích nên chạy câu truy vấn này bằng cách nào
            -   Có thể dùng `seq scan` , `index-scan`, `hashmap join` ...
            -   Nói chung tối ưu hóa chi phí
        -   4. Execution
            -   Executor sẽ chạy kết quả đã chọn để trả về kết quả

-   **EXPLAIN (ÂNALYZE, BUFFER)**
-   Là một hàm để tính toán thời gian. Và phương pháp cuối cùng được sử dụng . Để kiểm tra tính tối ưu hóa của CSDL

```sql
EXPLAIN SELECT * FROM products WHERE price > 100;
-- KQ:
--Seq Scan on products  (cost=0.00..1.06 rows=2 width=12)
--  Filter: (price > 100)

```

-   **Cascade View là sao**?

-   -   Vấn đề khi xóa view
        -   Trong Postgre các Object (bảng, view, function) có thể `phụ thuộc lẫn nhau` (view từ table. function từ talbe hoặc view, hoặc view từ view)

```sql
CREATE VIEW v_customer AS
SELECT customer_id, name FROM customers; -- Tạo view từ bảng customers thật

CREATE VIEW v_customer_orders AS
SELECT c.name, o.order_id
FROM v_customer c
JOIN orders o ON c.customer_id = o.customer_id; -- Tạo view từ view v_customer
```

-   Chúng ta có thể thay đổi các OPTION khi tạo view theo dạng `RESTRIC` `CASCADE` . Để tránh xóa bảng 1 cách bừa bãi mà khoogn biết
-   