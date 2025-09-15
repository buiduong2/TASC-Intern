-- Test ALL NULL
DO $$
DECLARE -- Khai báo biến để chứa con trỏ OUT
    c_orders REFCURSOR;
-- Khai báo biến để chứa giá trị tổng số trang OUT
page_total BIGINT;
-- Khai báo biến để chứa dữ liệu trả về
order_record RECORD;
BEGIN -- Gọi thủ tục và truyền biến đã khai báo vào các tham số OUT
CALL get_admin_orders_page(
    ref_orders => c_orders,
    page_total => page_total,
    p_page_size => 10,
    p_page_offset => 0
);
-- Lấy tất cả dữ liệu từ con trỏ
FETCH ALL
FROM c_orders INTO order_record;
-- Đóng con trỏ
CLOSE c_orders;

RAISE NOTICE 'Total pages: %',
page_total;
END $$;
-- FILTER By status
BEGIN;
CALL get_admin_orders_page(
    'c_orders',
    NULL,
    NULL,
    -- p_order_id
    'COMPLETED',
    -- p_status
    NULL,
    -- p_payment_method_name
    NULL,
    -- p_shipping_method_id
    NULL,
    -- p_customer_id
    NULL,
    NULL,
    -- created_from, created_to
    NULL,
    NULL,
    -- min/max total_price
    NULL,
    NULL,
    -- min/max total_cost
    'o.total DESC',
    -- p_sorts
    5,
    -- page_size
    0 -- page_offset
);
FETCH ALL
FROM c_orders;
COMMIT;