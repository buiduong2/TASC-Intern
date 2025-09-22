## Saga Pattern (mô hình saga)

- `Saga Pattern` là một cách tiếp cận khác có thể quản lý giao dịch phân tán, được sử dụng phổ biến trong kiến trúc `microservices`. Khác với 2PC cố gắng đảm bảo `tính nguyên tử (atomicity)` trên toàn hệ thống. Saga chấp nhận một giao dịch có thể thất bại một phần và sử dụng một chuỗi  các `giao dịch cục bộ (local transactions)` để khôi phục hoặc bù đắp lại

- Một `Saga` là một chuỗi các `giao dịch cục bộ`, mỗi giao dịc được thực hiện bởi một service khác nhau, Nếu một giao dịch cục bộ thất bại, saga sẽ thực hiện một loạt các `gia odichj bù đắp (compenstaing trânsctions)` để hoàn tác các thay đổi đã được thực hiện bởi các giao dịch cục bộ trước đó

## các loại Saga 

- Có 2 cách triển krhai Saga chính

- **1. Choreography (Điệu nhẩy)**

    - Các serivce tham gia giao dịch sẽ tự động lắng nghe và phản ứng với cá sự kiện khác nhau.

    - Khi một serivce hoàn thành giao dịch cục bộ của mình, nó sẽ phát ra một sự kiện
    - các service khác sẽ lắng nghe sự kiện này và bắt đầu giao dịch cục bộ của riêng chúng
    
    - `Ưu điểm: ` đơn giản, không có điểm lỗi duy nhất (single point of failure)
    - `Nhược điểm: ` Khó theo dõi và gỡ lỗi, đặc biệt với các giao dịch phức tạp

- **Orchestrations (dàn nhạc)**
    - Có một `điều phối viên` (orchestratior) trung tâm

    - `Điều phối viên` chịu trách nhiêm gửi lệnh tới từng service để thực hiện giao dịch cục bộ
    - `Điều phó iviene`: cũng trụy trách nhiệm thực hiện các giao dịch bù đắp nếu có lỗi xảy ra 

    - `Ưu điểm`: Dễ kiểm soát, gỡ lỗi và theo dõi trạng thái giao dịch
    - `Nhược điểm`: điều phối viên có thể trở thành một điểm lỗi duyh nhất nếu không được thế kế dự phòng (redundancy tốt)

## VD về Saga Pattern

- Hãy lấy ví dụ về việc đặt một đơn hàng online 

    - `1. tạo đơn hàng`: Serivce đặt hàng (order Serivce) tạo một đơn hagnf với trạng thái "chờ hoàn thành" và gửi một sự kiện

    - `2. TRừ tiền`: service thanh toán (paymetn Service) lawngs nghe sự kiện, trừ tiền từ tài khoản khác hàng, sau đó gửi một sự kiện thành công hoặc thất bại

    - `3. Cập nhật kho`:Service kho hàng (inventory Service) lắng nghe sự kiện thành công từ Service thanh toán và trừ đi số lượng sản phẩm

    - `nếu tất cả thành công`: giao dịch hoàn tất
    - `nếu tất cả có lỗi (VD kho hàng không đủ)`
        - Service kho hàng gửi một sự kiện lỗi
        - Service thanh toán lắng nghe sự kiện lỗi và thực hiện giao dịch bù đắp: hoàn tiền cho khách hàng
        - Serivce đặt hàng lắng nghe sự kiện lỗi và cập nhật trạng thái đơn hàng "đã hủy"


## So sánh saga và 2PC

- `2PC`
    - `ưu điểm`: đảm bảo `Atomicity` tuyệt đối:
    - `Nhược điểm`: tính khả dụng thấp, hiệu suất kém, không phù hợp với kiến trúc microservices

-`Saga`
    - `Ưu điểm`: linh hoạt, hiệu suất cao, phù hợp với kiến trúc phân tán
    - `Nhược điểm` : không đảm bảo Atomicity tuyệt đối, có thể xảy ra trạng thái không nhất quán tạm thời