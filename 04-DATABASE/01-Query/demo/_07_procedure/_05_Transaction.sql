CREATE TABLE logs (
    id SERIAL PRIMARY KEY,
    action TEXT,
    created_at TIMESTAMP DEFAULT NOW()
);
INSERT INTO logs(action)
VALUES ('start'),
    ('test insert'),
    ('delete something'),
    ('update user');
-- Câu lệnh tạo PROCEDURE tự động thêm bản gih
CREATE PROCEDURE do_transaction(do_fail boolean) --
LANGUAGE plpgsql --
AS $$ BEGIN -- Thao tác 1: thêm dòng log
INSERT INTO logs(action)
VALUES ('transaction start');
-- Thao tác 2: Tùy chọn gây lỗi
IF do_fail THEN -- Lỗi chia cho 0
PERFORM 1 / 0;
END IF;
RAISE NOTICE 'Giao dịch thành công!';
EXCEPTION
WHEN division_by_zero THEN RAISE NOTICE 'Lỗi chia cho 0!';
-- Ta hoàn toàn có thể throw ra một EXCPETIOn
WHEN OTHERS THEN RAISE NOTICE 'Lỗi không xác định!';
END;
$$;
-- GỌi
CALL do_transaction(false);
-- NOTICE: Giao dịch thành công!
CALL do_transaction(true);
-- NOTICE: Lỗi chia cho 0!