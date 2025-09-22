## - Thế nào là authentication, authorization

- `Authentication`(xác thực). `Authorization` (phân quyền) là 2 khái niệm cơ bản trong an ning mạng. thường đi đối với nhau nhưng có chức năng hoàn toàn khác biệt 

- `Authenticaiton (xác thực)`
    - `Authentication` là quá trình xác minh danh tính của người dùng. NÓ trả lời cho câu hỏi "bạn là ai" quá trình này đảm bảo rằng người dùng đăng nhập là người họ tuyên bộ
        - CÁc phương thức xác thực phổ biến bao gồm : 
            - `Password`: người dùng cung cấp mật khẩu
            - `Mã PIN`: mã số cá nhân
            - `Sinh trắc học (biometrics)`: sử dụng các đặc điểm sinh học như dấu vân tay, nhận dạng khuôn mặt,  hoặc quyét mống mắt 
            - `Mã OTP (One-time passowrd)` mã được gửi qua SMS hoặc email, chỉ có giá trị sử dụng một lần 
            - `Xấc thực đa yếu tố (MFA)`: yêu cầu người dùng cung cấp nhiều hơn một bằng chứng xác thực để tăng cường bảo mật

        - VD khi đăng nhập vào FAcebook. Việc nhập email và mật khảu là quá trình `xác thực` hệ thống sẽ kiểm tra xem thông tin chúng ta cung cấp có khớp với dữ liệu hay không 

## AUthorization - Phân quyèn

- `Authorization` là quá trình cấp quyền truy cập cho người dùng đã xác thực. Nó trả lời cho câu hỏi "bạn được phép làm gì". sau khi hệ thống biết ta là ai. nó sẽ quyết định chúng ta có quyền truy cập vao tài nguyên  hoặc thực hiện hành đông jnafo đó hay không

- Các quyền truy cập có thể được phân cấp dựa trên vai trò người `dùng (Role-Based ACCESS control - RBAC)` hoặc các quy tắc cụ thể 

- VD
    - Một quản trị viên (admin) có thể có quyền xóa bài viết của bất kì ai, trong khi người dùng bình thường chỉ có thể xóa bài viết của chính mình 
    - MỘt khách hàng có quyền xem thông tin sản phẩm, nhưng không có quyền chỉnh sửa giá 
    - Bạn đã đăng nhập thành công (Authentication) vào tài khoản facebook, nhưng ta ko thể xem tin nhắn riêng tư của người khác cvì không được `phân quyền` (AUthorization) để làm điều đó 

- `Phân quyền theo thuộc tính (Attribute-Based Access Control - ABAC)`
    - Mô hình này linh hoạt hơn. Quyền tủy cập được cấp trên sự kết hợp của nhiều `thuộc tính` như thuộc tính của người dùng (chức danh phòng ban), thuộc ví trị tài nguyên (độ nhạy cảm của tài liệu, ngày tạo), và thuộc tính môi trường (thời gian trong ngày)

- `Phân quyền tùy ý (Discretionary Access Control -DAC)` trong mô hình này. `chủ sở hữu của tài nguyên` sẽ quyết định ai có quyền truy cvapaj vào nó. VD khi ta tạo ram ột tài liệu trên Google DÓc. ta cso thể tự quyết định ai được phép xem, chỉnh sửa hoặc chia sẻ nó