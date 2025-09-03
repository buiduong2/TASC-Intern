-- View đơn giản không Update able
CREATE TABLE customers (
    customer_id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT UNIQUE,
    status TEXT NOT NULL CHECK (status IN ('active', 'inactive'))
);
CREATE TABLE orders (
    order_id SERIAL PRIMARY KEY,
    customer_id INT NOT NULL REFERENCES customers(customer_id),
    order_date DATE NOT NULL DEFAULT CURRENT_DATE,
    status TEXT NOT NULL CHECK (
        status IN ('pending', 'paid', 'shipped', 'cancelled')
    ),
    total NUMERIC(12, 2) NOT NULL CHECK (total >= 0)
);
CREATE TABLE order_items (
    order_id INT NOT NULL REFERENCES orders(order_id) ON DELETE CASCADE,
    product TEXT NOT NULL,
    qty INT NOT NULL CHECK (qty > 0),
    price NUMERIC(12, 2) NOT NULL CHECK (price >= 0),
    PRIMARY KEY (order_id, product)
);
-- 1) VIEW đơn giản (UPDATEABLE)
-- Lọc ra các user có status = 'active'
CREATE VIEW v_active_customer AS
SELECT customer_id,
    name,
    email,
    status
FROM customers
WHERE status = 'active' WITH LOCAL CHECK OPTION;
--CHECK OPTION Bắt buộc INSERT hoặc Update thông qua view phải thỏa mãn điều kiện WHERE của VIew . nếu vi phạm thì báo lỗi
-- LOCAL: chỉ check điều kiện của VIEW hiện tại
-- Nói tóm lại khi mà ta EDIT một record trong VIEW thì ko thể thay đổi bản chất khiến cho record đó biến mất khởi VIEW được . VD set status = 'inactive' khi đó nó không còn trong VIEW
-- Test việc đọc
-- Test đọc:
SELECT *
FROM v_active_customer;
-- customer_id	name	email	            status
-- 1	        Alice	alice@example.com	active
-- 3	        Caro	caro@example.com	active
-- Tiền hành Update VIEw sao cho điều kiện WHERE không còn chính xác nữa status = 'inactive'
UPDATE v_active_customer
SET status = 'inactive'
WHERE customer_id = 1;
-- Lỗi
-- new row violates check option for view "v_active_customer"
-- DETAIL: Failing row contains (1, Alice, alice@example.com, inactive).
-- Test thêm dữ liệu qua view (phải chuyến status = 'active' hoặc để default để thỏa mãn WHERE)
INSERT INTO v_active_customer(name, email, status)
VALUES('Duong', 'Duong@gmail.com', 'active');
--Đã thực hiện xong, ảnh hưởng đến 1 dòng
--
-- 2) tạo View tổng hợp
-- Tổng doanh thu khách hàng (chỉ tính order/shipped)
CREATE VIEW v_customer_sales AS
SELECT c.customer_id,
    c.name,
    COUNT(DISTINCT o.order_id) AS orders_count,
    COALESCE(SUM(oi.qty * oi.price), 0) AS gross_revenue
FROM customers c
    LEFT JOIN orders o ON o.customer_id = c.customer_id
    LEFT JOIN order_items oi ON oi.order_id = o.order_id
GROUP BY c.customer_id,
    c.name;
-- Chạy thử 1 câu SELECT
SELECT *
FROM v_customer_sales
ORDER BY gross_revenue DESC;
-- customer_id	name	orders_count	gross_revenue
-- 1	        Alice	4	            330.50
-- 3	        Caro	2	            300.00
-- 2	        Bob	    2	            50.00
-- 5	        Duong	0	            0
--  00:00:00.068
-- 5) VIEW BỘ LỌC CHO DỮ LIỆU BÁO CÁO
CREATE VIEW v_paid_orders AS
SELECT o.order_id,
    o.customer_id,
    o.order_date,
    o.total
FROM orders o
WHERE o.status = 'paid';
-- 6) Thử chuyển đổi sang Materialized để xem tốc độ
DROP VIEW v_customer_sales;
CREATE MATERIALIZED VIEW v_customer_sales AS
SELECT c.customer_id,
    c.name,
    COUNT(DISTINCT o.order_id) AS orders_count,
    COALESCE(SUM(oi.qty * oi.price), 0) AS gross_revenue
FROM customers c
    LEFT JOIN orders o ON o.customer_id = c.customer_id
    LEFT JOIN order_items oi ON oi.order_id = o.order_id
GROUP BY c.customer_id,
    c.name;
-- Tốc dỗ sẽ nhanh hơn