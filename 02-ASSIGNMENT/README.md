# So sánh 3 cách viết Logger

## Danh sách phương pháp

1. **IndexedCacheLogger** (Logger-demo)

    - Ý tưởng: Sử dụng cache + index mô phỏng cơ chế database để **tái query nhanh nhiều lần**.
    - Cơ chế: Dùng `RandomAccessFile.seek()` để nhảy đến các offset dòng.
    - Vấn đề:
        - Seek liên tục gây **quá tải I/O**.
        - Chia offset dòng đầu/cuối sinh bug, cần chuẩn hóa phức tạp.
    - Hiệu quả:
        - Với file nhỏ: chạy được.
        - Với file lớn: tìm kiếm **rất lâu**, tốn **bộ nhớ cao**.

2. **StreamLogger** (Logger-batch)

    - Ý tưởng: Dùng **Java Stream API (Lazy)** để xử lý log **một lần đọc file duy nhất**.
    - Cơ chế: `Files.lines(path).parallel()` → filter → write.
    - Ưu điểm:
        - **Không cache, không index**.
        - Code **ngắn gọn, dễ hiểu**.
        - **Song song tự nhiên** nhờ parallel stream.
        - Bộ nhớ nhẹ, tốc độ trung bình **~600ms**.
    - Nhược điểm:
        - Không tái sử dụng query.
        - Chỉ phù hợp với **query 1 lần duy nhất**.

3. **ChunkedLogger** (final-answer)
    - Ý tưởng: Chia file log thành các **chunk** (theo số dòng).
    - Cơ chế:
        - **FileReaderService** đọc tuần tự.
        - Đẩy chunk vào Queue.
        - **LogProcessor** xử lý từng chunk song song.
        - **FileWriterService** ghi file tạm và merge.
    - Ưu điểm:
        - **Bộ nhớ ổn định** (chỉ giữ chunk ngắn hạn, rồi xóa).
        - Hỗ trợ xử lý song song theo chunk.
        - Có thể mở rộng xử lý thêm (pre-process, line-by-line).
    - Nhược điểm:
        - Cần thiết kế **phức tạp hơn** (Producer-Consumer, Queue, merge).
        - Bộ nhớ dùng tạm thời **tương đương IndexedCacheLogger** nhưng quản lý tốt hơn.

---

## Bảng so sánh

| Tiêu chí               | IndexedCacheLogger            | StreamLogger                | ChunkedLogger                       |
| ---------------------- | ----------------------------- | --------------------------- | ----------------------------------- |
| **Mục tiêu**           | Tái query nhiều lần           | Query 1 lần nhanh gọn       | Query 1 lần, xử lý song song        |
| **Kỹ thuật chính**     | Cache + Index + RandomAccess  | Stream API (Lazy, Parallel) | Chunk + Queue + Producer/Consumer   |
| **Tốc độ**             | Rất chậm với file lớn         | ~600ms (rất nhanh)          | Nhanh (tùy cấu hình chunk)          |
| **Bộ nhớ**             | Cao (cache giữ nhiều dữ liệu) | Rất thấp                    | Trung bình (chỉ giữ chunk ngắn hạn) |
| **Độ phức tạp code**   | Cao, nhiều bug offset         | Thấp, dễ code               | Trung bình - Cao (đa luồng)         |
| **Khả năng tái query** | Có (ưu điểm chính)            | Không                       | Không (thiết kế cho 1 lần query)    |
| **Độ ổn định**         | Thấp (bug offset, I/O nặng)   | Cao                         | Cao nếu quản lý tốt queue           |
| **Ứng dụng thực tế**   | Thử nghiệm nhỏ                | Production query 1 lần      | Production query 1 lần + mở rộng    |
