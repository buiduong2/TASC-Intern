-- Tạo bang Order với Created_at theo dạng DATE 
-- Mục tiêu là chia PARTITION theo năm
CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(100),
    amount DECIMAL(10, 2),
    created_at DATE
) PARTITION BY RANGE (YEAR(created_at)) (
    PARTITION p2022
    VALUES LESS THAN (2023),
        PARTITION p2023
    VALUES LESS THAN (2024),
        PARTITION pmax
    VALUES LESS THAN MAXVALUE
);
-- MYSQL
--ERROR 1503 (HY000): A PRIMARY KEY must include all columns in the table's partitioning function (prefixed columns are not considered).
-- Tát cả các cột dùng trong hàm PARTITION (created_at) phải có mặt trong PRIMARY_KEY hoặc UNIQUE_KEY
-- Thêm PRIMARY KEY (mySQL còn postgres chỉ cần UNIQUE) chứa cả `id` và `created_at`
-- Lại
CREATE TABLE orders (
    id INT AUTO_INCREMENT, -- Id vẫn tự động tăng ko trùng lặp
    customer_name VARCHAR(100),
    amount DECIMAL(10, 2),
    created_at DATE,
    PRIMARY KEY (id, created_at)
) PARTITION BY RANGE (YEAR(created_at)) (
    PARTITION p2022
    VALUES LESS THAN (2023),
        PARTITION p2023
    VALUES LESS THAN (2024),
        PARTITION pmax
    VALUES LESS THAN MAXVALUE
);
-- Việc thêm PRIMARY_KEY (id,created_at) chúng ta vẫn ko cần  lo lắng đến tính độc nhất của created_at


-- 