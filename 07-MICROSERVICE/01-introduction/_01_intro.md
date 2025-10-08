# Khái niệm về hệ thống microservice

- Hệ thống `microservice` (hay còn gọi là kiến trúc microservice) là một phương pháp phát triển phần mềm , nơi ứng dụng được chia nhỏ thành tập hợp các `dịch vụ nhỏ độc lập` (mỉcroservice). Mỗi dịch vụ này: 
    - `Chạy trong một tiến trình riêng`: và giao tiếp với nhau, thường thông qua các cơ chế nhẹ như giao thức `HTTP/REST` hoặc `message queue`
    - Được xây dựng xung qunah `khả năng nghiệp vụ` cụ thể
    - Có thể được triển khai `độc lập` với các dịch vụ khác
    - Thường sở hữu `CSDL riêng` hoặc ít nhất là một mô hình dữ liệu độc lập. để đảm bảo tính phân tách
    - Có thể được phát triển bằng `các ngôn ngữ lập trình khác nhau` và sử dụng các công nghệ khác nhau (đa đạng công nghệ - polygot)

- Nó đối lập với kiến trúc `nguyên khối (monolithic)`, nơi toàn bộ ứng dụng được xây dựng như một đơn vị duy nhất lớn và chặt chẽ

## Ưu điểm của hệ thống microservice

- Kiến trúc microservice mang lại nhiều lợi ích đáng kể, đặc biệt cho các ứng dụng phức tạp và quy mô lớn

- `1. Khả năng mở rộng (scalability) và tính linh hoạt` 
    - `Mở rộng độc lập`: có thể `mở rộng` (scale) từng dịch vụ riêng biệt dựa trên nhu cầu thực tế. VD dịch vụ xử lý đơn hàng có thể cần mở rộng nhiều hơn dịch vụ quản lý hồ sơ người dùng

    - `Đa dạng công nghệ (technology Heeterogenity/Polygot)` các nhóm phát triển có thể chọn `ngôn ngữ lập trình, framework và CSDL tốt nhất`  cho từng dịch vụ cụ thể mà không bị ràng buộc với lựa chọn công nghệ cho toàn ứng dụng

- `2. Tính độc lập trong phát triển và triển khai`
    - `Phát triển nhanh chóng và độc lập`: các nhóm nhỏ có thể `làm việc độc lập` trên các dịch vụ khác nhau mà không cần phối hợp chặt chẽ với các nhóm khác

    - `TRiển khai liên tục (continuous deployment)` : mỗi dịch vụ có thể được `xây dựng, kiểm thử và triển khai (deploy) độc lập` và thường xuyên. Việc cập nhật một dịch vụ không làm ảnh hưởng đến sự hoạt động của dịch vụ khác, giảm thiểu rủi ro triển khai

- `3. Khả năng phục hồi (resilience) và CÔ lập lỗi`
    - `Cô lập lỗi (Fault Isolation)` : nếu một dịch vụ gặp sự cố, nó chỉ ảnh hưởng đến phần nghiệp vụ cụ thể đó, còn các dịch vụ khác vẫn tiếp tục hoạt động. Điều này giúp tăng cường `tính sẵn sàng` cho toàn bộ hệ thống
    - `Dễ dàng sửa lỗi`: Phạm vi code của mỗi dịch vụ nhỏ hơn giúp việc tìm và sửa lỗi nhanh chúng hơn

## Nhược điểm của hệ thống Microservice 
- `1. Độ phức tạp khi vận hành và quản lý`
    - `Phân tán (distribution)`: việc quản lý một hệ thống với `hàng chục, thậm chí hàng trăm dịch vụ` là phức tạp hơn nhiều so với một ứng dụng nguyên khối. Cần có các công cụ và kĩ thuật cho `khám phá dịch vụ (service discovery), cân bằng tảy (load balancing )` và `quản lý API Gateway`
    - `Giám sát và ghi nhật kí (monitoring & Logging)`: cần thiết lập một hệ thống giám sát và ghi nhật kí tập trung để theo dõi hiệu suất và tìm lỗi qua nhiều dịch vụ phân tán

    - `Kiểm thử phức tạp`: Kiểm thử end-to-end (kiểm thử đầu cuối) trở nên khó khăn vì phải đảm bảo sự tương tác chính xác giữa nhiều dịch vụ

- `2. Thách thức về giao tiếp và độ trễ`
    - `Gọi hàm từ xa (Remote call overhead)`: Việc giao tiếp giữa các dịch vụ (thường qua mạng) `chậm hơn` so với hàm cục bộ trong kiến trúc nguyên khối. Điều này có thể dẫn đến `độ trễ (latency)` cao hơn

    - `Tính nhất quán dữ liệu (data consistency)`: vì mỗi dịch vụ có CSDL dữ liệu riêng việc duy trì `tính toàn vẹn giao dịch` và `nhất quán dữ liệu` giữa các dịch vụ trở thành một vấn đề phức tạp hơn (thường giải quyết bằng các mô hình như SAGA)

- `3. Chi phí cơ sở hạ tầng  và tổ chứ`
    - `Chi phí cơ cở hạ tầng`: yêu cầu nhiều tài nguyên hơn so với việc chạy các `bản sao (instance)` độc lập lập của mỗi dịch vụ, cùng với chi phí quản lý các công nghệ container hóa (Như docker , kubernetes)

    - `Yêu cầu kĩ năng cao`: đòi hỏi đội ngữ phát triển và vận hành (devOps) có `kĩ năng chuyên sâu` về phân tán hệ thống , CICD containerization và quản lý đám mây
