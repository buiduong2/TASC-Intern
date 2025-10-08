
# - Quản lý transaction trong hệ thống microservice (Saga pattern)

- Việc quản lý `transaction (Giao dịch)` trong hệ thống microservice là một thách thức lớn vì mỗi dịch vụ đều có CSDL riêng, loại bỏ khả năng sử dụng giao dịch phân tán truyền thống  VD 2PC

- `Saga Pattern` là một giải pháp phổ biến để quản lý các giao dịch xuyên dịch vụ, đảm bảo tính nhất quán dữ liệu trong môi trường phân tán
## 2PC 

### Nhược điểm của 2PC

- `1. Phá vỡ tính độc lập và phân tách dữ liệu (Autonomy & decouling)`
    - `Tính độc lập`: mỗi microservice được thiết kế để có thể được `triển khai mở rộng và phát triển độc lập`. Việc sử dụng 2PC buộc các dịch vụ phải `Phụ thuộc chặt chễ` vào nhau trong quá trình giao dịch

    - `Khóa tài nguyên (locking)`: trong 2 PC, các tài nguyên (hàng trong CSDL) ở `tất cả` các dịch vụ tham gia phải bị `khóa` cho đến khi tất cả các bên đồng ý commit. Điều này làm `tăng độ trẽ` và `giảm tính sẵn sàng` của hệ thống. Nếu một dịch vụ bị lỗi hoặc chậm trễ, nó sẽ giữ khóa trên các dịch vụ khác, gây ra tình trạng `chặn (blocking)` toàn bộ giao dịch, dẫn đến hiện tượng `chết cứng (deeadLock)`

    - `Phân tách dữ liệu`: Mirservice khuyến kích mỗi dịch vụ sỡ hữu CSDL riêng (phân tách dữ liệu). 2PC buộc các CSDL này phải phối hợp ở múc độ thấp, làm `mất đi tính độc lập` về công nghệ và hoạt động

- `2. Vấn đề về Độ trễ và Hiệu suất (latency & performance)`
    - 2PC yêu cầu `hai vòng giao tiếp mạng (two rounds of network commmunication)` giữa `Coordinator (bộ điều phối)` và `tất cả các participant (bên tham gia)`
    - `1. Phase1 : Parepare`: gửi yêu cầu chuẩn bị
    - `2. Phase 2 (commit /rollback)`: Gửi yueeu cầu thực hiện hoặc hoàn tác

    - Trong hệ thống microservice, số lượng dịch vụ tham gia có thể lớn. ĐỘ trễ của giao tiếp mạng tĩnh lũy qua 2 vòng này làm cho 2PC `cực kì chậm` và `không phù hợp` cho các ứng dụng có hiệu suất cao hoặc yêu cầu thời gian phản hồi thấp

- `3. Vấn đề về tính sẵn sàng và khả năng chịu lỗi (avaiablitity & Fault Tolerance)`
    - `Bộ điều phối là điểm lỗi đơn lẻ (SIngle POint of Faulure)`: nếu Coordinator bị lỗi sau khi một số Participant đã chuẩn bị (phase 1) nhưng trước khi gửi lệnh Commit (phase 2), Các tài nguyên của Participant sẽ bị `khóa vĩnh viễn (indeffinitely locked)` cho ddense khi Coordinator phục hồi hoặc can thiệp thủ công. Điều này làm `giả mnghieepj trọng tính sẵn sàng` của hệ thống 

### Tóm lại

- Microservice ưu tiên `tính độc lập, khả năng mở rộng (scalability) và tính sẵn sàng cao`, 2PC là một `rào cản lớn`. Đó là lý do tại sao `Saga Pattern` được ưu tiên sử dụng, vì nó chấp nhật sự `nhất quán cuối cùng (eventual consistency)` để đổi lấy `tính sẵn sàng` và `tính độc lập cao hơn`

## Saga  Pattern : Quản lý giao dịch phân tán

- `Saga` là một chuỗi các `giao dịch cục bộ (local Transasctions)`. Mỗi giao dịch cục bộ được thực hiện bởi một microservice duy nhất và có thể cập nhật CSDL của dịch vụ đó
    - Mỗi giao dịch cục bộ sẽ phát sinh một `sự kiện (event)` để kích hoạt bước tiếp theo trong saga
    - nếu một giao dịch cục bộ `thất bại` Saga sẽ thực hiện hàng loạt các `giao dịch bù trừ (compenstating transactions)` để hoàn tác (undo) những thay đổi đã được thực hiện bởi các giao dịch cục bộ thành công trước đó, đảm bảo tính nhất quán (trở lại trạng thái ban đầu)

- Nói chung nó gồm 3 thành phần
    - `Giao dịch cục bộ (local transactoin)`
    - `Giao dịch bù trừ (compensating Transaction)`
    - `Sự kiện (Event)`

### Các loại giao dịch trong Saga

- Một Saga có thể gồm 3 loại dịch vụ cục bộ
    - `1. Giao dịch bù trừ (compensable Transaction)`: là giao dịch có thể bị đảo ngược bằng một giao dịch khác có tác dụng ngược lại (VD hoàn toàn để bù trừ cho giao dịch trừ tiền)

    - `2. Giao dịch Pivot (Pivot Transactions)`: kaf điểm "không quay lại" của Saga. Nếu giao dịch này thành công, Saga sẽ cam kết chạy cho đến khi hoàn toàn. nếu nó thất bại  Saga phải thực hiện các giao dịch bù trừ
    - `3. Giao dịch có thể thử lại (Retryable Transaction)`: là giao dịch theo sau giao dịch Pivot và được đảm bảo sẽ thành công

### Hai phương pháp triển khai Saga

- Có hai cách chính để điều phối các giao dịch cục bộ trong Saga

- `A. Choreography (điều phối bằng sự kiện)`
    - `Cơ chế`: Phi tập trung, các service giao tiếp với nhau bằng cách `phát và lắng nghe các sự kiện` thông qua một kênh trung gian (message broker như kafka, RabbitMQ)
    - `VD`: 
        - 1. Dịch vụ đặt hàng (order service) tạo đơn hàng, phát ra sự kiện `ORDER_CREATED`
        - 2. Dịch vụ thanh toán Paymetn Service nghe sự kiện `ORDER_CREATED`, xử lý thanh toán và phát ra sự kiện `PAYMENT_PROCESSED`
        - 3. Dịch vụ đặt hàng nghe sự kiện `PAYMENT_PROCESSED` và cập nhật lại trạng thái đơn hàng

    - `Uuw điểm:` độ kết nối lỏng lẻo (decouplng) cao , không có điểm lỗi đơn lẻ (Single Point of Failure) vì không có bộ điều phối trung tâm
    - `Nhược điểm`: Khó theo dõi luồng nghiệp vụ tổng thể và gỡ lỗi khi số lượng service tăng lên

- `B. Orchestration (điều phối tập trung)`
    - `Cơ chế`: tập trung sử dụng một `orchestration (bộ điều phối / Saga Coordinator)` để quản lý toàn bộ luồng giao dịch

    - VD: 
        - 1. Client gửi yêu cầu đến `orchestrator`
        - 2. Orchestrator gửi `lệnh (command)`trực tiếp đến dịch vụ dặt hàng
        - 3. Dịch vu jđặt hàng thực hiện giao dịch và gửi `phản hồi (reply)` về cho orchestrator
        - 4. Orchestrator nhận phản hồi và quyết định gửi lệnh tiếp theo (VD lệnh trừ kho cho dịch vụ kho hàng) hoặc kích hoạt giao dịch bù trừ nếu có lỗi

    - `Ưu điểm`: Dễ dàng kiểm soát, theo dõi (visibility) luồng giao dịch và xửi lý lỗi/giao dịch bù trừ vì logic được tập trung ở một nơi
    - `Nhược điểm`: tăng tính phức tạp (cần duy trì một dịch vụ Orchestratoir riêng) và có thể trở thành điểm nghẽn hoặc điễm lỗi đơn lẻ nếu không được thiết kế dự phòng (high avaiable)

- 