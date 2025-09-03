-- Tạo bảng users
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(100),
    name VARCHAR(100),
    age INT
);
-- Thêm dữ liệu mẫu
INSERT INTO users (email, name, age)
VALUES ('a@example.com', 'Alice', 22),
    ('b@example.com', 'Bob', 25),
    ('c@example.com', 'Carol', 30),
    ('d@example.com', 'David', 27),
    ('e@example.com', 'Eve', 22);
-- 1) Thử gọi một bảng mà ko có INDEX phụ (chỉ có PK)
EXPLAIN
SELECT *
FROM users
WHERE email = 'b@example.com';
-- +----+-------------+-------+------------+------+---------------+------+---------+------+------+----------+-------------+
-- | id | select_type | table | partitions | type | possible_keys | key  | key_len | ref  | rows | filtered | Extra       |
-- +----+-------------+-------+------------+------+---------------+------+---------+------+------+----------+-------------+
-- |  1 | SIMPLE      | users | NULL       | ALL  | NULL          | NULL | NULL    | NULL |    5 |    20.00 | Using where |
-- +----+-------------+-------+------------+------+---------------+------+---------+------+------+----------+-------------+
-- Types : ALL : không trùng khớp index
-- key: hiện key các cột nếu có
-- type: ref/const/ range -> dùng index
-- key: tên index được sử dụng
-- possible_keys: các indẽ có thể dùng
-- Extra: nếu sử dụng INDEX hoặc Using WHERE thì càng tối ưu
-- Ref: mức độ tận dụng truyh vấn

-- 2) Truy vấn tận dụng Index (sử dụng ==)
CREATE INDEX idx_email ON users(email);
EXPLAIN SELECT * FROM users WHERE email = 'b@example.com';


-- +----+-------------+-------+------------+------+---------------+-----------+---------+-------+------+----------+-------+
-- | id | select_type | table | partitions | type | possible_keys | key       | key_len | ref   | rows | filtered | Extra |
-- +----+-------------+-------+------------+------+---------------+-----------+---------+-------+------+----------+-------+
-- |  1 | SIMPLE      | users | NULL       | ref  | idx_email     | idx_email | 403     | const |    1 |   100.00 | NULL  |
-- +----+-------------+-------+------------+------+---------------+-----------+---------+-------+------+----------+-------+
-- 1 row in set, 1 warning (0.01 sec)

-- Ta có thể thấy Type = Ref 
-- Possible_keys: indx_email: nó đã sử dụng INdex email của chúng ta
-- Key : idx_email: đã sử dụng email làm index

-- 4) truy vấn không tận dụng Index (Sử dụng Function)
EXPLAIN SELECT * FROM users WHERE LOWER(email) = 'a@example.com';

-- +----+-------------+-------+------------+------+---------------+------+---------+------+------+----------+-------------+
-- | id | select_type | table | partitions | type | possible_keys | key  | key_len | ref  | rows | filtered | Extra       |
-- +----+-------------+-------+------------+------+---------------+------+---------+------+------+----------+-------------+
-- |  1 | SIMPLE      | users | NULL       | ALL  | NULL          | NULL | NULL    | NULL |    5 |   100.00 | Using where |
-- +----+-------------+-------+------------+------+---------------+------+---------+------+------+----------+-------------+
-- 1 row in set, 1 warning (0.00 sec)

-- Type = ALL ;
-- KEy = null