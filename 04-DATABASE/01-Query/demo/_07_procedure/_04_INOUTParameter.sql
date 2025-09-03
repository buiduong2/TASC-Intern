-- Viết một hàm nhận vào tham số và tăng tham số đó lên 1
CREATE PROCEDURE increase_num (INOUT num INT) --
LANGUAGE plpgsql --
AS $$ --
BEGIN --
num := num + 1;
END;
$$;
--  Gọi bên ngoài
CALL increase_num(10);
-- Trả về bảng các cột là các tên parameter OUT và hàng là KQ
-- Gọi bằng Block Code
DO $$ --
DECLARE --
    value INT;
BEGIN value := 10;
CALL increase_num(value);
RAISE NOTICE 'Kết quả sau khi tăng là : %',
value;
END;
$$;
--NOTICE:  Kết quả sau khi tăng là : 11