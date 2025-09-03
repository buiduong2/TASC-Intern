-- Ví dụ tạo ra một PROCEDURE để in ra một dòng log
CREATE PROCEDURE hello_world() --
LANGUAGE plpgsql --
AS $$ --
BEGIN --
RAISE NOTICE 'Hello, World'
END;
$$;
-- Khi cần thì gọi
CALL hello_world();
-- KQ
-- NOTICE:  Hello, World
-- CALL
-- Query returned successfully in 55 msec