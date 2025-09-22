CREATE TABLE orders (
    order_id BIGINT PRIMARY KEY,
    order_date DATE NOT NULL,
    customer_id BIGINT,
    status VARCHAR(20) -- PENDING, PAID, CANCELLED, COMPLETED
);
CREATE TABLE order_items (
    order_item_id BIGINT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id)
);
CREATE TABLE products (
    product_id BIGINT PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    category VARCHAR(100),
    price DECIMAL(10, 2)
);
INSERT INTO products (product_id, product_name, category, price)
VALUES (1, 'iPhone 15', 'Điện thoại', 25000.00),
    (2, 'Samsung Galaxy S23', 'Điện thoại', 20000.00),
    (3, 'MacBook Air M2', 'Laptop', 35000.00),
    (4, 'Dell XPS 13', 'Laptop', 30000.00),
    (5, 'AirPods Pro 2', 'Phụ kiện', 5000.00);
INSERT INTO orders (order_id, order_date, customer_id, status)
VALUES (101, '2025-09-01', 1, 'COMPLETED'),
    (102, '2025-09-02', 2, 'COMPLETED'),
    (103, '2025-09-02', 3, 'COMPLETED'),
    (104, '2025-09-03', 2, 'CANCELLED'),
    (105, '2025-09-04', 4, 'COMPLETED');
INSERT INTO order_items (
        order_item_id,
        order_id,
        product_id,
        quantity,
        unit_price
    )
VALUES (1001, 101, 1, 2, 25000.00),
    -- 2 iPhone 15
    (1002, 101, 5, 1, 5000.00),
    -- 1 AirPods Pro 2
    (1003, 102, 2, 3, 20000.00),
    -- 3 Samsung Galaxy S23
    (1004, 103, 3, 1, 35000.00),
    -- 1 MacBook Air M2
    (1005, 103, 5, 2, 5000.00),
    -- 2 AirPods Pro 2
    (1006, 104, 4, 1, 30000.00),
    -- 1 Dell XPS 13 (đơn bị hủy)
    (1007, 105, 1, 1, 25000.00),
    -- 1 iPhone 15
    (1008, 105, 5, 2, 5000.00);
-- 2 AirPods Pro 2