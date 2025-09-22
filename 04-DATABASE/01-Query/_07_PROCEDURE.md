# PROCEDURE

## Câu hỏi

## PROCEDURE : mục đích khi tạo ra 1 PROCEDURE là gì ?

-   Tự động hóa và gom nhóm các thao tác
-   Tái sử dụng và giảm lặp code
-   Bảo mật và ẩn logic xử lý
    -   Giúp bảo vệ nghiệp vụ, kiểm soát dữ liêu jtruy cập, tránh dò rỉ các thao tác nhạy cảm
-   Xử lý Logic phức tạp trong DB
    -   Xử lý trực tiếp trên DB mà ko cần phải truyền dữ liệu đi ra một bên thứ 3 - Tối ưu dòng code
-   Hỗ trợ transaction (Giao dịch)
    -   Cho phép quản lý `transactino` có thể `COMMIT` hoặc `ROLLBACK` khi có lỗi
-   Giảm tải cho code ứng dụng,
    -   Đóng gói logic ở tầng database
    -   Giảm độ phức tạp cho ứng dụng
    -   tăng tốc độ xử lý (logic chay jowr server ko phải truyền dữ liệu đi)
    -   Dẽ audit bảo mật
    -   Tối ưu team lớn, app nhiều ngôn ngữ (PROCEDURE dùng chung)

## Làm thế nào để sử dụng PROCEDURE trong ứng dụng java.

-   **Các bước để sử dụng PROCEDURE trong ứng dụng Java JDBC**

    -   Sử dụng JDBC Sử dụng đối tượng CallableStatement với việc đăng kí các PAramterMode
    -   Sử dụng Jpa với EntityManager và `createStoredProcedureQuery`
    -   Sử dụng JPARepository với` @Procedure(procedureName = "calc_max")`

## Làm thế nào để sử dụng PROCEDURE trong ứng dụng java.

## 1. PROCEDURE là gì ?

-   **Định nghĩa**

-   `PROCEDURE` là một `đoạn chương trình (block code)` được viết và lưu sẵn trong database

-   Khi cần, chúng ta chỉ cần `gọi tên` procuder là nó sẽ thực thi toàn bộ logic bên trong (Giống như mọi hàm trong lập trình)

-   Thường dùng để:

    -   Thực hiện `nhiều thao tác` cùng lúc (INSERT, UPDATE, DELETE , Kiểm tra dữ liệu, ...)
    -   Tự động hóa nghiệp vụ
    -   Tối ưu, bảo mật logic xử lý

-   **Lợi ích**
    -   tái sử dụng : viết 1 lần gọi nhiều lần
    -   Bảo mật: Giấu logic khởi phía ứng dụng
    -   Tối ưu: Database chạy trực tiếp, nhanh hơn Code ngòaoi
    -   Quản lý: Sửa logic chỉ cần một nơi

## 2. Khác biệt giữa PROCEDURE và FUNCTION

|                | **PROCEDURE**                                       | **FUNCTION**                                                              |
| :------------- | :-------------------------------------------------- | :------------------------------------------------------------------------ |
| Mục đích       | Thực hiện thao tác, không nhất thiết trả về giá trị | Trả về 1 giá trị, có thể dùng trong mệnh đề SELECT                        |
| Trả về giá trị | Không bắt buộc                                      | Bắt buộc                                                                  |
| Gọi bằng       | `CALL`                                              | Có thể gọi trong `SELECT, UPDATE` hoặc đơn thuần `SELECT function_name()` |
| Transaction    | Có thể thực hiện commit/ rollback                   | Không được phép                                                           |

-   `Tóm lại` : nếu chusgn ta cần thực hiện một loạt hành động. hãy sử dụng PROCEDURE. nếu chúng ta cần lấy ra 1 giá trị cụ thể để dùng tiếp, Hãy sử dụng FUNCTION

## 3. Cấu trúc tạo PROCEDURE trong PostgreSQL

-   **Cú pháp tổng quát**

```sql
CREATE PROCEDURE ten_procedure([các tham số])
LANGUAGE plpgsql
AS $$
BEGIN
    -- Xử lý code ở đây
END
$$;
```

-   `ten_procedure` : tên procedure chúng ta tự dặt
-   `[Các tham số]`: có thể có hoặc không, dùng để truyền dữ liệu vào (giống truyền biến vòa hàm)
-   `LANGUAGE plpgsql`: Đa số viết bằng PL/pgSQL (ngôn ngữ lập trình procedural của PostgreSQL)

-   **Ví dụ đơn giản nhất**

```sql
CREATE PROCEDURE hello_world()
LANGUAGE plpgsql
AS $$
BEGIN
    RAISE NOTICE 'Hello, World'
END;
$$;
```

-   Giải thích:
    -   Procedure này không có tham số
    -   Khi gọi sẽ in ra dòng "Hello World" ở phần thông báo (notice) của PostgreSQL

## 4. Cách gọi (chạy) PROCEDURE

-   Sau khi tạo xong, Chúng ta cso thể sử dụng lệnh `CALL` để gọi procedure

```sql
CALL hello_world();
-- KQ:
-- NOTICE:  Hello, world!

```

## 5. Tham số trong PROCEDURE

-   **Tham số IN (truyền vào)**
-   Dùng để truyền dữ liệu từ ngoài vào trong procedure
-   Mặc định mọi tham số đều là `IN`
-   Khi khai báo parameter cần khai báo cả kiểu dữ liệu

```sql
-- VD Procedure nhận vào tên, in ra lời chào
CREATE PROCEDURE say_hello(IN username TEXT)
LANGUAGE plpgsql
AS $$
BEGIN
    -- Sử dụng đặt chỗ trước giống của Javaformat
    RAISE NOTICE 'Hello, % ', username
END;
$$;

-- Gọi
CALL say_hello('Duong');
-- KQ: Hello Duong, Dương
```

-   **Tham số OUT (truyền ra)**

-   Giúp Procedure trả lại dữ liệu sau khi xử lý

```sql
-- VD: PROCEDURE tính tổng 2 số
CREATE PROCEDURE calc_sum(IN a INT, IN b INT, OUT total INT)
LANGUAGE plpgsql
AS $$
BEGIN
    total := a + b;
END;
$$;

-- Tiến hành gọi
CALL calc_sum(3,5,total);
-- sau khi gọi biến total sẽ có giá trị 8
-- Khi dùng một số công cụ như các app client thì giá trị OUT sẽ hiện ra dưới dạng kết quả
```

## 6. Tham số INOUT (vừa truyền vào vừa truyền ra)

-   Dùng IN, nhưng giá trị có thể thay đổi trong PROCEDURE và trả lại ra ngoài

```sql
CREATE PROCEDURE increase_num (INOUT num INT)
LANGUAGE plpgsql
AS $$
BEGIN
    num := num +1;
END;
$$;

-- Gọi
CALL increase_num(10) -- Trả về kết quả 11
```

## 7. Xử lý Transaction trong PROCEDURE

-   Procedure có thể chứa các lệnh như
-   `COMMIT`;
-   `ROLLBACK`;

-   Điều này rất hữu ích để xử lý lỗi, hoặc chỉ COMMIT khi thành công mà thôi

```sql
CREATE PROCEDURE do_transaction()
LANGUAGE plpgsql
AS $$
BEGIN
    -- Giả sử thao tác 1
    INSERT INTO logs(action) VALUES ('start')
    -- Thao tác 2
    -- Nếu mọi thứ OK thì commit
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN ROLLBACK;
END;
$$;
```

## 8. Quản lý PROCEDURE

```sql
-- Xem dan hsasch PROCEDURE
SELECT proname FROM pg_proc WHERE prokind = 'p';

```

```sql
-- Xoas PROCEDURE
DROP PROCEDURE ten_procedure([kiểu tham số]);
```

```sql
-- Để EDIT thì DROP xong CREATE lại
```

## NÓ là gì

## nó dùng trong trường hợp nào

## VỀ nhà

-   Tạo các VD về index của PARTITION

    -   Phải có hơn 1m bản ghi chẳng hạn

-   Về nhà tìm hiểu tại sao . Procdure 1 câu queryr duy nahast. CÓ thể viết ở tầng ứng dụng. Tại sao người ta lại cần viết ở DB
    -   Trong khi đó việc lưu lại cắt 1 phần bô jnhwos
    -   Compile

    - Nếu ko có Entity ko lây được. Nhuwgn vnax map sang DTO bình thường
    - 

- Có bao nhiệu công nghệ lại công nghệ này. 


