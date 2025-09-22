## JWT là gì

-   `JWT` (viết tắt của `JSON Web Token`) là một chuẩn mở (RFC-7519) để truyền thông tin một cashc an toàn dưới dạng một đối tượng JSON. Thông tin này có thể được xác minh và đáng tin cậy vì nó được kí số (digitally signed)

-   Một cách đơn giản, hãy coi JWT như một tấm vé kĩ thuật số. Khi ta đang nhập thành công, máy chủ sẽ cấp cho ta một tấm vé (token). Tấm vé này không chỉ xác nhận danh tính của ta mà chứa một số thông tin (như vai trò của chúng ta, thời gian hết hạn)

-   một cách đơn giản, hãy coi JWT như một tấm vé kĩ thuật số. Khi ta đăng nhập thành công, máy chủ sẽ cấp cho ta một tấm vé (token). Tấm vé này không chỉ xác nhận danh tính của ta mà còn chứa một số thông tin (như vai trò của chúng ta, thời gian hết hạn). Khi ta gửi yêu cầu đến máy chủ sau đó ta chỉ cần đưa kèm tấm vé này, và máy chủ sẽ biết ta là ai và được phép làm gì, mà không cần phải xác nhận lại bằng cách hỏi tên đăng nhập/ mật khẩu

## Cấu trúc của JWT

-   Một JWT luôn có 3 thành phần tách bằng dấu `.`

`header.payload.signature`

### 1. Header (tiêu đề)

-   Header là một đối tượng JSON chứa 2 thông tin chính
    -   `alg (algorithm)`: thuật toán dùng để kí token. Phổ biến nhất lHS256 và RS256
    -   `typ(Type)` loại token, thường là `JWT`

```json
{
	"alg": "HS256",
	"typ": "JWT"
}
```

-   pHanafn header này sau đó được mã hóa (base 64urrl encoded) để trở thành phần đầu tiên của JWT

### 2. Payload (nội dung)

-   Payload là một đối tượng JSON chứa các `claims` (các khẳng định hoặc thông tin) . Đây là phần quan trọng nhất ví nó chứa các dữ liệu thực tế mà ta muốn truyền đi. Có 3 loại claims

-   `Registered Claims (các Claims đã đăng kí)`: đây là những claimns được định nghĩa trước và không bắt buộc, nhưng được khuyến khích sử dụng để đảm bảo tính tương thích

    -   `iss (Issuer)` bên phát hành token
    -   `exp (Expiration time)`: thời gian hết hạn của token (tính bằng giây)
    -   `sub (Subject)` Chủ thể của token (VD id của người dùng)
    -   `iat (issuaed At)`: thời gian token được phát hành

-   `public claims (các claims công khai)`: được định nghĩa bởi người sử dụng JWT, có thể đăng kí trên IANA Web Token Registrty

-   `private claims (Các claims riêng tư)`
    -   Được tạo ra để trao đổi thông tin giữa các bên (VD thêm vai trò của người dùng `role:"admin"` ) tên của các claims này phải được thống nhất giữa người gửi và người nhận

```json
{
	"sub": "1234567890",
	"name": "John Doe",
	"role": "admin",
	"iat": 1516239022
}
```

-   Tương tự như header, payload cũng được mã hóa (base64url enceded) để trở thành phần thứ 2 của JWT

### 3. Signature (chữ kí)

-   Chữ kí là phần đảm bảo tính toàn vẹn và xác thực của token. nó được tạo ra bằng cách
    -   1. Lấy phần `header đã được mã hóa` và phần `payload dã được mã hóa`
    -   2. ghép chúng lại với nhau bằng dấu chấm `.`
    -3. Sử dụng thuạt toán được chỉ định trong header `alg` cùng một với một `khóa bí mật (secret key)` chỉ có máy chủ biết để ekis chuỗi đã ghép

- `Công thức signature = HASH_HMAC_SHA256(base64UrlEncode(header) + "." + base64UrlEncode(payload),secret)` 

- Phàn chữ kí này giúp máy chủ xác minh rằng token không bị thay đổi bởi bất kì ai. Nếu ai đó cố gắng thay đổi nội dung header hoặc payload. Chữ kí sẽ không khớp và token sẽ bị từ chối