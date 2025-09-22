## Table: orders

| Column Name | Type        | Notes                               |
| ----------- | ----------- | ----------------------------------- |
| order_id    | BIGINT      | Primary Key                         |
| order_date  | DATE        | NOT NULL                            |
| customer_id | BIGINT      |                                     |
| status      | VARCHAR(20) | PENDING, PAID, CANCELLED, COMPLETED |

---

## Table: order_items

| Column Name   | Type          | Notes                          |
| ------------- | ------------- | ------------------------------ |
| order_item_id | BIGINT        | Primary Key                    |
| order_id      | BIGINT        | Foreign Key → orders(order_id) |
| product_id    | BIGINT        | NOT NULL                       |
| quantity      | INT           | NOT NULL                       |
| unit_price    | DECIMAL(10,2) | NOT NULL                       |

---

## Table: products

| Column Name  | Type          | Notes       |
| ------------ | ------------- | ----------- |
| product_id   | BIGINT        | Primary Key |
| product_name | VARCHAR(255)  | NOT NULL    |
| category     | VARCHAR(100)  |             |
| price        | DECIMAL(10,2) |             |

## bài tập

1. Sản phẩm bán chạy nhất

-   Đề: Tìm sản phẩm nào có tổng số lượng bán ra nhiều nhất.
-   Kỹ năng: SUM, GROUP BY, ORDER BY, LIMIT.

```sql
SELECT
    p.*,
    SUM(oi.quantity) AS quantity_sold
FROM
    orders AS o
    JOIN order_items AS oi ON o.order_id = oi.order_id
    JOIN products AS p ON oi.product_id = p.product_id
WHERE
    o.status = 'COMPLETED'
GROUP BY
    p.product_id
ORDER BY
    quantity_sold DESC
LIMIT 1


--  product_id | product_name  | category |  price  | total_sold
-- ------------+---------------+----------+---------+------------
--           5 | AirPods Pro 2 | Phụ kiện | 5000.00 |          5
```

---

2. Top N sản phẩm bán chạy nhất theo doanh thu

-   Đề: Liệt kê top 3 sản phẩm có doanh thu cao nhất (doanh thu = quantity \* unit_price).
-   Kỹ năng: Aggregation, ORDER BY, LIMIT.

```sql

SELECT
    p.*,
    SUM(oi.quantity * oi.unit_price) AS revenue
FROM
    orders AS o
    JOIN order_items AS oi ON o.order_id = oi.order_id
    JOIN products AS p ON oi.product_id = p.product_id
WHERE
    o.status = 'COMPLETED'
GROUP BY
    p.product_id
ORDER BY
    revenue DESC
LIMIT 3
--  product_id |    product_name    |  category  |  price   | revenue
-- ------------+--------------------+------------+----------+----------
--           1 | iPhone 15          | Điện thoại | 25000.00 | 75000.00
--           2 | Samsung Galaxy S23 | Điện thoại | 20000.00 | 60000.00
--           3 | MacBook Air M2     | Laptop     | 35000.00 | 35000.00
```

---

3. Doanh thu theo ngày

-   Đề: Tính tổng doanh thu cho từng ngày (order_date) chỉ tính các đơn hàng COMPLETED.
-   Kỹ năng: WHERE filter, GROUP BY theo ngày.

```sql
SELECT
    o.order_date,
    SUM(oi.quantity * oi.unit_price) AS daily_revenue
FROM
    orders AS o
    JOIN order_items AS oi ON oi.order_id = o.order_id
WHERE
    o.status = 'COMPLETED'
GROUP BY
    o.order_date
ORDER BY
    o.order_date

--  order_date | daily_revenue
-- ------------+---------------
--  2025-09-01 |      55000.00
--  2025-09-02 |     105000.00
--  2025-09-04 |      35000.00
```

---

3.1. Doanh thu theo ngày trong tuần

-   Đề: Liệt kê doanh thu tương ứng cho từng ngày trong tuần (Thứ Hai → Chủ Nhật), chỉ tính các đơn hàng COMPLETED. Nếu một ngày không có đơn hàng nào thì doanh thu = 0.

-   Kỹ năng: Date functions (DAYNAME, DAYOFWEEK hoặc EXTRACT DOW), GROUP BY theo ngày trong tuần, xử lý giá trị thiếu bằng LEFT JOIN hoặc generate_series().

-   như 2025-09-01 đến 2025-09-10

```sql
WITH day_of_week AS (
    SELECT 0 AS day_of_week
    UNION ALL SELECT 1
    UNION ALL SELECT 2
    UNION ALL SELECT 3
    UNION ALL SELECT 4
    UNION ALL SELECT 5
    UNION ALL SELECT 6
),
revenue_day_of_week AS (
    SELECT
        EXTRACT (DOW FROM o.order_date) AS day_of_week,
        COALESCE(SUM(oi.quantity * oi.unit_price),0) AS revenue
    FROM
        orders AS o
        JOIN order_items AS oi ON o.order_id = oi.order_id

    WHERE
        o.status = 'COMPLETED'
        AND o.order_date BETWEEN '2025-09-01' AND '2025-09-10'
    GROUP BY
        day_of_week
)
SELECT
    CASE
        WHEN dw.day_of_week = 1 THEN 'Thứ Hai'
        WHEN dw.day_of_week = 2 THEN 'Thứ Ba'
        WHEN dw.day_of_week = 3 THEN 'Thứ Tư'
        WHEN dw.day_of_week = 4 THEN 'Thứ Năm'
        WHEN dw.day_of_week = 5 THEN 'Thứ Sáu'
        WHEN dw.day_of_week = 6 THEN 'Thứ Bảy'
        ELSE 'Chủ nhật'
    END AS day_of_week,
    COALESCE(rdw.revenue,0) AS revenue
FROM
    day_of_week AS dw
    LEFT JOIN revenue_day_of_week AS rdw ON dw.day_of_week = rdw.day_of_week
ORDER BY
    dw.day_of_week ASC
```

---

3.3 Phân tích doanh thu theo danh mục sản phẩm

-   Với mỗi ngày trong khoảng thời gian từ 2025-09-01 đến 2025-09-10, hãy liệt kê danh mục sản phẩm có tổng doanh thu cao nhất trong ngày đó. Nếu một ngày không có đơn hàng COMPLETED, hãy hiển thị NULL cho danh mục và doanh thu.

```sql
WITH RECURSIVE dates AS (
    SELECT '2025-09-01'::DATE AS day
    UNION ALL
    SELECT
        d.day::DATE + 1
    FROM dates AS d
    WHERE d.day <= '2025-09-10'
),
category_revenue_by_day AS (
    SELECT
        p.category,
        o.order_date,
        SUM(oi.quantity * oi.unit_price) AS daily_revenue
    FROM
        orders AS o
        JOIN order_items AS oi ON o.order_id = oi.order_id
        JOIN products AS p ON oi.product_id = p.product_id
    WHERE
        o.status = 'COMPLETED'
    GROUP BY
        p.category, o.order_date
),
rank_category AS (
    SELECT
        o.category,
        o.order_date,
        ROW_NUMBER() OVER(PARTITION BY o.order_date ORDER BY daily_revenue DESC ) AS rank,
        o.daily_revenue
    FROM
        category_revenue_by_day AS o

)
SELECT
    d.day ,
    c.category,
    COALESCE(c.daily_revenue,0) AS daily_revenue
FROM
    dates AS d
    LEFT JOIN rank_category AS c ON d.day = c.order_date AND rank = 1

--  product_id | order_date | daily_revenue
-- ------------+------------+---------------
--           1 | 2025-09-01 |      50000.00
--           2 | 2025-09-02 |      60000.00
--           4 | 2025-09-03 |      30000.00
--           1 | 2025-09-04 |      25000.00
```

---

4. Doanh thu theo danh mục sản phẩm

-   Đề: Với mỗi category (VD: Laptop, Điện thoại, Phụ kiện), tính tổng doanh thu.
-   Kỹ năng: JOIN với bảng products, GROUP BY theo category.

---

5. Khách hàng mua nhiều nhất

-   Đề: Tìm customer_id có số lượng sản phẩm mua nhiều nhất (tính tổng quantity).
-   Kỹ năng: GROUP BY theo customer, SUM, ORDER BY.

```sql
SELECT
    o.customer_id
FROM
    orders AS o
    JOIN order_items AS oi ON oi.order_id = o.order_id
GROUP BY
    o.customer_id
ORDER BY
    SUM(oi.quantity) DESC
LIMIT 1

--  customer_id
-- -------------
--            2
```

---

6. Sản phẩm chưa từng được bán

-   Đề: Liệt kê những sản phẩm trong bảng products mà chưa hề xuất hiện trong order_items với trạng thái đơn hàng COMPLETED.
-   Kỹ năng: LEFT JOIN hoặc NOT IN.

```sql
SELECT
    p.*
FROM
    products AS P
WHERE
    p.product_id NOT IN (
        SELECT oi.product_id
        FROM order_items  AS oi
        INNER JOIN orders AS o ON oi.order_id = o.order_id
        WHERE o.status = 'COMPLETED'
    );

-- Cách 2 hiệu quả hơn
SELECT
    p.*
FROM
    products AS p
    LEFT JOIN order_items AS oi ON p.product_id = oi.product_id
    LEFT JOIN orders AS o ON o.order_id = oi.order_id AND o.status = 'COMPLETED'
WHERE
    o.order_id IS NULL;
-- Không cần distinct bởi vì nếu nó xuất hiện 0 hoặc 1 lần thì nó chỉ xuất hiện 1 lần

```

---

7. Đơn hàng có tổng giá trị cao nhất

-   Đề: Tìm đơn hàng (order_id) nào có tổng tiền (SUM(quantity\*unit_price)) cao nhất.
-   Kỹ năng: GROUP BY order_id, ORDER BY.

---

8. Tỷ lệ hủy đơn

-   Đề: Tính % số lượng đơn hàng CANCELLED so với tổng số đơn hàng.
-   Kỹ năng: COUNT, CASE WHEN, phép chia.

```sql
SELECT
    CAST (SUM(CASE WHEN o.status = 'CANCELLED' THEN 1 ELSE 0 END) AS DECIMAL ) / NULLIF(COUNT(*),0 ) * 100 AS percent
FROM
    orders AS o;
-- Phép chia 2 số double
```

---

9. Doanh thu trung bình mỗi đơn

-   Đề: Tính doanh thu trung bình trên một đơn hàng (chỉ tính COMPLETED).
-   Kỹ năng: AVG trên SUM per order (dùng subquery hoặc CTE).

---

10. Tháng nào bán được nhiều hàng nhất

-   Đề: Tính tổng doanh thu theo tháng, sau đó tìm tháng có doanh thu cao nhất.
-   Kỹ năng: DATE functions (EXTRACT(MONTH FROM order_date)), GROUP BY theo tháng.

```sql
WITH revenue_per_month AS (
    SELECT
        EXTRACT (MONTH FROM order_date) AS month,
        SUM(oi.quantity * oi.unit_price) AS total_revenue
    FROM
        orders AS o
        JOIN order_items AS oi ON o.order_id = oi.order_id AND o.status = 'COMPLETED'
    GROUP BY
        month
)

SELECT
    *
FROM
    revenue_per_month
ORDER BY
    total_revenue DESC
LIMIT 1
```
