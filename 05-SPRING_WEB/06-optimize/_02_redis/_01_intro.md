## Redis là gì ?

- Redis là viết tắt của `REmote Dictionary Server` làm một  thống lưu trữ dữ liệu `nguồn mở` được sử dụng  làm `database` ,`cache` và `messageBroker`. Đặc điểm đặc biệt của Redis là nó lưu trữ dữ liệu trong `RAM` (bộ nhớ truy cập ngẫu nhiên), giúp tốc độ độc/ ghi cực nhanh, thường chỉ tình bằng mili giây hoặc thậm chí nano giây. Điều này làm cho nó trở lên lý tưởng cho các ứng dụng đòi hỏi hiệu năng cao
    - `Tính linh hoạt` Redis hỗ trợ nhiều cấu trúc dữ liệu khác nhau, không chỉ đơn thuần là key-value thông thường
    - `Tốc độ`: việc lưu trữ dữ liệu trong bộ nhớ  cho phép Redis xử lý hàng triệu request mỗi giây
    - `Đơn giản`: Giao diện dòng lệnh CLI của REdis rất dễ học và sử dụng

## cấu trúc dữ liệu chính trong Redis

- Một trong những ưu điểm lớn nhất  của REdis là hỗ trợ nhiều cấu trúc dữ liệu phức tạp. Điều này giúp cho các nhà phát triển giải quyết nhiều vấn đề khác nhau chỉ với một công cụ

    - `String`: cấu trúc đơn giản nhất, lưu trữ một giá trị chuỗi (string) cho một khóa (key). VD `SET mykey "HELLO world"`. Rất hữu ích cho việc lưu các giá trị như sessionId nội dung HTML hoặc số đếm

    - `Hashes`: giống như các đối tượng (Object) hoặc từ điển (dictionary), lưu trữ một tập hợp các trường giá trị (field-value) bên trong một key. VD `HSET user:1000 name "Jhon Doe" age=25`. Thường được sử dụng để lưu thông tin về một đối tượng

    - `Lists` một danh sách các chuỗi theo