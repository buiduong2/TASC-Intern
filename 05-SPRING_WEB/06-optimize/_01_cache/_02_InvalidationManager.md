## Bài toán dữ liệu lỗi thời (cache invalidation)

- Dữ liệu lỗi thời xảy ra khi dữ liệu gốc (trong CSDL, trên máy chủ, ...) bị thay đổi , nhưng bản sao trong cache lại không được cập nhật. Nếu người dung truy cập dữ liệu lỗi thời họ sẽ nhận được thông tin không chính xác

## Chiến lược giải quyết dữ liệu lỗi thời

### 1. Write-through Cache

- `Cách hoạt độnh`: khi có một yêu cầu ghi (cập nhật ) dữ liệu, hệ thống sẽ thực hiện đồng thời 2 thao tác
    - Cập nhật dữ liệu vào `nguồn gốc`
    - Cập nhật dữ liêu jvafo `Cache`

- `Ưu điểm`: dữ liêu jtrong cache luôn đồng bộ với nguồn gốc. Không rủi ro về dữ liệu lỗi thời

- `Nhược điểm`
    - `Tăng độ trễ`: mỗi lần ghi hệ thống phải chờ cả hai thao tác hoàn thành, làm chậm quá trình ghi
    - `TỐn kém`: mỗi lần ghi ta phải chờ cả 2 nơi

### 2. Write-back cache

- `Cách hoạt động`: khi có một yêu cầu ghi, hệ thống chỉ ghi dữ liệu vào `cache`s. Dữ liệu trong cache được đánh dấu là đã thay đổi (dirty). Sau đó dữ liệu này sẽ được ghi vào `nguồn gốc` sau một khoảng thời gian nhất định hoặc khi có điều kiện cụ thể (VD: khi bộ nhớ cache đầy)

- `Nhược điểm`
    - `Rủi ro mất dữ liệu`: nếu hệ thống bị sập trước khi dữ liệu trong cache được ghi vào nguồn gốc, dữ liệu đó sẽ mất

    - `Phức tạp hơn`: yêu cầu quản lý phức tạp để theo dõi những bản sao đã thay đổi và đồng bộ chúng

### 3. Write-around Cache

- `Cách hoạt động`: Khi có một yêu cầu ghi, hệ thống `chỉ ghi vào nguồn gốc` và `bỏ qua cache`. Dữ liệu chỉ được đưa vào cache khi có một yêu cầu đọc dữ liệu đó sau này

    - `Ưu điểm`
        - `Phù hợp với dữ liệu ít khi được đọc lại`: tránh việc đưa dữ liệu vào cache mà không bao giờ được sử dụng, tiết kiệm không gian cache

    - `Nhược điểm: `
        - `Tăng độ trễ lần đầu đọc`: sau khi ghi, lần đọc đầu tiên sẽ là "cache miss" (không có trong cache), buộc phải lấy dữ l;iệu từ nguồn gốc, làm tăng độ trễ

### Tóm lại

- Việc lựa chọn quản lý cache phụ thuộc vào yêu cầu của ứng dụng

- Nếu `tính nhất nhất quán và độ tin cậy` là quan trọng nhaastm hãy chọn `Write-through`

- Nếu `Hiệu suát và tốc độ ghi` là ưu tiên hàng đầu, hãy chọn `Write-back`

- nếu chỉ muốn cache các thao tác dữ liệu thường xuyên được đọc, hãy cân nhất `Write-around`
