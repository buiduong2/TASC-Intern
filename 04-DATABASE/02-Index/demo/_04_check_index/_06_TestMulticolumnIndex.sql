-- ===============Tạo Index trên multi Column==============
CREATE INDEX users_city_score ON users(city, score);
DROP INDEX users_city_score;
EXPLAIN ANALYZE
SELECT *
FROM users
WHERE city = 'Can Tho'
    AND score > 800;
--                                                          QUERY PLAN-- Trước Index
-- ----------------------------------------------------------------------------------------------------------------------------
--  Gather  (cost=1000.00..24739.30 rows=49493 width=63) (actual time=0.548..83.678 rows=49962 loops=1)
--    Workers Planned: 2
--    Workers Launched: 2
--    ->  Parallel Seq Scan on users  (cost=0.00..18790.00 rows=20622 width=63) (actual time=0.022..73.454 rows=16654 loops=3)
--          Filter: ((score > '800'::numeric) AND (city = 'Can Tho'::text))
--          Rows Removed by Filter: 316679
--  Planning Time: 0.147 ms
--  Execution Time: 86.466 ms
-- (8 rows)
-- KQ: tìm kiếm tuần tự và kiểm tra theo điều kiện
--                                                               QUERY PLAN -- Sau Index
-- --------------------------------------------------------------------------------------------------------------------------------------
--  Bitmap Heap Scan on users  (cost=1327.73..14610.12 rows=49493 width=63) (actual time=20.560..38.344 rows=49962 loops=1)
--    Recheck Cond: ((city = 'Can Tho'::text) AND (score > '800'::numeric))
--    Heap Blocks: exact=12336
--    ->  Bitmap Index Scan on users_city_score  (cost=0.00..1315.36 rows=49493 width=0) (actual time=17.181..17.181 rows=49962 loops=1)
--          Index Cond: ((city = 'Can Tho'::text) AND (score > '800'::numeric))
--  Planning Time: 0.228 ms
--  Execution Time: 40.422 ms
-- (7 rows)
-- Điều kiện Index được áp dụng trên city 'Can Tho' và score 
-- KQ: Nhanh gấp 2 lần
-- ====3) Đảo ngược thứ tự
EXPLAIN ANALYZE
SELECT *
FROM users
WHERE score < 800
    AND city = 'Can Tho';
--                                                                QUERY PLAN
-- ---------------------------------------------------------------------------------------------------------------------------------------- 
--  Bitmap Heap Scan on users  (cost=5428.44..21006.51 rows=202538 width=63) (actual time=22.533..64.864 rows=200093 loops=1)
--    Recheck Cond: ((city = 'Can Tho'::text) AND (score < '800'::numeric))
--    Heap Blocks: exact=12539
--    ->  Bitmap Index Scan on users_city_score  (cost=0.00..5377.81 rows=202538 width=0) (actual time=20.462..20.462 rows=200093 loops=1)  
--          Index Cond: ((city = 'Can Tho'::text) AND (score < '800'::numeric))
--  Planning Time: 0.115 ms
--  Execution Time: 73.557 ms
-- (7 rows)
-- KQ: vẫn áp dụng được Index trên ((city = 'Can Tho'::text) AND (score < '800'::numeric))
-- ===)Kết luận : vậy postgre đủ thông minh để sắp xếp lại các vế trong mệnh đề WHERE 
-- === 4) Sử dụng Index ngược + một column không INdex: 
EXPLAIN ANALYZE
SELECT *
FROM users
WHERE score < 800
    AND city = 'Can Tho'
    AND email LIKE 'user%';
EXPLAIN ANALYZE
SELECT *
FROM users
WHERE score < 800
    AND email LIKE 'user%'
    AND city = 'Can Tho';
-- ==KQ: Chỉ có các toán tử nằm liền kề nhau thì thế nào cũng có thể áp dụng được Index. Engine tự động có thể tối ưu hóa câu SQL
-- 6): Sử dụng Index ở cột 2 . Bỏ qua cột 1
EXPLAIN ANALYZE
SELECT *
FROM users
WHERE score < 800;
--                                                    QUERY PLAN
-- ----------------------------------------------------------------------------------------------------------------
--  Seq Scan on users  (cost=0.00..25040.00 rows=803616 width=63) (actual time=0.012..125.803 rows=800444 loops=1)
--    Filter: (score < '800'::numeric)
--    Rows Removed by Filter: 199556
--  Planning Time: 0.071 ms
--  Execution Time: 148.941 ms
-- (5 rows)
-- KQ: Sử dụng Query tuần tự 100%. KO áp dụng được Index nếu ở cột 2
EXPLAIN ANALYZE
SELECT *
FROM users
WHERE city = 'Can Tho';
--                                                                QUERY PLAN
-- ---------------------------------------------------------------------------------------------------------------------------------------- 
--  Bitmap Heap Scan on users  (cost=6121.68..21812.09 rows=252033 width=63) (actual time=23.348..58.838 rows=250059 loops=1)
--    Recheck Cond: (city = 'Can Tho'::text)
--    Heap Blocks: exact=12539
--    ->  Bitmap Index Scan on users_city_score  (cost=0.00..6058.67 rows=252033 width=0) (actual time=21.678..21.679 rows=250059 loops=1)  
--          Index Cond: (city = 'Can Tho'::text)
--  Planning Time: 0.124 ms
--  Execution Time: 67.119 ms
-- (7 rows)
-- Vẫn có thể áp dụng được INdex đầu tiên

-- ====7) Test việc sử dụng INDEX với GROUP BY
EXPLAIN ANALYZE
SELECT city
FROM users
WHERE  city LIKE 'H' -- Neus sử dụng <> hoặc khác với LIKE prefix hoặc == sẽ là tuần tự
GROUP BY city;

--                                                             QUERY PLAN
-- -----------------------------------------------------------------------------------------------------------------------------------
--  Group  (cost=0.42..4.45 rows=1 width=8) (actual time=0.183..0.184 rows=0 loops=1)
--    Group Key: city
--    ->  Index Only Scan using users_city_score on users  (cost=0.42..4.45 rows=1 width=8) (actual time=0.182..0.183 rows=0 loops=1)
--          Index Cond: (city = 'H'::text)
--          Filter: (city ~~ 'H'::text)
--          Heap Fetches: 0
--  Planning Time: 0.416 ms
--  Execution Time: 0.214 ms
-- (8 rows)

-- Phiên dịch: Sử dụng index users_city_score duy nhât để scan --> kể cả có là GROUP BY thì vẫn áp dụng được

-- ====8) Áp dụng WHERE và GROUP BY là 2 index khác nhau


EXPLAIN ANALYZE
SELECT city
FROM users
WHERE  id >  500000 -- Neus sử dụng <> hoặc khác với LIKE prefix hoặc == sẽ là tuần tự
GROUP BY city;

--                                                                   QUERY PLAN
-- ------------------------------------------------------------------------------------------------------------------------------------------------  
--  Group  (cost=19269.14..19270.33 rows=5 width=8) (actual time=71.088..73.609 rows=5 loops=1)
--    Group Key: city
--    ->  Gather Merge  (cost=19269.14..19270.30 rows=10 width=8) (actual time=71.087..73.602 rows=15 loops=1)
--          Workers Planned: 2
--          Workers Launched: 2
--          ->  Sort  (cost=18269.11..18269.12 rows=5 width=8) (actual time=68.634..68.636 rows=5 loops=3)
--                Sort Key: city
--                Sort Method: quicksort  Memory: 25kB
--                Worker 0:  Sort Method: quicksort  Memory: 25kB
--                Worker 1:  Sort Method: quicksort  Memory: 25kB
--                ->  Partial HashAggregate  (cost=18269.00..18269.05 rows=5 width=8) (actual time=68.602..68.603 rows=5 loops=3)
--                      Group Key: city
--                      Batches: 1  Memory Usage: 24kB
--                      Worker 0:  Batches: 1  Memory Usage: 24kB
--                      Worker 1:  Batches: 1  Memory Usage: 24kB
--                      ->  Parallel Seq Scan on users  (cost=0.00..17748.33 rows=208268 width=8) (actual time=12.879..33.489 rows=166667 loops=3)   
--                            Filter: (id > 500000)
--                            Rows Removed by Filter: 166667
--  Planning Time: 0.578 ms
--  Execution Time: 73.652 ms