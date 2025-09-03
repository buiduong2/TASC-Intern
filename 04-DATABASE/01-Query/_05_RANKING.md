## + Ranking : ROW_NUMBER, RANK, DENSE_RANK

## Window FUNCTION VS GROUP BY

-   **Định nghĩa**

-   Các hàm window là gì
    -   Các hàm window thực hiện một hoạt động nào đó trên tập hợp các hàng có liên quan đến hàng hiện tại
    -   Chúng tương tự như các hàm tổng hợp `GROUP BY` ở chỗ chúng trải dài trên nhiều hàng. Nhưng thay vì các hàng được nhóm lại như một ở `GROUP BY` thì các hàng ở WINDOW Function vẫn xuất hiện ra
-   **Cú pháp**

```sql
 WIN_FUNC(arg1,arg2,...) OVER (
    [ PARTITION BY biểu_thức_phân_vùng ]
    [ ORDER BY biểu_thức_sắp_xếp [ASC | DESC ] [NULL {FIRST | LAST} ] ]
)


-- Trong cú pháp này
```

-   Mệnh đề `PARTITION BY`

    -   mệnh đề `PARTITION BY` chia các hàng thành nhiều nhóm hoặc nhiều phân vùng mà hàm window được áp dụng
    -   Mệnh đề` PARTIION BY` là tùy chọn. Nếu ta bỏ qua mệnh đề `PARTITION BY`, hàm window sẽ coi toàn bộ tập kết quả là một vùng duy nhất

-   Mệnh đề `ORDER BY`

    -   Mệnh đề `ORDER BY` chỉ định thứ tự các hàng trong mỗi phân vùng mà chức năng cửa sổ được áp dụng
    -   Mệnh đề `ORDER BY` sử dụng tùy chọn `NULL FIRST` hoặc `NULL LAST` để chỉ định xem các giá trị null có thể nằm trên đầu hay về cuối trong cùng tập kết quả. Mặc định tùy chọn là `NULL LAST`

-   Thuộc chủ đề về `Window Function` trong SQL, Các hàm `ROW_NUMBER()` `RANK()` và `DENSE_RANK()` đều dùng để `xếp hạng các hàng trong một tập dữ liệu`, thường đi kèm với `OVER(...)` để xác định cách phân vùng `PARTIION` và thứ tự sắp xếp `ORDER BY`

-   Mệnh đề `ROWS BETTWEEN` | `RANGE BETWEEN`

    -   Xác định `cửa sổ WINDOW` xung quanh mỗi hàng -> hàng nào sẽ được tính toán cho mỗi hàng hiện tại
    -   Nếu `ko viết gì`

        -   Với các hàm `Aggregation` như `SUM` , `AVG` , `MAX`

            -   Mặc định là `RANGE BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW` -> tính từ đầu partition đén hàng hiện tại

        -   Với các hàm `RANKING` (`ROW_NUMBER` ,`RANK`)
            -   Mặc định chỉ tính trên toàn bộ PARTIION, nên `thường không dùng FRAME`

    -   Khi có `ORDER BY` thì cái này mới được áp dụng - Thường cái này với mục đích tính toán thôi. Còn các hàm ko tính toán ko ảnh hưởng
    -   Mục tiêu là tính toán tích lũy v..v..
    -   Ngoài ra hàm `LAST_VALUE()` đôi khi có bị ảnh hưởng nếu không set FRAME đúng

-   **Lợi ích**

    -   `Không mât dữ liệu dòng`: Khác với GROUP BY mỗi hàng vẫn còn
    -   `Tính toán dựa trên tập con`: theo nhóm, theo thứ tự, hoặc theo cửa sổ tùy chỉnh
    -   `Dễ kết hợp với các hàm RANKING , LEAD LAG ` để phân tích dữ liệu

## ROW_NUMBER

-   Hàm `ROW_NUMBER()` là một hàm window gán một số nguyên liên tiếp cho mỗi hàng trong tập kết quả.

```sql
ROW_NUMBER() OVER(
    [PARTITION BY column_1,column_2,...]
    [ORDER BY column_3,column_4,...]
)
```

-   Tập hợp các hàng mà hàm ROW_NUMBER hoạt động được gọi là hàm window.

-   Mệnh đề `PARTITION BY` chia các windown thành các tập hoặc phân vùng nhỏ hơn. Nếu ta chỉ chỉ định mệnh đề `PARTITION BY`, số hàng ở mỗi phân vùng `bắt đầu bằng 1`. Bởi vì mệnh đề `PARTITION BY` là tuỳ chọn cho hàm `ROW_NUMBER`, dó đó ta có thể bỏ qua nó. Lúc này, hàm `ROW_NUMBER` sẽ coi toàn bộ các windown nằm trong một phân vùng

-   Mệnh đề `ORDER BY` bên trong mệnh đề `OVER` xác định thứ tự sắp xếp của các cột được gán

## RANK

-   Hàm RANK gán thứ hạng cho mỗi hàng trong một phân vùng kết quả.

-   Đối với mỗi phân vùng, thứ hạng của hàng đầu tiên là 1. Hàm RANK thêm số lượng hàng bị ràng buộc vào thứ hạng của hàng tiếp theo. Do đó cấp bậc có thể ko tuần tự. Ngoài ra hàng có cùng giá trị sẽ có cùng thứ hạng

```sql
RANK() OVER(
    [PARTITION BY partition_expression...]
    ORDER BY sort_expression [ASC | DESC ]
)

-- Mệnh đề PARTITION BY phân phối các hàng của tập kết quả thành các phân vùng mà hàm RANK được áp dụng

-- Sau đó, mệnh đề ORDER BY chỉ định thứ tự các hàng trong mỗi phân vùng mà hàm được áp dụng

```

## DENSE_RANK

-   DENSE_RANK chỉ định thứ hạng cho mỗi hàng trong phân vùng của tập kết quả. Khác với hàm hàm RANK, hàm DENSE_RANK trả về các giá trị xếp hạng liên tiếp. (Tức là có trùng nhau cũng ko đc bỏ thứ hạng nào cả)

-   Đối với mỗi phân vùng, hàm DENSE_RANK trả về cùng một thứ hạng cho các hàng cùng giá trị.

-   Phần sau đây hiển thị cú pháp của hàm DENSE_RANK

```sql
DENSE_RANK OVER(
    [PARTITION BY biểu_thức_phân_vùng,...]
    ORDER BY biểu_thức_sắp_xếp [ASC | DESC]
)
```

-   Hàm DENSE_RANK được áp dụng cho mọi hàng trong mỗi phân vùng được xác định bởi mệnh đề PARTITION BY, theo thứ tự được chỉ định bởi mệnh đề ORDER BY. Nó sẽ đặt lại thứ hạng khi vượt qua ranh giới phân vùng.

-   Mệnh đề PARTITION BY là tuỳ chọn. Nếu ta bỏ qua nó, hàm DENSE_RANK sẽ coi toàn bộ tập kết quả là một phân vùng duy nhất.
