# Hệ thống xử lý log

## Thành phần chính

-   **LogQueryManager**  
    Trung tâm điều phối, đóng vai trò **“trái tim”** của hệ thống. Quản lý toàn bộ luồng xử lý từ đầu đến cuối.

-   **FileReaderService**  
    Đảm nhận việc **đọc file log**, chia thành các **chunk** (trunk) và đưa dữ liệu vào hàng đợi (Queue).

-   **LogProcessor**  
    Xử lý logic tìm kiếm và lọc log trong từng chunk. Bao gồm:

    -   Xử lý theo **nhóm chunk** (pre-process).
    -   Xử lý chi tiết **từng dòng log** trong chunk.

-   **FileWriterService**  
    Ghi kết quả từng chunk ra các **file tạm** (chunk file), sau đó có phương thức **merge** để gộp thành kết quả cuối cùng.

---

## Luồng hoạt động

1. **Thread Main** khởi chạy và gọi phương thức trong **LogQueryManager**.
2. **LogQueryManager** tạo một **thread đọc file** (sử dụng FileReaderService), đẩy các chunk log vào Queue.
3. Một **thread khác** lắng nghe Queue và tạo nhiều **thread con** để xử lý từng chunk.
4. Mỗi **thread chunk** sẽ:
    - Thực hiện xử lý nhóm log (nếu có).
    - Xử lý chi tiết từng dòng log.
    - Ghi kết quả ra file tạm.
5. Sau khi toàn bộ chunk được xử lý, **FileWriterService** tiến hành **merge** các file tạm thành **file kết quả cuối**.

---

## Kiến trúc xử lý song song

-   Áp dụng mô hình **Producer - Consumer** để đọc và xử lý log.
-   Dữ liệu được chia nhỏ theo chunk để **đa luồng** xử lý.
-   Đảm bảo **tách biệt rõ trách nhiệm**:
    -   Đọc file.
    -   Xử lý log.
    -   Ghi và hợp nhất kết quả.
