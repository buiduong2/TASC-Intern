1. Sản phẩm bán chạy nhất
- Đề: Tìm sản phẩm nào có tổng số lượng bán ra nhiều nhất.
- Kỹ năng: SUM, GROUP BY, ORDER BY, LIMIT.
-----------------------------
2. Top N sản phẩm bán chạy nhất theo doanh thu
- Đề: Liệt kê top 3 sản phẩm có doanh thu cao nhất (doanh thu = quantity * unit_price).
- Kỹ năng: Aggregation, ORDER BY, LIMIT.
-----------------------------
3. Doanh thu theo ngày
- Đề: Tính tổng doanh thu cho từng ngày (order_date) chỉ tính các đơn hàng COMPLETED.
- Kỹ năng: WHERE filter, GROUP BY theo ngày.
-----------------------------
4. Doanh thu theo danh mục sản phẩm
- Đề: Với mỗi category (VD: Laptop, Điện thoại, Phụ kiện), tính tổng doanh thu.
- Kỹ năng: JOIN với bảng products, GROUP BY theo category.
-----------------------------
5. Khách hàng mua nhiều nhất
- Đề: Tìm customer_id có số lượng sản phẩm mua nhiều nhất (tính tổng quantity).
- Kỹ năng: GROUP BY theo customer, SUM, ORDER BY.
-----------------------------
6. Sản phẩm chưa từng được bán
- Đề: Liệt kê những sản phẩm trong bảng products mà chưa hề xuất hiện trong order_items với trạng thái đơn hàng COMPLETED.
- Kỹ năng: LEFT JOIN hoặc NOT IN.
-----------------------------
7. Đơn hàng có tổng giá trị cao nhất
- Đề: Tìm đơn hàng (order_id) nào có tổng tiền (SUM(quantity*unit_price)) cao nhất.
- Kỹ năng: GROUP BY order_id, ORDER BY.
-----------------------------
8. Tỷ lệ hủy đơn
- Đề: Tính % số lượng đơn hàng CANCELLED so với tổng số đơn hàng.
- Kỹ năng: COUNT, CASE WHEN, phép chia.
-----------------------------
9. Doanh thu trung bình mỗi đơn
- Đề: Tính doanh thu trung bình trên một đơn hàng (chỉ tính COMPLETED).
- Kỹ năng: AVG trên SUM per order (dùng subquery hoặc CTE).
-----------------------------
10. Tháng nào bán được nhiều hàng nhất
- Đề: Tính tổng doanh thu theo tháng, sau đó tìm tháng có doanh thu cao nhất.
- Kỹ năng: DATE functions (EXTRACT(MONTH FROM order_date)), GROUP BY theo tháng.