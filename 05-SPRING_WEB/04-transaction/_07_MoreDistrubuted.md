## Eventual Consistency (tính nhất quán cuối cùng)

- trong các hệ thống phân tán phức tpaj như kiến trúc microserice, việc tủy trì `tính nhất quán tức thì (immediate consitency)` như 2PC là rất khó khăn. Thay vào đó, nhiều hệ thống chấp nhận `tính nhất quán cuối cùng`
    - `Tính nhất quán tức thì` đảm bảo rằng mọi bản sao của dự liệu đều giống hệ nhau tại mọi thời điểm
    - `Tính nhất quán cuối cùng` là một mô hình nơi dữ liệu có thể không đồng bộ trong khoảng thời gian ngắn sau khi thay đổi, nhưng cuối cùng sẽ trở lên nhất quán

- Mô hình saga hoạt động dựa trên nguyên lý này. Khi một giao dịch đang diễn ra , có thể có các trạng thái không nhất quán tạm thời trên các service khác nhau. VD sau khi trừ tiền khách hàng nhưng trước khi cập nhật kho hàng, hệ thống đang ở trạng thái không nhất quán. Tuy nhiên khi tất cả các giao dịch cục bộ đã hoàn tất hoặc được bù đắp, hệ thống sẽ trở về trạng thái nhất quán


## Vai trò của Message Queue trong saga

- Để triển khai mô hình Saga một cách hiệu quả, đặc biệt với `Choreography` các hệ thống thường sử dụng `messageQueue` (hàng đợi tinh nhắn) hoặc `Event Bus`
    - `Cách hoạt động `: khi một giao dịch cục bộ hoàn tất, service sẽ gửi một tin nhắn (sự kiện ) tới hàng đợi tin nhắn chung. Các service khác quan tâm đến sự kiện đó sẽ lắng nghe và lấy tin nhắn ra để xử lý giao dịch cục bộ của riêng chúng

- `Lợi ích`
    - `Giảm sự phụ thuộc`: các service không nhất thiết phải biết về nhau, chúng chỉ cần giao tieps thông qua các sự kiện

    - `Xử lý bất đồng bộ`: các service có thể xử lý tin nhắn bất cứ khi nào chúng rảnh giúp cải thiện hiệu suất

    - `Đáng tin cậy`: message Queue có thể đảm bảo tin nhắn được gửi và xử lý, ngay cả khi một service tạm thời ko hoạt động

    