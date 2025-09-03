-- Bài toán:  Xếp hạng
-- // Xếp hạng User, theo SCORE-> Khi trùng thì theo thứ tự ID
CREATE TABLE students (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100),
    score INT
);
-- Ta biết để xếp hạng thì dựa vào số điểm. Vậy trong biểu phức window tá sẽ sắp xếp lại và đánh số cho nó
-- Ngoài ra ta cần tập hợp tát cả mọi người chứ ko riêng gì 1 người duy nhất
SELECT *,
    ROW_NUMBER() OVER(
        ORDER BY score DESC
    )
FROM students;
-- Bài toán: Đánh số thứ tự theo nhóm 
-- CHo SInh viên, môn học, điểm. Tiến hành xếp loại các sinh viên theo điểm từng môn học
CREATE TABLE exam_results (
    id SERIAL PRIMARY KEY,
    student_name VARCHAR(100),
    subject VARCHAR(50),
    score INT
);
SELECT *,
    ROW_NUMBER() OVER(
        PARTITION BY subject -- Ở đây sử dụng xếp loại theo nhóm
        ORDER BY score DESC -- sort chỉ trong cái window đó thôi
    ) AS rank
FROM exam_results;
-- 
-- Bài toán Nth
-- Bài toán tìm SV đứng thứ 2 trong mỗi nhóm của bảng trên
-- Lưu ý là WINDOW FUNCTION chạy sau khi mệnh đề SELECT chạy. Nên WHERE ở đây vẫn chưa nhìn thấy rank nào cả
-- Vậy ta phải sử dụng Sub QUery hoặc CTE
SELECT *
FROM (
        SELECT *,
            ROW_NUMBER() OVER(
                PARTITION BY subject
                ORDER BY score DESC
            ) AS rank
        FROM exam_results
    )
WHERE rank = 2;
-- Bài toán Top N
-- Tìm 3 SV có tổng điểm cao nhất trong bảng `students`
SELECT *
FROM (
        SELECT *,
            ROW_NUMBER() OVER(
                ORDER BY score DESC
            ) AS rank
        FROM students
    )
WHERE rank <= 3;
-- Mõi nhóm tìm 3 SV có điểm cao nhất trong bảng `exam_results`
SELECT *
FROM (
        SELECT *,
            ROW_NUMBER() OVER (
                PARTITION BY subject
                ORDER BY score DESC
            ) AS rank
        FROM exam_results
    )
WHERE rank <= 3;