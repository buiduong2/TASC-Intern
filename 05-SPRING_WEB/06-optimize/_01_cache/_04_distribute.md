## Khác biệt chính: từ Cache cục bộ đến Cache phân tán

- Trong một hệ thống đơn lẻ, Cache cục bộ (in-memory cache) là đủ. Nó lưu trữ dữ liệu trên cùng một máy chủ ứng dụng. Nhưng khi ta có nhiều máy chủ ứng dụng (VD, trong hệ thống dịch vụ hoặc trên nền tảng đám mấy). Cache cục bộ có một vấn đề lớn
    - `Không đồng bộ`: Mỗi máy chủ có một bản sao cache riêng. nếu dữ liệu trên máy chủ A thay đổi, bản sao trên máy chủ B vẫ là dữ liệu cũ , dẫn đến `tính nhất quán dữ liệu bị phá vỡ`

    - `Không hiệu quả`: nếu một yêu cầu đến máy chủ B, nó sẽ bị "cache miss" và truy cập CSDL, ngay cả khi dữ liệu đó đã có trên cache của máy chủ A 

- Để giải quyết vấn đề này, người ta sử dụng `distributed Cache (cache phân tán)`

## Distributed Cache là gì

- Distributed Cache là một hệ thống Cache độc lập, gồm nhiều máy chủ cache, hoạt động như một bộ nhớ đệm chung cho toàn bộ hệ thống ứng dụng, thay vì mỗi máy chủ ứng dụng có cache riêng chúng đều truy cập vào cùng một hệ thống cache phân tán

## Các thành phần chính

- 1. `Cache Node (Các nút cache)`: Các máy chủ độc lập (thường được gọi là các node) tạo nên hệ thống Cache phân tán. VD nổi tiếng là REdis và Memcached

- `2. Clients (ứng dụng khách)`: các máy chủ ứng dụng của chúng ta sẽ sử dụng client để kết nối và truy cập vào hệ thống cache này

- `Data Partitioning (phân vùng dữ liệu)`: dữ liệu được chia nhỏ và lưu trữ trên các node khác nhau. Thuật toán `Consistent Hashing (băm nhất quán)` thường được sử dụng để phân phối dữ liệu một cách đồng đều và giảm thiểu việc di chuyển dữ liệu khi thêm hoặc bớt node

### Cách thức hoạt động

- Khi một máy chủ ứng dụng cần dữ liệu: 
    - 1. Nó gửi yêu cầu tới hệ thống cache phân tán.
    - 2. Dựa vào key của dữ liệu, thuật toán băm nhất quán sẽ xác định node nào đang lưu dữ liệu đó
    - 3. Yêu cầu được chuyển đến node đó. Nếu tìm thấy dữ liệu (`cache hit`) nó sẽ trả về ngay lập tức
    - 4. Nếu không tìm thấy `cache miss`, máy chủ ứng dụng sẽ truy cập CSDL gốc, sau đó lưu bản sao của dữ liệu vào hệ thống cache phân tán để lần sau sử dụng

- `Lợi ích` 
    - `Tính nhất quán dữ liệu`: mọi máy chủ ứng dụng đều truy cập vào cùng một nguồn cache. Đảm bảo dữ liệu luôn đồng bộ. Khi dữ liệu được cập nhật, nó chỉ cần được cập nhật ở một nơi duy nhất trong hệ thống cache phân tán

    - `Khả năng mở rộng ngan (horizontal Scability)` : ta có thể dễ dàng tăng dùng luongj và hiệu suất của cache bằng cách tiêm thêm nhiều node cache mới, thay vì nang cập một máy chủ cache duy nhất

    - `Độ tin cậy cao`: nếu một node cache bji lỗi, dữ liệu trên các node khác vẫn còn, và hệ thống có thể chuyển hướng tới các cache còn lại

    - `Giảm tải cho CSDL`M ọi yêu cầu truy cập dữ liệu đều được xử lý bởi cache, giảm đáng kể gánh nặng cho CSDL

## Cache Invalidation trong môi trường phân tán

### 1. Time - to -live (TTL - Thời gian sống)

- Đây là phương pháp đơn và và phổ biến nhất. Mỗi mục dữ liệu được hteme vào cache sẽ có thời gian sống nhất định (5p)

    - `Cách hoạt động`: sau khi khoảng thời gian này kết thúc, dữ liệu sẽ tự động bị xóa khỏi cache, bất kể nó có được truy cập lại hay không
    - `Ưu điểm: ` đơn giản và hiệu quả. Nó tự động giải quyết vấn đề dữ liệu lỗi thời mà không cần các cơ chế phức tạp

    - `Nhược điểm: `
        - `Độ chế`: dữ liêu jcos thể bị lỗi trong khoảng thời gian TTL . VD nếu thời gian TTL là 5p, nhưng dữ liệu bị thay đổi sau 1P, hệ thống sẽ đọc dữ liệu cũ trong 4 phút còn lại

        - `Hiệu suát kém`: dữ liệu có thể bị xóa ngay cả khi nó vẫn còn rất "hot", dẫn đến cache miss không cần thiết và tăng tải cho cSDL

### 2. Write-through Cahce


- `Cách hoạt động`: Khi một máy chủ ứng dụng ghi dữ liệu, nó sẽ đồng thời ghi vào cơ sở dữ liệu gốc và hệ thống cache phân tán.

- `Ưu điểm`: Đảm bảo tính nhất quán ngay lập tức. Dữ liệu trong cache luôn đồng bộ với dữ liệu gốc.

- `Nhược điểm:`

    - `Độ trễ khi ghi`: Quá trình ghi sẽ chậm hơn vì phải chờ cả hai thao tác hoàn thành.

    - `Không giải quyết được mọi trường hợp`: Vẫn có thể xảy ra vấn đề nếu dữ liệu được thay đổi trực tiếp trên cơ sở dữ liệu mà không thông qua lớp ứng dụng.

### 3. Báo hiệu xóa bỏ (invlidation signaling)

- đây là chiến lược phức tạp và hiệu quả hơn, thay vì để dữ liệu hết hạn theo thời gian, hệ thống sẽ thông báo cho cache khi dữ liệu gốc thay đổi
    - `Cách hoạt động`
        - 1. Máy chủ A cập nhật dữ liệu trong CSDL
        - 2. Máy chủ A gửi một thông điệp (Signal) tới hệ thống cache, yêu cầu xóa mục dữ liệu đó khỏi Cache
        - Hệ thống Cache sẽ tìm và xóa mục đó
    
    - `Ưu điểm`
        - `Tính nhất quán gần như ngay lập tức` : ngay khi dữ liệu gốc thay đổi, dữ liệu trong cache sẽ được xóa, đmả bảo các yêu cầu tiếp theo sẽ lấy dữ liệu mới từ CSDL

        - Hệ thống cache sẽ tìm và xóa mục đó

    - `Nhược điểm`
        - `Phức tạp`: yêu cầu một hệ thống nhắn tin (message qeue) và kiến trúc phức tạp để xử lý các thông điệp này

        - `Khả năng lỗi`: nếu thông điệp bị lỗi hoặc xử lý chậm, dữ liệu vẫn có thể không nhất quán trong một khoảng thời gian

### 4. Kết luận

- Lựa chọn phụ thuộc vào yêu cầu của ứng dụng về `độ chính xác` và `hiệu suát`
    - `TTL` phù hợp với các ứng dụng có thể chấp nhận độ trễ nhỏ hoặc dữ liệu thay đổi không thường xuyên

    - `Inlivdation Signaling` là lựa chọn tốt nhất cho các ứng dụng cần `tính nhất quán cao` (GIao dịch tài chính, thông tin người dùng)