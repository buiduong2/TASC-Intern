## Trang xem Sumamry của user

| STT | Tên Cột (Hiển thị)         | Service Cung cấp  | Lý do Cần thiết                                                         |
| :-: | :------------------------- | :---------------- | :---------------------------------------------------------------------- |
|  1  | `ID Khách hàng`            | `Auth Service`    | Mã định danh duy nhất (để tra cứu chi tiết).                            |
|  2  | `Tên đăng nhập (Username)` | `Auth Service`    | Xác định tài khoản.                                                     |
|  3  | `Tên đầy đủ`               | `Profile Service` | Xác minh danh tính người dùng thực (cho mục đích hỗ trợ).               |
|  4  | `Email`                    | `Auth Service`    | Thông tin liên hệ chính.                                                |
|  5  | `Số điện thoại`            | `Profile Service` | Thông tin liên hệ nhanh.                                                |
|  6  | `Trạng thái TK`            | `Auth Service`    | Quản lý truy cập (`Active`/`Blocked`). Rất quan trọng khi lọc/tìm kiếm. |
|  7  | `Tổng số đơn hàng`         | `Order Service`   | Đánh giá nhanh mức độ hoạt động (để sắp xếp theo khách hàng tiềm năng). |
|  8  | `Ngày đăng ký`             | `Auth Service`    | Dùng để phân tích thời gian và lọc theo khách hàng mới.                 |

##

Vậy đây có lẽ sẽ là cái mà admin-bff của tôi trả về dưới dạng Page cho trang dashboard có ui table

@Getter

@Setter

public class UserInfo {

    private long id;

    private String username;

    private String email;

    private String userType;

    private String phone;

    private String status;



    private long orderCount;



    private LocalDateTime createdAt;

}

vậy tôi sẽ tạm thời tạo 3 client.

public interface AuthService {

    PageResponseDTO<UserDTO> getUsers(Map<String, ?> params);

}

public interface OrderService {

    Map<Long, Long> getUserOrderCountByIds(List<Long> ids);

}

public interface ProfileService {
    Map<Long, ProfileDTO> getProfileByIds(List<Long> ids);
}

