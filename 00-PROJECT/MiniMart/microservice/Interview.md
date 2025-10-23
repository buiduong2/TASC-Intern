## Điều cần làm

- 22/10/2025: Chuản bị đi interview

- Tiến hành việc consume các message sao cho vẫn ok khi đọc các message trùng lặp
    - Bao gồm kiểm tra các paylaod trong message phải hợp lệ với trạng thái hiện tại tránh ghi đè
    - Kiểm tra sự tồn tại thì dừng lại

- Tiến hành cho Order lắng nghe các logic để chuẩn bị cho tạo ra sự kiện bù trừ

- Tập hợp các trạng thái chuẩn bị bù trừ khi xảy ra faild trong quá trình tạo Order

- Viết Logic để cancel Order hợp lý

- Viết logic để có thể thanh toán thành công

### Idependome

- OrderService.processProductValidationPassedEvent()
    - Chỉ là cập nhật UnitPrice. ta chỉ cứ update vậy

- tạm bỏ qua

### Tiến hành cho Order lắng nghe các logic để chuẩn bị cho tạo ra sự kiện bù trừ

### Luồng bù trừ khi tạo thất bại 

- ProductService
    - phớt lờ
- PaymentService:
    - Kiểm tra chắc chắn paymentTranssaction chưa được tọa-
    - Đánh dấu PAymetn failed luôn
- Inventory: 
    - đảo ngược lại reservered STock (nếu có)

### Luồng bù trừ khi cancel (sau khi confirm)