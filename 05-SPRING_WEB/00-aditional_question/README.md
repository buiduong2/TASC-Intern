## @Controller và @RestController

-   **@Controller xử lý các yêu cầu truyền thống**

-   @Controller là một annotation dùng để đánh dấu các lớp xử lý các request HTTP và trả về một `View` (một trang web HTML, JSP, THymeleaf) mục tiêu của chính của nó là tạo ra các ứng dụng web theo kiểu Model-VIew-Controller (MVC) truyền thống

-   Khi chúng ta dùng `@Controller` các phương thức sẽ trả về tên của một trang HTML hoặc JPA. Spring sẽ tìm kiếm template tương ứng và render nó để gửi về trình duyệt
-   Để trả về dữ liệu dưới dạng JSON hoặc XML thay vì một VIew chúng ta phải kết hợp thêm `@ResponseBody` trên cùng một method

```java
@Controller
public class HomeController {

    @RequestMapping("/")
    public String index() {
        return "index"; // Trả về file index.html
    }

    @RequestMapping("/data")
    @ResponseBody // Phải thêm cái này để trả về dữ liệu thay vì view
    public MyData data() {
        return new MyData("Hello", "World");
    }
}
```

-   **@RestController xử lý các yêu cầu RestFull**

-   `@RestController` là một annotaion thuận lợi, kết hợp chức năng `@Controller` và `@RestController` lại với nhau thiết kế đặc biệt cho dịch vụ web Restful, nơi mà các APi chỉ trả về dữ liệu (như JSOn hoặc XML), không trả về View
    -   Khi chúng ta sử dụng `RestController`, SPring mặc định hiểu rằng mọi phương thức trong lớp này sẽ trả về dữ liệu (JSON,XML) và dữ liệu này sẽ tự đọng chuyển đổi gửi thẳng về client. chúng ta ko cần phải thêm `@RestController` trên các method trong class này nữa
    -   Điều này giúp code sạch về gọn hơn rất nhiều khi làm việc vỡi các API

```java
@RestController // Tự động có @ResponseBody
public class MyRestController {

    @RequestMapping("/api/data")
    public MyData data() {
        return new MyData("Hello", "API"); // Trả về dữ liệu JSON
    }
}
```

-   **Tóm lại**

-   `@Controller` trả về `view` theo mặc định , cần thêm `@ResponseBody` để trả về dữ liệu
-   `@RestController` trả về `dữ liệu` (JSON, XML) theo mặc định, không cần `@ResponseBody`

-   Sự phân tách này giúp các nhà phát triển dễ dàng hơn trong việc quản lý và phát triển, chúng ta có thể sử dụng `@Controller` để xây dựng frontend và `@RestController` để xây dựng backend

## Các loại token

-   opaque token
    -   Không lưu trữ dữ liệu. Để triển khai xác thực, phân quyền, server thường xuyên phải gọi đến nơi chứa thông tin về opaque token để lấy về thông tin của người dùng (cuộ gọi điều tra)
-   Non-opaqua Token

    -   Lưu trữ dữ liệu, có khả năng ngay lập tức triển khai phân quyền mà ko cần gọi đi đâu
    -   JWT là cách triển khai thường được sử dụng nhất

-   Liên hệ thực tế:
    -   Opaque: Giống như một chìa khóa của một cái rương có thể sử dụng để xác định người dùng. Ta phải đi mở rương
    -   Non-Opaque: một văn bản có chữ kí, nhìn là biết là ai

## JWT acesstoken và refresh Token

-   Facebook ko sử dụng một JWT duy nhất. Mà họ sử dụng mô hình phức tạp hơn nhiều gọi là `mô hình refresh Token`

-   **Vấn đề với JWT thông thường**
-   Ta thường thiết lập thời gian hết hạn (VD 15p, 1h) để dảm bảo an toàn. Nếu một kẻ tấn công lấy được JWT của chúng ta. chúng chỉ có thể sử dụng trong một khoảng thời gian giới hạn
-   Nhược điểm là người dùng phải đăng nhập lại liên tục

-   **Mô hình refresh tokene**

-   Access Token (Token truy cập): Đây là token ngắn hạn, tương tự như JWT mà bạn thường dùng. Nó được sử dụng để truy cập các tài nguyên và API được bảo vệ. Thời gian sống của nó rất ngắn (vài phút đến một giờ).

-   Refresh Token (Token làm mới): Đây là token dài hạn. Nó không được dùng để truy cập tài nguyên mà chỉ có một mục đích duy nhất: làm mới Access Token khi nó hết hạn. Refresh Token được lưu trữ cực kỳ an toàn.

-   **Cơ chế**

-   Đăng nhập lần đầu: Khi bạn nhập email và mật khẩu, máy chủ xác thực và trả về cả hai token: một Access Token (ngắn hạn) và một Refresh Token (dài hạn).

-   Access Token được lưu trữ tạm thời trong bộ nhớ của trình duyệt hoặc Local Storage. Nó được đính kèm vào mọi yêu cầu API.

-   Refresh Token được lưu trữ trong một cookie đặc biệt, có các cờ bảo mật như HttpOnly và Secure. Cờ HttpOnly ngăn chặn mã JavaScript độc hại truy cập vào cookie, làm giảm nguy cơ bị đánh cắp.

-   Khi Access Token hết hạn, client sẽ tự động gửi yêu cầu đến máy chủ cùng với Refresh Token.

-   Máy chủ kiểm tra: Máy chủ xác thực Refresh Token. Nếu nó hợp lệ và chưa bị thu hồi, máy chủ sẽ tạo và gửi lại một cặp token mới: một Access Token mới và một `Refresh Token` mới.
-   Client lại lưu trữ cặp token mới và tiếp tục sử dụng. Quá trình này lặp lại liên tục, cho phép bạn duy trì trạng thái đăng nhập trong nhiều tháng mà không cần nhập lại mật khẩu.

-   **Tại sao lại an toàn**

    -   Giảm thiểu rủi ro: Nếu Access Token nó sẽ hết hạn rất nhanh
    -   Kiểm soát: Refresh Token được lưu trữ an toàn hơn. Quan trọng nhất, máy chủ có thể thu hồi (revoke) Refresh Token bất cứ lúc nào
    -   Tự động vô hiệu hóa: Khi bạn thay đổi mật khẩu hoặc phát hiện hoạt động đáng ngờ,

-   **Nhưng chưa đủ. Facebook họ còn có nhiều cơ chế để cân bằng giữa an toàn và duy trì lâu dài**
    -   Token Rotation: vô hiệu hóa Refresh Token cũ, phát hành accessTOken và refreshToken mới
    -   Kiểm tra ngữ cảnh (Contextual Checks) mỗi khi gửi Refresh Token,
        -   User Agent:khớp với lần hoạt động trước không
        -   Địa chỉ IP: có đến từ một địa điểm bất thường không
        -   Hành vi người dùng: Có bất kỳ hoạt động đáng ngờ nào không?
    -   Khả năng thu hồi (Revocation): có thể bị thu hồi bất cứ lúc nào từ phía máy chủ

## Opaque Token

    - chỉ là tham chiếu. Một chuỗi ngẫu nhiên ko có ý nghĩa gì
    - Luôn cần CSDL hoặc cache. Máy chủ phải tìm kiếm Token này trong kho lưu trữ để biết nó thuộc về ai (ai là người đang truy cập)
    -  Máy chủ có thể ngay lập tức xóa nó khiến nó bị vô hiệu hóa

## Facebook sử dụng Opaque Token mô hình access + refresh

    - Bảo mật thông tin: Như vậy thông tin người dùng ko bị lộ
    - Thu hồi tức thì:
    - Kiểm soát tập trung
