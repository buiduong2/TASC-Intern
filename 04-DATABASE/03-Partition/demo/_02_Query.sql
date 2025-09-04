-- Kiểm tra cấu trúc của bảng orders
INSERT INTO orders(customer_name, amount, created_at)
VALUES ('Alice', 100.0, '2022-05-01'),
    ('Bob', 200.0, '2023-07-15'),
    ('Carol', 300.0, '2024-02-10');
SELECT partition_name,
    table_rows
FROM information_schema.partitions
WHERE table_name = 'orders';
-- +----------------+------------+
-- | PARTITION_NAME | TABLE_ROWS |
-- +----------------+------------+
-- | p2022          |          1 |
-- | p2023          |          1 |
-- | pmax           |          1 |
-- +----------------+------------+
-- Chúng ta đã có các số dòng tương ứng
-- 1) Truy vấn trong từng PARTITION
SELECT *
FROM orders PARTITION (p2022);
+ ----+---------------+--------+------------+
-- | id | customer_name | amount | created_at |
-- +----+---------------+--------+------------+
-- |  1 | Alice         | 100.00 | 2022-05-01 | YEAR(2022) < 2023
-- +----+---------------+--------+------------+
-- 1 row in set (0.00 sec)
SELECT *
FROM orders PARTITION (p2023);
-- +----+---------------+--------+------------+
-- | id | customer_name | amount | created_at |
-- +----+---------------+--------+------------+
-- |  2 | Bob           | 200.00 | 2023-07-15 | -- YEAR(2023) < 2024
-- +----+---------------+--------+------------+
-- 1 row in set (0.00 sec)
SELECT *
FROM orders PARTITION (pmax);
-- +----+---------------+--------+------------+
-- | id | customer_name | amount | created_at |
-- +----+---------------+--------+------------+
-- |  3 | Carol         | 300.00 | 2024-02-10 | YEAR(2024) < max
-- +----+---------------+--------+------------+
-- 1 row in set (0.00 sec)
-- Vậy RANGE kiểm tra từ trên xuống dưới (LESS THAN) dòng nào khớp thì ở PARTITION đó