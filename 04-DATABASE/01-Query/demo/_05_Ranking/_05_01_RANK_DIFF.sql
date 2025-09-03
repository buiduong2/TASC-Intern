--Bảng mẫu
-- id	name	score
-- 1	An	    95
-- 2	Bình	87
-- 3	Cường	95
-- 4	Dũng	76
-- 5	Hà	    87
-- 6	Lan	    65
-- 7	Mai	    95
-- 8	Nam	    76
-- 9	Oanh	92
-- 10	Phúc	87
-- Hàm ROW_NUMBER đánh số theo thứ tự các hàng
SELECT *,
    ROW_NUMBER() OVER(
        ORDER BY score DESC
    )
FROM students;
-- KQ: 
-- id	name	score	row_number
-- 3	Cường	    95	1 -- Điểm trùng nhau
-- 7	Mai	        95	2  -- Điểm chùng nhau
-- 1	An	        95	3 -- Điểm trùng nhau
-- 9	Oanh	    92	4
-- 10	Phúc	    87	5
-- 2	Bình	    87	6
-- 5	Hà	        87	7
-- 8	Nam	        76	8
-- 4	Dũng	    76	9
-- 6	Lan	        65	10
-- Hàm RANK có thể đánh số trùng các hàng có giá trị bằng nhau. Cấp bậc không tuần tự. Bỏ số khi có đồng hạng
SELECT *,
    RANK() OVER(
        ORDER BY score DESC
    )
FROM students;
-- id	name	score	rank
-- 3	Cường	    95	1 -- Điểm trùng nhau cùng số
-- 7	Mai	        95	1 -- Điểm trùng nhau cùng số
-- 1	An	        95	1 -- Điểm trùng nhau cùng số
-- 9	Oanh	    92	4 -- Nhưng đã đánh trùng 2 lần số bên trong vẫn được tính. Bị nhảy cóc
-- 10	Phúc	    87	5
-- 2	Bình	    87	5
-- 5	Hà	        87	5
-- 8	Nam	        76	8
-- 4	Dũng	    76	8
-- 6	Lan	        65	10
-- RANK_DENSE đánh số từ trên xuống dưới. Khi có trùng thứ tự sắp xếp thì có cùng số. Nhưng không bỏ sót số nào

SELECT *,
    DENSE_RANK() OVER(
        ORDER BY score DESC
    )
FROM students;
-- id	name	score	dense_rank
-- 3	Cường	    95	1 -- Trùng thứ tự thì trùng hạng
-- 7	Mai	        95	1 -- Trùng thứ tự thì trùng hạng
-- 1	An	        95	1 -- Trùng thứ tự thì trùng hạng
-- 9	Oanh	    92	2 -- Rank tuần tự
-- 10	Phúc	    87	3
-- 2	Bình	    87	3
-- 5	Hà	        87	3
-- 8	Nam	        76	4
-- 4	Dũng	    76	4
-- 6	Lan	        65	5