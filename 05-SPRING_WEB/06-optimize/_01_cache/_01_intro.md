## Cache là gì ?

- là một bộ bộ nhớ đệm tạm thời, được sử dụng để lưu trữ dữ liệu hoặc thông tin thường xuyên được truy cập. Mục đích chính của nó là tăng tốc độ truy cập dữ liệu bằng cách lưu trữ bản sao của dữ liệu đó ở một vị trí gần với người dùng hoặc hệ thống xử lý

## Nguyên lý hoạt động cơ bản

- Nguyên lý hoạt động của Cache dựa trên giả định rằng các dữ liệu đã được truy cập gần đây hoặc được truy cập thường xuyên sẽ có khả năng được truy cập trong tương lai gần . Nguyên lý này được goij là `nguyên lý địa phương (principle locality)`
- Khi một ứng dụng hoặc hệ thống cần truy cập một dữ liệu
    - 1. Hệ thống sẽ kiểm tra dữ liệu đó có trong `cache` không
    - Nếu có (gọi là `Cache hit`) , hệ thống sẽ lấy dữ liệu từ Cache và sử dụng ngay lập tức. Đây là trường hợp lý tưởng

    - 3. Nếu không có ( gọi là `cache miss`) hệ thống sẽ phải đi dến nguồn dữ liệu gốc. (VD: ổ cứng, CSDL hoặc một máy chủ từ xa) để lấy dữ liệu. sau khi lấy được, dữ liệu này sẽ được lưu một bản sao vào cache để lần sau truy cập sẽ nhanh hơn

### Các loại cache phổ biến

- Cache cơ mặt ở khắp nơi trong hệ thống máy tính. Từ phần cứng đến phần mềm, Đưới đây là một số ví dụ phổ biến
    - `Cache của CPU`: là một nhớ đệm cực nhanh, 
    - `Cache của trình duyệt (Broswer Cache)`: khi ta truy cập vào một tragn web, trình duyệt sẽ lưu trữ các tệp tĩnh như hình ảnh, css, javascript vào máy tính của chúng ta. Làn sau truy cập vào trang web đó, trình duyệt không cần phải tải lại các tệp này, giúp trang web tải nhanh hơn

    - `Cache của CSDL`: các hệ thống CSDL thường sử dụng cache để lưu trữ kết quả của các truy vấn thường xuyên, trành việc truy vấn lại dữ liệu từ ổ đĩa giúp giảm tải và tăng tốc độ phản hồi

### Lợi ích và thách thức

- **Lợi ích**   
    - `Tăng tốc độ truy cập`: Đây là lợi ích quan trọng nhất. Cách giảm dộ trễ (latency) và tăng thông lượng (throughput) của hệ thống
    - `Giảm tải cho nguồn tài nguyên gốc`: Nguồn dữ liệu gốc (như CSDL , máy chủ) không phải xử lý quá nhiều yêu cầu lặp lại, giúp chúng hoạt động hiệu quả hơn
    - `Tiết kiệm băng thông mạng`: trong trường hợp cache web, việc không phải tải lại các tệp giúp tiết kiệm băng thông cho cả người dùng và máy chủ

- **Thách thức**
    - `Dữ liệu không đồng bộ (Cache invalidation)`: đây là thách thức lớn nhất. Khi dữ liệu gốc thay đổi, dữ liêu jtrong cache sẽ trở lên lỗi thời (stale). Việc đảm bảo cache luôn chứa dữ liệu mới nhất là một bài toán phức tạp

    - `Kích thước của Cache`: cache không thể lưu trữ tất cả mọi thứ. Nó có kích thước giới hạn. Vì vậy cần có các thuật toán quyết định dữ liệu nên được giữ lại và dữ liệu nào nên bị xóa để nhường chỗ cho dữ liệu mới  (VD thuật toán LRU - Least recenlty Used)

- 
