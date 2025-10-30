## Kafka

-   `Apache Kafka ` là một `nền tảng steaming sự kiện (event Streaming platform) phân tán` mã nguồn mở. nó được thiết kế để xử lý lượng lớn dữ liệu theo thời gian thực một cách hiệu suất cao, bền bỉ và có khả năng mở rộng

-   Kafka thường được mô tả là một hệ thống `message Queue (Hàng đợi thông điệp)` nhưng mạnh mẽ hơn nhiều, hoạt động dựa trên mô hình `publish-subscribe (Pub/sub)` và có khả năng lưu trữ dữ liệu (Persistent)

## 1. Các khái niệm cốt lõi của kafka

-   `A. Sự kiện (Event)`

    -   Sự kiện là đơn vị dữ liệu cốt lõi trong kafka. Một sự kiện đại diện cho một hành động hoặc sự thay đổi đã xảy ra (VD một giao dịch đã được thực hiện, một người dùng đã nhập vào một liên kết)

-   `B. Topic (Chủ đề)`

    -   `Topic` là một danh mục hoặc tên kênh mà cá sự kiện được lưu trữ và sắp xếp

-   `C. Partition (Phân vùng)`

    -   Mỗi `topic` được chia thành một hoặc nhiều `partition`

    -   `Partition` là đơn vị `bền bỉ (durabkle)` và `có thứ tự (ordered)` của cá sự kiện. Thứ tự chỉ được đảm bảo trong một partition

    -   Các sự kiện trnog Parittion được gán một `offset (chỉ số tuần tự)` duy nhất

    -   Việc chia Partition cho phép kafka `mở rộng` quy mô vì dữ liệu của một topci có thể được phân tán trên nhiều máy chủ (broker)

-   `D. Broker (máy chủ)`

    -   `Broker` là một máy chủ (server) chạy kafka
    -   Một kafka cluster (cụm kafka) gồm nhiều Broker làm việc cùng nhau
    -   Broker có nhiệm vụ lưu trữ các Partition của các Topic và phục vụ yêu cầu từ Producer và Consumer

-   `E. Producer (Người sản xuất) và Consumer (người tiêu thụ)`

    -   `Producer` : ứng dụng gửi sự kiện đến các Topic. Producer quyết định Parittion nào để ghi sự kiện vào

    -   `Consumer` : ứng dụng đọc các sự kiện từ các Topic. Các Consumer thuộc cùng một `Consumer Group` sẽ chia sẻ việc đọc Partition để xử lý song song

-
