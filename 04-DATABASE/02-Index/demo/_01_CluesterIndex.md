## câu hỏi thật

### Phân biệt cluester index và non-cluster index ?

-   `Clustered Index`

    -   Dữ liêu vật lý của bảng được sắp xếp lại : theo thứ tự của index
    -   Chỉ có duy nhất một clustered index cho mỗi bảng - Thường là khóa chính nhuwg ko bắt buộc
    -   Truy vấn theo đúng cột Clusterd index sẽ rất nhanh (Vì dữ liệu nằm liền kề trên đĩa)

-   `Non-Clusterd Index`
    -   Chỉ là một bản đồ phụ (cấu trúc riêng) chứa khóa + Địa chỉ dòng dữ liệu trên đĩa
    -   Có thể tạo nhiều non-clsuteredIndex trên nhiều cột hoặc kết hợp nhiều cột
    -   Không ảnh hưởng thứ tự vật lý
    -   Truy vấn đúng index nhan hơn full-scan rất nhiều. Nhưng tốn dung lượng lưu trữ cho các index

## So sánh về perfomance khi sử dụng 2 loại index này ? Giải thích nguyên nhân

-   CLusterd Index;
    -   Tốc độ truy vấn
        -   Rất nhanh theo cột của clusterd Index (thường là khóa chính)
        -   Vì dữ liệu thật được sắp xếp liền mạch trên đĩa, đọc một loạt dòng gần nhau rất hiệu quả
        -   Tốc độ ghi
            -   Có thể `châm hơn` nếu dữ liệu mới cần chen vào giuawx vì phải di chuyển các dòng dữ liệu thật trên đĩa để giữ thứ tự
-   Non-clusted Index

    -   Tốc độ truy vấn

        -   Rất nhanh với các truy vấn phù hợp với INDEX
        -   Cần thêm bước tra bản đồ để tìm địa chỉ dữ liệu thật rồi mới đọc dòng đó trên đĩa
        -   Nếu chỉ cần lấy khóa (PRIMARY KEY || INDEX COLUMN) hoặc số lượng dòng (COUNT), non-clusted Index rất nhanh. Nếu lấy nhiều cột, có thể chậm hơn clusterd index một chút (vì phải nhảy nhiều nơi trong đĩa)

    -   TỐc độ ghi
        -   Việc thêm xóa sửa dòng `không phải di chuyển dữ liệu thật trên đĩa` chỉ cần cập nhật lịa bản đồ phụ nên nhanh hơn so với Clusterd Index khi dữ liệu bị chen vào giữa

-   Nguyên nhân Performance tăng
    -   Dữ liệu thật nằm liền mạch 0> truy vấn đọc nhiều dòng liên utcj sẽ nhanh hơn , nhất là với BETWEEN
    -   Non-clusted Index: Truy vấn khóa thì nhanh. Nhưng đọc nhiều dòng rải rác sẽ chậm hơn, vì phải "nhảy" nhiều làn (Seek) để lấy từng dong

### Có giới hạn việc đánh bao nhiêu index trong 1 table hay không ? Giải thích

-

-   Với `Clusterd Index`

    -   Mỗi bảng chí có `một `. Vì dữ liệu vật lý trên đĩa chỉ có thể sắp xếp theo `một` quy tắc duy nhất
    -   Không thể "clone" cho mỗi kiểu sắp xếp -> tốn qua nhiều dung lượng và ko thực tế

-   Với `Non-clustered Index`

    -   Chỉ Là các bản đồ phụ (mapping). Chỉ lưu key và "địa chỉ" dòng dữ liệu gốc trên đĩa. nên lưu bao nhiêu cũng ok
    -   Không phải bản sao của dữ liệu thật -> Nhẹ hơn rất nhiều so với cả bảng
    -   Không thay đổi dữ liệu vật lý
    -   Có thể tạo nhiều cột, kết hợp nhiều cột
    -   NÓ vẫn được lưu trữ trên đĩa (các file riêng biệt) . RAM Có hạn. Và ko bền vững. Đảm bảo tính đồng bộ. Thực tế lưu cả RAM và đãi (cache (tạm lưu))

-   Giới hạn
    -   nhiều Index > tăng dung lượn
    -   Làm `chậm thao tác ghi` vì mỗi lần thay đổi dữ liêu jcasc INdex phải cập nhật theo
    -   Vì vậy chỉ nên đánh index khi thực sự cần thiết và thường xuyên
    -   Tối ưu truy vấn khó hơn (Database phải chọn giữa nheieuf INdex)

### Việc đánh index dựa trên cơ sở nào ?

-   Clustered Index
    -   Chi phí UPDATE rất dắt vì phải dịch chuyển dữ liệu + cập nhật các non-clusterd INdex tương ứng

-   Tạo INDEX:

    -   Chỉ nên `tạo index` cho các cột (hoặc tổ hợp các cột) mà chúng ta `truy vấn  WHERE , ORDER BY , JOIN thường xuyên và thực sự cần tăng tốc`
    -   `tránh tạo` index trên các cột ít dùng, hoặc cột có giá trị lặp lại nhiều (VD: giới tính, status)

-   Việc bỏ index
    -   Việc đọc ghi quá chậm
    -   Truy vấn vẫn bị` FULL_Scan` tức là INdex sai
    -   Các index ít được tận dụng
    -   Thay đổi mô hình truy vấn (truy vấn mới phát sinh, kiểu lọc/ sắp xếp khác)

    -   Nếu đã có một Composite index mà có column ở đầu thì không cần đánh đơn cột đó nữa

### Làm thế nào để biết 1 câu query đã sử dụng index hay chưa ?

-Có thể `xem metadata/statistics của hệ quản trị` Thực tế nhất là dùng `EXPLAIN` hoặc `EXPLAIN ANALYZE`

```sql
EXPLAIN SELECT * FROM users WHERE email = 'abc@gmail.com';
```

-   Database sẽ trả về kế hoạch thực thi (query plan)
-   Nếu dòng nào ghi là "Index Scan" hay "INdex Only Scan" nghĩa là truy vấn đã dùng INdex
-   nếu chỉ thấy "Seq Scan" (Sequantial scan), tức là tủy vấn `không dùng index` mà nó đọc toàn bộ bảng

-   KHi mà `Type != NULL` và `KEY != NULL` hay `POSSIBLE_KEYs != NULL` ta biết ta đã áp dụng được index

## Định nghĩa riêng

-   **Index là gì**

-   Index trong database dùng để làm gì ?

    -   Tăng tốc dữ liệu
    -   INdex: (chỉ mục) giúp tăng tốc truy vấn (SELECT) đặc biệt là các truy vấn tìm kiếm SEARCH ,Sắp xếp (ORDER BY ) , Lọc (WHERE )
    -   Index hoạt động gióng như mục lục của cuốn sách - NÓ giúp database xác định vị trí các dòng dữ liệu nhanh hơn thay vì duyệt từng dòng 1 ,

-   **Logic-Non-CLusterd-index**

    -   Chỉ các cấu trúc dữ liệu bổ sung
    -   Tạo ra `một cấu trúc dữ liệu riêng biệt` kieur như một bảng nhỏ chứa (giá trị key, Tham chiếu đến địa chỉ vật lý của dòng dữ liệu)
    -   Dữ liêu vật lý các thứ tự,.. `ko bị tha đổi`
    -   Truy vấn database sẽ;

        -   tra trong index logic để biết địa chỉ dòng cần tìm
        -   Nhảy tới dòng trên ổ cứng (theo địa chỉ vật ls)

-   **Vật lý (CLusted Index)**

    -   Sắp xếp và lưu trữ dòng dữ liệu trên `ổ cứng thật`

-   **Cấu trúc dữ liệu của Index là gì**

-   Khi chúng ta tạo `index` . Database sẽ tạo ra một `cấu trúc dữ liêu tối ưu cho tìm kiếm (chứ ko phải giống tọa bảng thông thường)`

-   2 cấu trúc dữ liệu được sử dụng nhiều nhất(B-Trê index, HashIndex)

-
-   Cả non và ko non clusterd idnex đều có thể lưu theo kiểu `B-tree` ko phải chí có non-Clsuterred

    -   B-tree cho phép Tìm kiếm, cHÈn xóa, O LogN
    -   Giảm khả năng phân mảnh dữ liệu nhờ khả nawgn cân bằng (balance tree)
    -   Khi lưu cũng tuân theo quy tắc cây. Để giảm việc di chuyển con trỏ

-   **Tại sao INdex lại giúp truy vấn nhanh hơn**

-   Nếu `không có index`. Mỗi lần tìm một giá trị WHERE ma_Sach = 100. Database phải đọc tuần tự từng dòng - GỌi là `full table scan` -> Rất chậm khi bảng lớn

-   Khi `có INdex`: Giả sử là `B-Trê` Index (Cây nhiều nhánh, Các giá trị lưu theo thứ tự). Để tìm ta chỉ `cần duyệt qua các Node theo nhánh. Số bước giảm rất nhiều`. Tương tư như chúng ta tra từ điển. Nếu sắp xép ABC ta tra L chỉ cần nhảy tới đúng đoạn L. KO phải đọc từ A - Z
-   `HashIndex` tạo ra bảng băm. Cao cho truy vấn chính xác. Nhưng không thể sử dụng so sánh nên ko hỗ trợ ORDER BY

-   Chúng ta hoàn toàn có thể chỉ định kiểu index

```sql
CREATE INDEX tên_index ON tên_bảng (cột) USING btree;
CREATE INDEX tên_index ON tên_bảng (cột) USING hash;
-- Không ghi thì mặc định là B-Tree
```

-   **Khi nào INdex hoạt động tốt trong truy vấn WHERE**

-` Chỉ hoạt động khi điều kiện WHERE phù hợp với cấu trúc của INdex`

-   Thông thường

    -   Chúng ta có INDEX trên cột `A` thì truy vấn `WHERE A = ` , `WHERE A > ...`, `WHERE < ...` đều có thể dùng INDEx
    -   Các điều kiện phức tạp hoặc kết hợp nhiều cột hoặc sử dụng hàm `LOWER(name) = 'alice` `có thể` khiến DB ko tận dụng được index hoặ kém

-   **Index nhiều cột**

```sql
CREATE INDEX idx_name_age ON users(name, age);
```

-   `SHOW INDEX` ta nhận 2 dòng idx_name_age giống nhau . nhưng có Seq_in_index khác nhau (name là cột 1, age là cột 2)
-   Thực chất là 1 Index

    -   Có nhiều cột làm key
    -   Thứ tự các cột trong Index cũng quan trọng

-   Câu truy vấn trên sẽ tối ưu truy vấn

```sql
WHERE name = ...
WHERE name = ... AND age = ...
WHERE name = ... AND age > ...

-- Không tối ưu:
WHERE age = ...
WHERE age = ... AND name = ...
```

-   Đó gọi là `prefix` của compsite index
    -   Chỉ truy vấn bắt đầu từ cột đầu tiên mới hiệu quả
