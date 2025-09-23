## Bài toán dung lượng Cache

- Cache có dung lượng hữu hạn. Khi cache đầy và một dữ liệu mới cần được thêm vào. Hệ thống phải quyết định dữ liệu nào trong cache nên bị xóa để nhường chỗ. 

- Mục tiêu là giữ lại những dữ liệu có khả năng được sử dụng cao nhất trong tương lai, từ đố tối đa hóa tỷ lệ `cache hit`


### 1. LRU( least recently Used) - Ít được sử dụng gần đây nhất

- `Nguyên lý hoạt động`
    - Thuật toán LRU giả định rằng dữ liệu gần đây nhất sẽ ó khả năng được sử dụng cao hơn trong tương lai. Do đó, khi cần xóa dữ liệu LRU sẽ loại bỏ mục đã không được truy cập trong một khoảng thời gian dàn nhất

- `Ưu điểm`
    - `Hiệu quả cao`: đây là một trong những thuật toán hiệu quả nhất. Đặc biệt với các luồng dữ liệu có tính lặp lại

- `Nhược điểm`
    - `Chi phí cao`: để theo dõi thời gian truy cập của từng mục, thuật toán này yêu cầu cấu trúc dữ liệu bổ xung (danh sách liên kết kép), làm tăng chi phí tính toán và bộ nhớ

### 2. FIFO (First-In, First-Out) vào trước ra trước

- `Nguyên lý hoạt động`
    - FIFO hoạt động giống như một hàng đợi. Dữ liệu được đưa vào cache đầu tiên sẽ là dữ liệu bị xóa đầu tiên khi cần. nó không quan tâm đến tần xuất hoặc thời điểm dữ liệu được sử dụng 

- `Ưu điểm`
    - `Đơn giản`: dễ triển khai và yêu cầu ít chi phí tính toán

- `Nhược điểm`
    - `Kém hiệu quả`: nó có thể loại bỏ dữ liệu quan trọng và thường xuyên được sử dụng chỉ vì dữ liệu đó đưa vào cache sớm nhất. VD dữ liệu A vẫn được truy cập liên tục, FIFO sẽ bỏ nó vào cache miss ngay sau đó

### 3. Các loại khác

- `LFU (Least frequently Used)`: loại bỏ dữ liệu ít được truy cập nhất (tần xuất)

- `Random Replacecment`: loại bỏ dữ liệu ngẫu nhiên

- `ARD (Adaptive Replacement Cache)`: một thuật toán phức tạp hơn, kết hợp các ưu điểm của LRU và LFU để ra quyết định tốt hơn