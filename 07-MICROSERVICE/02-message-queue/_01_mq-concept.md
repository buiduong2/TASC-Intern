## Message queue là gì ?

-   `Message Queue` (hàng đợi thông điệp): là một thành phần hoặc mô hình kiens trúc cho phép các ứng dụng hoặc thành phần của hệ thống giao tiép với nhau một cách `bất đồng bộ (asynchronously)`

-   nó hoạt động như một `bộ đệm (buffer)` trung gian , lưu trữ dữ liệu (dưới dạng thông điệp/ message) được gửi từ cùng một ứng dụng (gọi là `Producer` hoặc `Publisher`) cho đến khi ứng dụng đích (gọi là `Consumer` hoặc `Subscriber`) sẵn sàng xử lý

## 1. Các thành phần cơ bản:

-   Một hệ thống Message Queue tiêu chuẩn thường bao gồm 3 thành phần chính:
-   `1. hàng đợi Queue`: là nơi lưu trữ tạm thời các thông điệp. Nó đảm bảo các thông điều được xử lý theo thứ tự (thương là FIFO - First in, First Out)

-   `2. Producer (người sản xuất)`: ứng dụng tạo và gửi thông điệp vào hàng đợi
-   `3. Consumer (Người tiêu thụ)`: ứng dụng kết nối với hàng đợi để nhận và xử lý các thông điệp

## 2. Cách thức hoạt động

-   Khi một ứng dụng cần thông báo hoặc truyền dữ liệu cho một ứng dụng khác nó sẽ:

    -   `1. Gửi: Producer` đóng gói dữ liệu vào một `thông điệp (message)` và gửi nó đến hàng đợi
    -   `2. Lưu trữ` Hàng đợi lưu thông điệp một cách an toàn
    -   `3. Nhận: Consumer` chủ động (pull) hoặc bị động (push) nhận thông điêp từ hàng đợi khi nó rảnh rỗi

    -   `4. Xử lý: ` Consumer xử lý thông diệp, sau khi xử lý thành công, nó gửi xác nhận (acknowledgement) và thông điệp sẽ bị xóa khỏi hàng đợi

## 3. Chức năng và lợi ích

-   Message Queue đặc biệt quan trọng trong kiến trức Microserrivce và hệ thống phân tán

-   `A. Giao tiếp bất đồng bộ (Asynchrounous Communication)`

-   Đây là chức năng quan trọng nhất. Producer không cần đợi Consummer xử lý xong thông điệp. Sau khi gửi, Producer có thể tiếp tục công việc khác

    -   `Giảm độ trễ`: CLient không phải chờ đợi các hoạt động tốn thời gian
    -   VD: Khi người dùng đặt hàng, dịch vụ đặt hàng chỉ cần gửi thông điệp `ORDER_CREATED` vào Queue rồi phản hồi lại người dùng, thay vì phải đợi dịch vụ thanh toán, dịch vụ gửi email và dịch vụ trừ kho hoàn thành

-   `B. Khử ghép nối (decoupling)`

-   Procducer và Consumer không cần biets vị trí hay trạng thái hoạt động của nhau (hoặc thậm chí là công nghệ của nhau). Chúng chỉ cần thông nhất về định dạng thông điệp

    -   `Tăng tính linh hoạt`: dễ dàng thay thế, nâng cấp hoặc thêm mới các service mà không làm ảnh hưởng đến các service khác

-   `C. Chịu tải và đệm (Load leveling & buffering)`

-   Message Queue hoạt động nhự một bộ đệm giữa các hệ thống có tốc độ xử lý khác nhau hoặc khi có lưu lượng truy cập đột biến

    -   `Ngăn chặn quá tải`: Khi có quá nhiều yêu cầu, queue sẽ giữ lại các yêu đó cho phép consumer xử lý chúng theo tốc độ ổn định, tránh làm sập COnsumer

-   `D. Đảm bảo giao hàng (Guaranteed Delivery)`
-   Hệ thống Queue thường lưu trữ thông điệp một cách bền bỉ (persistent) cho đến khi chúng được xử lý thành công
    -   `Chống mất dữ liệu`: Nếu consumer bị lỗi trong quá trình xử lý, thông điệp sẽ không bị xóa và có thể được xử lý lại sau khi COnsumer phục hồi

## 4. Các mô hình Message Queue phổ biến

-   `Các mô hình Message Queue phổ biến`

    -   `Point-to-point (hàng đợi đơn)`: Một thông điệp chỉ được gửi đến `một consumer duy nhất`. Thường sử dụng cho các tác vụ task cần xử lý một lần

    -   `Publish subscribe (Pub/sub)`: một thông điệp được gửi đến một `Topic (chủ đề)` và có thể được nhận bởi `nhiều consumer` cùng lúc. Thường dùng cho việc lan truyền dữ liệu (VD USER_ACCOUNT_CREATED)

# Ưu điểm khi sử dụng Message Queue (MQ)

-   `Giao tiếp bất đồng bộ (Asynchrounously)`

    -   `Giảm độ trễ` Ứng dụng gửi (producer) không cần đợi ứng dụng nhận (consumer) xử lý xong. Nó gửi thông điệp và tiếp tục công việc của minhf. Điều này cải thiện trải nghiệm người dùng vì phản hồi trở lên nhanh hơn

-   `Khử ghép nối (decoupling)`

    -   Các dịch vụ (microservice) hoàn toàn độc lập với nhau. Producer không cần biết COnsumer là ai, nó ở đâu hay được viết bằng ngôn ngữ gì? Điều này giúp `dễ dàng thay thế, nâng cấp` hoặc `thêm mới` dịch vụ mà ko ảnh hưởng đến dịch vụ khác

-   `Cân bằng tải và chịu tải (Load leveling)`:

    -   MQ đóng vai trò là `bộ đệm`. Nó lưu trữ các yêu cầu trong thời gian lưu lượng truy cập cao điểm. Ngăn chặn việc quá tải dịch vụ consumer, Consumer có thể xử lý các yêu cầu với tốc độ ổn dịnh

-   `Độ bền và độ tin cậy (durability)`
    - MQ có lhar năng lưu thông điệp một cách `bền bỉ` (persistent Storage) cho đến khi thông điệp được xác nhận và xử lý. Nếu Consumer bị lỗi, thông điệp sẽ không bị lỗi và sẽ được xử lý sau khi hệ thống phục hôi

- `Khả nawgn mở rộng (Scalability)`: dễ dàng `thêm nhiều Consumer` (Máy chủ xử lý) để tăng tốc độ xử lý thông điệp khi cần thiết, không ko cần thay đổi Producer 

### Nhược điểm khi sử dụng Message Queue (MQ)

- `Tăng độ phức tạp`
    - Thêm một hệ thống mới (message Broker như kafka, rabbitMQ) để quản lý , đồi hỏi thêm công việc về `triển khai, cấu hình và giám sát`
- `Đảm bảo thứ tự (Ordering)`: hầu hết các hệ thống MQ `không đảm bảo thứ tự` xử lý nghiêm ngặt (trừ khi được cấu hình đặc biệt, như sử dụng `partition key` trong kafka). Việc này có thể gây ra lỗi nghiệp vụ nếu thứ tự là quan trọng

- `Quản lý lỗi và debugging`: 
    - Việc gỡ lỗi trở lên khó khăn hơn. Thay vì đường dẫn yêu cầu trực tiếp ta phải theo dõi yêu cầu qua nhiều dịch vụ và qua 
    
    - hàng đợi (trancing) . Cần có các công cụ `giám sát và truy vết phân tán (distributed Traccing)`

- `Độ chễ (latency)`
    - Mặc dù giảm độ trễ cho người dùng, MQ `thêm độ trễ` tổng thể và quá trình xử lý đầu cuối (END-to-END). thông điệp cần thời gian để được gửi và lưu trữ sau đó là được nhận

- `Vấn để một lần và duy nhất (Exactly Once)`: rất khó để đảm bảo một thông điệp được `xử lý chính xác một lần` Hầu hết các MQ chỉ đảm bảo `ít nhất một lần (at leat one )`, yêu cầu consumer phải xử lý `viejec tính toán lại (idempotency)` để tránh dtrungf lặp dữ liệu

### Khi nào cần dùng tới Message Queue

- `1. Xử lý tác vụ nền (background processing)`
- Sử dụng MQ cho các tác vụ tốn thời gian mà  người không cần đợi kết quả ngay lập tức'
    - VD: Gửi Email xác nhận,tạo báo cáo, nén chuyển đổi tệp tin video/hình ảnh

- `2. Giao tiếp giữa các microservice (Microservice Communication)`

- MQ là nên tảng cho kiến trúc `Saga Choẻography` và các hoạt động `bất đồng bộ` xuyên dịch vụ
    - VD : Khi dịch vụ Đơn hàng tạo đơn hàng, nó gửi sự kiện `ORDER_CREATED` để kích hoạt dịch vụ Thanh toán và dịch vụ gửi `Email` cùng lúc

- `3. Tăng cường độ bền và khả năng chịu lỗi (resilience)`
- Đảm bảo rằng các hoạt động quan trọng sẽ khoogn bị mất trong trường hợp một dịch vụ bị lỗi
    - VD: Ghi lại nhật kì giao dịch tài chính. Nếu dịch vụ ghi nhất kí bị gặp sự cố, thông điệp vẫn nằm trong hàng đợi xử lý

- `4. Xử lý lưu lượng đỉnh`
    - Khi hệ thống thường xuyên có sự thay đổi lớn về lưu lượng truy cập (VD các chiến dịch khyens mại, sự kiện lớn)

        - VD trong một giờ cao điểm, MQ sẽ giữ 100.000 yêu cầu mua sắm và chuyển chúng từ từ đến hệ thống xử lý (VD 1000 yêu cầu / giây) đẻ tránh làm sập CSDL


-_Tóm lại: Cần sử dụng khi hệ thống phân tán lớn, yêu cầu khả năng mở rộng, độ tin cậy cao và giao tiếp bất đồng bộ_