# Các thành phần và chức năng chính trong hệ thống Microservice

-   `1. Miroservice (các dịch vụ độc lập)`

    -   `Thành phần`: là cốt lõi của kiến trúc. Mỗi MIcroservice là một ứng dụng nhỏ, tự chứa, tập trung vào một `khả năng nghiệp vụ` cụ thể (VD dịch vụ đơn hàng, Dịch vụ người dùng, Dịch vụ thanh toán)
    -   `Chức năng`
        -   Thực hiện các `logic nghiệp vụ` riêng
        -   Duy trì `kho dữ liệu riêng ` (hoặc mô hình dữ liệu) của mình
        -   `Giao tiếp` với các dịch vụ khác thông qua API (thường là REST/HTTP hoặc gRPC)
        -   Có thể được `phát triển và triển khai dộc lập`

-   `2. API gateway (cổng API)`

    -   `Thành phần`: là điểm truy cập duy nhất (SIngle Entry Point) cho tất cả các yêu cầu từ bên ngoài (client ứng dụng di động, trình duyệt)

    -   `Chức năng`
        -   `Định tuyến (routing)` : Chuyển tiếp yêu cầu của client đến microservice thích hợp
        -   `Tổng hợp (Aggregation)` : Có thể tổng hợp kết quả từ nhiều dịch vụ thành một phản hồi duy nhất cho client (Pattern Backend-for fronend BFF)
        -   `Bảo mật`: xử lý xác thực / ủy quyền, giới hạn tốc độ (Rate limiting)
        -   `Giảm tải` : Giảm gánh nặng cho các client bằng cách xử lý các vấn đề giao tiếp phức tạo với các microservice nội bộ

-   `3. Service discovery (khám phá dịch vụ)`

    -   `Thành phần`: là cơ chế hoặc kho lưu trữ giúp các dịch vụ tìm thấy vị trí (địa chỉ mạng) của nhau một cách tự động. Thường bao gồm `Registry Server` (VD eurkea, Consul, ZooKeeper)

    -   Chức năng"
        -   `Đăng kí (Registration)`: Khi một dịch vụ mới khởi động, nó sẽ tự đăng kí địa chỉ mạng của mình với Service điscovery
        -   `khám phá (discovery)`: các dịch vụ khác hoặc API Gateawy truy vấn Registry để tìm địa chỉ dịch vụ cần giao tiếp . Điều này cực kì quan trọng vì các microservice đượ triển khai động trên các host khác nhau

-   `4. Inter-service Communication (GIao tiếp giữa các dịch vụ)`

    -   `Thành phần`: Cơ chế mà Microservice sử dụng để trao đổi dữ liueej -`Chức năng`: - `Đồng bộ (Synchronous)`" phổ biến nhất là sử dụng `REST/HTTP` hoặc `gRPC` dịch vụ gọi phải chờ phản hồi - `Bất đồng bộ (Asynchornous)`: thường sử dụng `Message Broker` hoặc `EventBus` (VD kafka RabbitMQ). Dịch vụ gửi `publiser` không cần chờ phản hồi, tăng tính linh hoạt và khả năng mở rộng

-   `5. Configuration Management (quản lý cấu hình)`

    -   `Thành phần` : một kho lưu trữ tập trung (VD config Server) để quản lý cấu hình (biến môi trường, chuỗi kết nối CSDL, tham số) cho tát cả các dịch vụ

    -   `Chức năng`
        -   `Tập trung hóa`: đảm bảo tất cả các dịch vụ sử dụng CSDL nhất quasnt và có thể cập nhật cấu hình cho toàn bộ hệ thống từ một nơi duy nhất
        -   `Tải lại động`: Cho phép tải lại cấu hình mà không cần khởi động lại

-   `6. Observability (khả năng quan sát)`

    -   `Thành pahafn` : bao gồm 3 trụ cốt chính là `Loggin(ghi nhật kí)`, `Metrics (đô lường)` và `Tracing (truy vết phân tán)`

    -   `Chức năng: `

        -   `Logging`: tập hợp naaht kí từ tất cả các dịch vụ vào một hệ thống tập trung (VD ELK stack / Grafano loki) để dễ dàng gỡ lỗi

        -   `Metrics`: thu thập dữ liệu hiệu suất (CPU, bộ nhớ, thời gian phản hồi) để giám sát trạng thái sức khỏe
        -   `Tracing`: truy vết một yêu cầu duy nhất khi nó đi qua nhiều microservice khác nhau, giúp xác định nút cổ chai và lỗi trong hệ thống phân tán

-   `7. Containerization & Prrchestration (contaainter hóa & điều phối)`

    -   `Thành phần: `

        -   `Containerization (VD docker)`: đóng gói ứng dụng và tất cả các phụ thuộc vào một đơn vị độc lập
        -   `orchestration (VD Kubernetes)`: quản lý, điều phối việc triển khai, mở rộng và tự phục hồi của hàng loạt container

    -   `Chức năng`
        -   `Đóng gói và cô lập`: Đảm bảo dịch vụ chạy nhất quán ở mọi môi trường
        -   `Tự động hóa`: Tự động hóa việc triển khai, cân bằng tải, và tự phục hồi dịch vụ khi gặp lỗi
