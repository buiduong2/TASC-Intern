-- Sinh ra 1.000.000 bản ghi
-- 1) Tạo bảng mẫu (đơn giản, không index để insert nhanh)
DROP TABLE IF EXISTS users;
CREATE TABLE users (
    id bigint PRIMARY KEY,
    full_name text,
    email text,
    signup_at timestamptz,
    city text,
    score numeric(10, 2)
);
-- 2) Chèn 1.000.000 bản ghi
--    Dùng 1 câu INSERT ... SELECT cho tốc độ tốt
INSERT INTO users (id, full_name, email, signup_at, city, score)
SELECT gs AS id,
    'User ' || gs AS full_name,
    'user' || gs || '@example.com' AS email,
    NOW() - (random() * interval '365 days') AS signup_at,
    (
        ARRAY ['Ha Noi','Ho Chi Minh','Da Nang','Can Tho','Hue']
    ) [(random()*4+1)::int] AS city,
    (random() * 1000)::numeric(10, 2) AS score
FROM generate_series(1, 1000000) AS gs;