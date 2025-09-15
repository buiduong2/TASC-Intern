CREATE OR REPLACE PROCEDURE get_admin_orders_page (
        INOUT ref_orders REFCURSOR,
        OUT page_total BIGINT,
        IN p_order_id BIGINT DEFAULT NULL,
        IN p_status VARCHAR(50) DEFAULT NULL,
        IN p_payment_method_name VARCHAR(50) DEFAULT NULL,
        IN p_shipping_method_id BIGINT DEFAULT NULL,
        IN p_customer_id BIGINT DEFAULT NULL,
        IN p_created_from TIMESTAMP DEFAULT NULL,
        IN p_created_to TIMESTAMP DEFAULT NULL,
        IN p_min_total_price NUMERIC DEFAULT NULL,
        IN p_max_total_price NUMERIC DEFAULT NULL,
        IN p_min_total_cost NUMERIC DEFAULT NULL,
        IN p_max_total_cost NUMERIC DEFAULT NULL,
        IN p_sorts TEXT DEFAULT NULL,
        IN p_page_size INT DEFAULT 10,
        IN p_page_offset INT DEFAULT 0
    ) LANGUAGE plpgsql AS $$
DECLARE v_main_sql TEXT := '';
v_count_sql TEXT := '';
v_where TEXT := '';
v_having TEXT := '';
v_order TEXT := '';
BEGIN -- Base query
v_main_sql := '     SELECT       
                        o.id,
                        o.status,
                        p.name AS paymentMethod,
                        s.name AS shippingMethod,
                        o.total AS totalPrice,
                        COALESCE(SUM(oi.avg_cost_price), 0) AS totalCost,
                        CASE
                            WHEN o.status = ''COMPLETED''
                            THEN o.total - COALESCE(SUM(oi.avg_cost_price * oi.quantity), 0)
                            ELSE 0
                        END AS profit,   
                        o.customer_id AS customerId,
                        o.created_at AS createdAt,
                        o.updated_at AS updatedAt
                    FROM orders o
                    LEFT JOIN order_item oi ON o.id = oi.order_id
                    LEFT JOIN shipping_method s ON s.id = o.shipping_method_id
                    LEFT JOIN payment p ON p.id = o.payment_id ';
-- WHERE
IF p_order_id IS NOT NULL THEN 
    v_where := v_where || ' AND o.id = ' || p_order_id;
END IF;
IF p_status IS NOT NULL THEN
    v_where := v_where || ' AND o.status = ' || quote_literal(p_status);
END IF;
IF p_payment_method_name IS NOT NULL THEN
    v_where := v_where || ' AND p.name = ' || quote_literal(p_payment_method_name);
END IF;
IF p_shipping_method_id IS NOT NULL THEN
    v_where := v_where || ' AND s.id = ' || p_shipping_method_id;
END IF;
IF p_customer_id IS NOT NULL THEN
    v_where := v_where || ' AND o.customer_id = ' || p_customer_id;
END IF;
IF p_created_from IS NOT NULL THEN
    v_where := v_where || ' AND o.created_at >= ' || quote_literal(p_created_from);
END IF;
IF p_created_to IS NOT NULL THEN
    v_where := v_where || ' AND o.created_at <= ' || quote_literal(p_created_to);
END IF;
IF p_min_total_price IS NOT NULL THEN
    v_where := v_where || ' AND o.total >= ' || p_min_total_price;
END IF;
IF p_max_total_price IS NOT NULL THEN
    v_where := v_where || ' AND o.total <= ' || p_max_total_price;
END IF;
-- HAVING
IF p_max_total_cost IS NOT NULL THEN
    v_having := v_having || ' AND COALESCE(SUM(oi.avg_cost_price), 0) <= ' || p_max_total_cost;
END IF;
IF p_min_total_cost IS NOT NULL THEN
    v_having := v_having || ' AND COALESCE(SUM(oi.avg_cost_price), 0) >= ' || p_min_total_cost;
END IF;
-- ORDER
IF p_sorts IS NOT NULL THEN
    v_order := p_sorts;
ELSE 
    v_order := 'o.created_at DESC';
END IF;
-- Count query
IF v_having = '' THEN 
    v_count_sql := 'SELECT COUNT(*) FROM orders o 
                        LEFT JOIN shipping_method s ON s.id = o.shipping_method_id
                        LEFT JOIN payment p ON p.id = o.payment_id 
                        WHERE 1=1 ' || v_where;
ELSE 
    v_count_sql := 'WITH main_query AS (
                            SELECT o.id
                            FROM orders o
                            LEFT JOIN order_item oi ON o.id = oi.order_id
                            LEFT JOIN shipping_method s ON s.id = o.shipping_method_id
                            LEFT JOIN payment p ON p.id = o.payment_id
                            WHERE 1=1 ' || v_where || '
                            GROUP BY o.id
                            HAVING ' || substring( v_having FROM 5 ) || ' ) 
                            SELECT COUNT(*) FROM main_query';
END IF;
-- Main query
IF v_where <> '' THEN 
    v_main_sql := v_main_sql || ' WHERE 1=1 ' || v_where;
END IF;
v_main_sql := v_main_sql || ' GROUP BY o.id, o.status, p.name, s.name, o.total, o.customer_id, o.created_at, o.updated_at';
IF v_having <> '' THEN 
    v_main_sql := v_main_sql || ' HAVING ' || substring( v_having FROM 5 );
END IF;
v_main_sql := v_main_sql || ' ORDER BY ' || v_order;
v_main_sql := v_main_sql || ' LIMIT ' || p_page_size || ' OFFSET ' || p_page_offset;
-- Run queries
EXECUTE v_count_sql INTO page_total;
OPEN ref_orders FOR EXECUTE v_main_sql;
END;
$$;