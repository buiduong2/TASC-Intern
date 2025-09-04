-- 1). Thêm PARTITION mới VD 2025
ALTER TABLE orders REORGANIZE PARTITION pmax INTO (
        PARTITION p2024
        VALUES LESS THAN (2025),
            PARTITION pmax
        VALUES LESS THAN MAXVALUE
    );
-- Tách pmax thành 2 partition
-- p2024 chứa YEAR(created_at) <2025
-- pmax: chứa các năm lớn hơn hoặc bằng 2025
SELECT *
FROM orders PARTITION (p2024);
-- +----+---------------+--------+------------+
-- | id | customer_name | amount | created_at |
-- +----+---------------+--------+------------+
-- |  3 | Carol         | 300.00 | 2024-02-10 |
-- +----+---------------+--------+------------+
-- 1 row in set (0.00 sec)
SELECT *
FROM orders PARTITION(pmax);
-- Empty set (0.00 sec)
-- Vậy là hệ thống đã tự cắt nhỏ thay đổi pMaxx thành các PARTITION và sắp xếp dữ liệu cho chúng ta luôn
-- 2) Merge 2 Parition
ALTER TABLE orders REORGANIZE PARTITION p2022,
    p2023 INTO (
        PARTITION p_old
        VALUES LESS THAN (2024)
    );
-- Kiểm tra PARTITION old
-- +----------------+------------+
-- | PARTITION_NAME | TABLE_ROWS |
-- +----------------+------------+
-- | p2024          |          0 |
-- | p_old          |          0 |
-- | pmax           |          0 |
-- +----------------+------------+
-- 3 rows in set (0.00 sec)
SELECT *
FROM orders PARTITION(p_old);
-- +----+---------------+--------+------------+
-- | id | customer_name | amount | created_at |
-- +----+---------------+--------+------------+
-- |  1 | Alice         | 100.00 | 2022-05-01 |
-- |  2 | Bob           | 200.00 | 2023-07-15 |
-- +----+---------------+--------+------------+
-- 2 rows in set (0.00 sec)