-- MỘt số database không cung cấp khả năng tạo ra Clusterde Index : VD như MySQL, PostgresSQL, bản chất nó là PK rồi ko cho đổi
-- Tạo INdex trên một bảng
CREATE INDEX idx_email ON users(email);
-- Tạo Index trên nhiều cột
CREATE INDEX idx_name_age ON users(name, age);
-- Kiểm tra index
-- +-------+------------+--------------+--------------+-------------+-----------+-------------+----------+--------+------+------------+---------+---------------+---------+------------+
-- | Table | Non_unique | Key_name     | Seq_in_index | Column_name | Collation | Cardinality | Sub_part | Packed | Null | Index_type | Comment | Index_comment | Visible | Expression |
-- +-------+------------+--------------+--------------+-------------+-----------+-------------+----------+--------+------+------------+---------+---------------+---------+------------+
-- | users |          0 | PRIMARY      |            1 | id          | A         |           5 |     NULL |   NULL |      | BTREE      |         |               | YES     | NULL       |
-- | users |          0 | id           |            1 | id          | A         |           5 |     NULL |   NULL |      | BTREE      |         |               | YES     | NULL       |
-- | users |          1 | idx_email    |            1 | email       | A         |           5 |     NULL |   NULL | YES  | BTREE      |         |               | YES     | NULL       |
-- | users |          1 | idx_name_age |            1 | name        | A         |           5 |     NULL |   NULL | YES  | BTREE      |         |               | YES     | NULL       |
-- | users |          1 | idx_name_age |            2 | age         | A         |           5 |     NULL |   NULL | YES  | BTREE      |         |               | YES     | NULL       |
-- +-------+------------+--------------+--------------+-------------+-----------+-------------+----------+--------+------+------------+---------+---------------+---------+------------+
-- 5 rows in set (0.01 sec)

-- name cột 1. Age cột 2
-- Tức là index được đánh quan trong về thứ tự. Nếu thứ tự tìm kiếm khác sẽ ko hiệu quả