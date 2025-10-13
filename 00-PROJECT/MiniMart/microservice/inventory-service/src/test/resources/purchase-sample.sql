-- Purchase chưa có stock allocation
INSERT INTO purchase (id, supplier, status, created_at, updated_at, created_by_id, updated_by_id)
VALUES (1000, 'Supplier A', 'DRAFT', NOW(), NOW(), 1001, 1001);

-- Các item trong đơn DRAFT (chưa có stock allocation)
INSERT INTO purchase_item (id, quantity, remaining_quantity, cost_price, created_at, purchase_id, product_id)
VALUES 
    (1000, 50, 50, 12.75, NOW(), 1000, 101),
    (2000, 100, 100, 8.50, NOW(), 1000, 102);

    -- Purchase đã có stock allocation
INSERT INTO purchase (id, supplier, status, created_at, updated_at, created_by_id, updated_by_id)
VALUES (1001, 'Supplier B', 'ACTIVE', NOW(), NOW(), 1002, 1002);

-- Các item của purchase ACTIVE
INSERT INTO purchase_item (id, quantity, remaining_quantity, cost_price, created_at, purchase_id, product_id)
VALUES 
    (3000, 200, 150, 10.00, NOW(), 1001, 201),   -- Đã xuất 50 -> remaining 150
    (4000, 100, 90, 15.00, NOW(), 1001, 202);    -- Đã xuất 10 -> remaining 90

-- Stock allocations tương ứng
INSERT INTO stock_allocation (id, allocated_quantity, created_at, purchase_item_id, order_id, status)
VALUES
    (1000, 50, NOW(), 3000, 5001, 'ACTIVE'),
    (2000, 10, NOW(), 4000, 5002, 'ACTIVE');