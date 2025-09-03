-- VÍ dụ sử dụng IN parameter của Procedure
-- Viết một Procedure sẽ tiến hành nhận vào một chuỗi và trả ra từ "Hello, {str}"
CREATE PROCEDURE say_hello(IN username TEXT) --
LANGUAGE plpgsql --
AS $$ --
BEGIN --
RAISE NOTICE 'Hello, %',
username;
END;
$$;
-- GỌi thử và xem kết quả
CALL say_hello('Duong');
--NOTICE:  Hello, Duong