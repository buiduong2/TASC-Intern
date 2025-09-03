-- Out sẽ làm thay đổi biến được truyền vòa trong PROCEDURE
-- Tạo ra hàm tính tổng của 2 tham số truyền vào. Và trả về tham số thứ 3
CREATE PROCEDURE calc_sum(IN a INT, IN b INT, OUT total INT) --
LANGUAGE plpgsql --
AS $$ --
BEGIN total := a + b;
END;
$$;
-- Tiến hành gọi bên ngoài như một SELECT 
-- Các OUT PARMEATER nên gọi NULL
CALL calc_sum(1, 2, NULL);
--  KQ : trả về một bảng có cột các là các paramter out và kết quả của chúgn
-- Để có thể sử dụng biến
-- Dùng DO Block, hoặc bất cứ Block Code nào
DO $$ --
DECLARE --
    my_total INT;
BEGIN --
CALL calc_sum(1, 2, my_total);
RAISE NOTICE 'Kết quả là : %',
my_total;
END;
$$
-- KQ: NOTICE:  Kết quả là : 3
