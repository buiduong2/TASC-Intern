-- PostgreSQL
-- ===1) So sánh Sequential Scan vs Index Scan
-- Chạy Query tìm Email ở giữa. Khi chưa có index và sau khi có Index
-- Trước khi có Index
--    id   |  full_name  |         email          |           signup_at           | city | score  
-- --------+-------------+------------------------+-------------------------------+------+--------
--  500000 | User 500000 | user500000@example.com | 2025-02-24 23:30:30.762876+00 | Hue  | 422.63
-- (1 row)
EXPLAIN ANALYZE
SELECT *
FROM users
WHERE email = 'user500000@example.com';
--                                                      QUERY PLAN
-- ---------------------------------------------------------------------------------------------------------------------
--  Gather  (cost=1000.00..18748.43 rows=1 width=63) (actual time=52.097..54.961 rows=1 loops=1)
--    Workers Planned: 2
--    Workers Launched: 2
--    ->  Parallel Seq Scan on users  (cost=0.00..17748.33 rows=1 width=63) (actual time=40.252..47.551 rows=0 loops=3)
--          Filter: (email = 'user500000@example.com'::text)
--          Rows Removed by Filter: 333333
--  Planning Time: 0.071 ms
--  Execution Time: 54.982 ms
-- KQ: Có vẻ như khi chưa có index theo column mà chúng ta tìm kiếm. nó sẽ thực hiện Seq (tìm kiếm tuần tự).
-- Và nó cũng sử dụng Parallel (song song) nhưng vẫn là tuần tự
-- Tạo index và thử lại
CREATE INDEX users_email ON users(email);
--                                                      QUERY PLAN
-- --------------------------------------------------------------------------------------------------------------------
--  Index Scan using users_email on users  (cost=0.42..8.44 rows=1 width=63) (actual time=0.031..0.033 rows=1 loops=1)
--    Index Cond: (email = 'user500000@example.com'::text)
--  Planning Time: 0.087 ms
--  Execution Time: 0.052 ms
-- (4 rows) 
--            KQ: 
-- ở đây dã sử dụng Index Scan và nó tăng tốc gấp  nhanh hơn 10.000 lần.
-- SỐ lượng Row remove của tìm == 0
-- ===2) Sử dụng Index nhưng với các toán tử không hỗ trợ cho B-tree
EXPLAIN ANALYZE
SELECT *
FROM users
WHERE email LIKE '%00000@example.com';
--                                                       QUERY PLAN
-- ----------------------------------------------------------------------------------------------------------------------
--  Gather  (cost=1000.00..18758.33 rows=100 width=63) (actual time=11.406..64.058 rows=10 loops=1)
--    Workers Planned: 2
--    Workers Launched: 2
--    ->  Parallel Seq Scan on users  (cost=0.00..17748.33 rows=42 width=63) (actual time=16.819..57.373 rows=3 loops=3)
--          Filter: (email ~~ '%00000@example.com'::text)
--          Rows Removed by Filter: 333330 -> SỐ lượng như tìm kiếm tuần tự
--  Planning Time: 0.447 ms
--  Execution Time: 64.087 ms
-- == KQ: Vậy là nó thưc hiện scan tuần tự
-- ===3) Sử dụng index nhưng bên trái mệnh đề WHERE sử dụng column ko Index
EXPLAIN ANALYZE
SELECT *
FROM users
WHERE city = 'Can Tho'
    AND email = 'user300000@example.com';
--                                                      QUERY PLAN
-- --------------------------------------------------------------------------------------------------------------------
--  Index Scan using users_email on users  (cost=0.42..8.45 rows=1 width=63) (actual time=0.090..0.091 rows=1 loops=1)
--    Index Cond: (email = 'user300000@example.com'::text)
--    Filter: (city = 'Can Tho'::text)
--  Planning Time: 0.125 ms
--  Execution Time: 0.116 ms
-- (5 rows)
-- -- KQ: Có vẻ như nó vẫn đủ thông minh để biến đổi các vế của mệnh đề AND trước khi sử dụng INDEX
-- Không sử dụng lọc dòng nào cả
-- ===3) Sử dụng Index nhưng kết hợp với mệnh đề OR trong column
EXPLAIN ANALYZE
SELECT *
FROM users
WHERE city = 'Can Tho'
    OR email = 'user300000@example.com';
-- KQ:Nó sẽ thực hiện filter = Email có index trước -> filter email (thì tìm kiếm tuần tự)
--                                                    QUERY PLAN
-- ----------------------------------------------------------------------------------------------------------------
--  Seq Scan on users  (cost=0.00..27540.00 rows=252034 width=63) (actual time=0.022..119.137 rows=250059 loops=1)
--    Filter: ((city = 'Can Tho'::text) OR (email = 'user300000@example.com'::text))
--    Rows Removed by Filter: 749941
--  Planning Time: 0.091 ms
--  Execution Time: 128.419 ms
-- (5 rows)
-- ===KQ:  
-- Không aps dụng được INdex.
-- Scan tuần tự
-- === 3) SỬ dụng index nhưng WHERE thì sử dụng AND + index Column = midle
EXPLAIN ANALYZE
SELECT *
FROM users
WHERE city = 'Can Tho'
    AND full_name = 'User 300000'
    AND email = 'user300000@example.com';
--                                                          QUERY PLAN
-- --------------------------------------------------------------------------------------------------------------------
--  Index Scan using users_email on users  (cost=0.42..8.45 rows=1 width=63) (actual time=0.040..0.041 rows=1 loops=1)
--    Index Cond: (email = 'user300000@example.com'::text)
--    Filter: ((city = 'Can Tho'::text) AND (full_name = 'User 300000'::text))
--  Planning Time: 0.122 ms
--  Execution Time: 0.066 ms
-- (5 rows)
-- KQ:Nó sẽ thực hiện filter = Email có index trước -> filter city -> filter full_name
EXPLAIN ANALYZE
SELECT *
FROM users
WHERE city = 'Can Tho'
    AND email = 'user300000@example.com'
    AND full_name = 'User 300000';
--                                                          QUERY PLAN
-- --------------------------------------------------------------------------------------------------------------------
--  Index Scan using users_email on users  (cost=0.42..8.45 rows=1 width=63) (actual time=0.051..0.054 rows=1 loops=1)
--    Index Cond: (email = 'user300000@example.com'::text)
--    Filter: ((city = 'Can Tho'::text) AND (full_name = 'User 300000'::text))
--  Planning Time: 0.326 ms
--  Execution Time: 0.083 ms
-- (5 rows)
-- KQ:Nó sẽ thực hiện filter = Email có index trước -> filter city -> filter full_name
-- === Kết luận: Nếu sử dụng mênh đề AND thì bất cứ vị trí nào của index đều sẽ được áp dụng
-- Còn mệnh đề OR Thì áp dụng 1 phần nếu có thể
-- ===4) remove index 
DROP INDEX users_email;