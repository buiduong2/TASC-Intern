SELECT s.product_id, s.total_quantity, SUM(pi.quantity) AS calc_sum
FROM stock s
JOIN purchase_item pi ON pi.product_id = s.product_id
JOIN purchase p ON p.id = pi.purchase_id
WHERE p.status = 'ACTIVE'
GROUP BY s.product_id, s.total_quantity
ORDER BY s.product_id;