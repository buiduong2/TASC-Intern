## Mức độ cô lập (isolation levels)

- `Tính cô lập Isolation` là một trong bốn tính chất của `ACID` quan trọng nhất. nó quyết định mức độ mà một transaction đang chạy có thể nhìn thấy dữ liệu của transaction khác chạy đồng thời

- Các hệ quản trị CSDL cung cấp nhiều mức độ cô lập khác nhau. Việc chọn mức độ cô lập phù hợp rất quan trọng vì nó ảnh hưởng đến hiệu suất và tính đúng đắn của ứng dụng

    -  `READ UNCCOMMIED` (đọc dữ liệu chưa commit) : mức độ thấp nhất. Một transaction có thể đọc dữ liệu từ một transaction khác chưa được commit. Điều này có thể dẫn đến `"dirty Readd"`(đọc dữ liệu bẩn), tức là đọc phải dữ liệu mà sau đó bị hủy bỏ (rollback)

    - `READ COMMITED (đọc dữ liệu đã commit)`: mức odojd phổ biến nhất. Một transaction chỉ có thể đọc dữ liệu đã được commit. Vì nó ngăn chặn `dirty Reads` nhưng có thể xảy ra`NO-repreatable reads (đọc không nhất quán)`. VD ta đọc một dòng dữ liệu sau đó transaction khác commit thay đổi dòng đó, và khi đó ta đọc lại, giá trị đã khác

    - `Repeatable READ (đọc lặp lại)`: ngăn chặn tất cả `dirty reads` và `non-repeatable reads` một transaction sẽ đọc cùng một giá trị nếu nó đọc lại cùng một dòng dữ liệu. Tuy nhiên mức độ này có thể xảy ra `phantom reads (đọc dữ liệu ma)`. VD ta đọc một tập hợp các dòng dữ liệu, sau đó một transaction thêm một dòng mới vào tập hợp đó, và khi ta đọc lại số lượng dòng đã thay đổi

    - `Serializable (tuần tự hóa)`: mức độ cao nhất, đảm bảo transaction được thực hiện như thể chúng chạy tuần tự, không có sự đồng thời. Mức độ này ngăn chặn tát cả các vấn đề trên (dirty reads, non-repeatable reads, phantom reads) nhưng có thể làm giảm hiệu suất đáng kể.