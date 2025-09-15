## Binding dữ liệu trong Controller

- `URL Template`: là một mẫu URL động có chứa biến (parameters) được đặt trong dấu ngoặc {}.

- Server định nghĩa URL template, sau đó khi client gửi request thật sự thì server sẽ thay thế {} bằng giá trị cụ thể trong URL.

- 
## Gán URI variable template với @PathVariable

- The annotation `@PathVariable`  được sử dụng với một parameter trong method xử lý để bắt giá trị của một a URI template  variable.

- **Params (Request Parameters)**

- Params, hay còn gọi là request parameters, là các cặp key-value xuất hiện ở cuối URL, sau dấu ?. Chúng thường được sử dụng cho việc lọc, phân trang hoặc truyền dữ liệu không quá nhạy cảm.

- Sử dụng annotation @RequestParam
- GET /users?id=123&name=john

```java
@GetMapping("/users")
public String getUserById(@RequestParam("id") int userId, @RequestParam(name = "name", required = false) String userName) {
    // userId sẽ nhận giá trị 123
    // userName sẽ nhận giá trị "john"
    return "User ID: " + userId + ", Name: " + userName;
}
```

- **Body (Request Body)**

- Body chứa dữ liệu chính của một yêu cầu HTTP, thường là JSON hoặc XML, . Nó được sử dụng chủ yếu trong các yêu cầu POST, PUT, và PATCH để gửi dữ liệu lớn hoặc phức tạp.
- Cách nhận: Sử dụng annotation @RequestBody, Spring sẽ tự động chuyển đổi JSON/XML thành một đối tượng Java.
- VD POST /users với body là { "id": 1, "name": "Alice" }
```java
@PostMapping("/users")
public User createUser(@RequestBody User user) {
    // Spring sẽ chuyển đổi JSON thành đối tượng User
    return user;
}
```

- Để sử dụng @RequestBody, bạn cần có một lớp Java tương ứng (ví dụ: lớp User)

- **Path Variable**

- Path variables là các giá trị được nhúng trực tiếp vào URL path. Chúng thường được dùng để xác định một tài nguyên cụ thể

- Cách nhận: Sử dụng annotation @PathVariable
- GET /users/123

```java
@GetMapping("/users/{id}")
public String getUserById(@PathVariable("id") int userId) {
    // userId sẽ nhận giá trị 123
    return "User ID from path: " + userId;
}
```

- **Header (Request Headers)**

- Headers chứa các thông tin metadata về yêu cầu HTTP, chẳng hạn như loại nội dung (Content-Type), thông tin xác thực (Authorization) hoặc user agent.

- Cách nhận: Sử dụng annotation @RequestHeader
- Ví dụ: GET /data với header Authorization: Bearer abcde123

```java
@GetMapping("/data")
public String getData(@RequestHeader("Authorization") String authorizationHeader) {
    // authorizationHeader sẽ chứa "Bearer abcde123"
    return "Authorization header: " + authorizationHeader;
}
```

## Cách để liên kết dữ liệu đầu vào Controller

- **1. Handler Method Arguments Resolvers**

- Đây là thành phần cốt lõi thực hiện việc phân giải các đối số (arguments) của phương thức trong controller. Khi một yêu cầu HTTP đến, Spring MVC sẽ duyệt qua danh sách các HandlerMethodArgumentResolver đã đăng ký để tìm ra bộ phân giải phù hợp cho từng tham số của phương thức.

- VD 
    - `RequestParamMethodArgumentResolver` xử lý các tham số được đánh dấu bằng `@RequestParam`
    - `PathVariableMethodArgumentResolver` xử lý các tham số được đánh dấu bằng `@PathVariable`
    - `RequestResponseBodyMethodProcessor` xử lý các tham số được đánh dấu bằng `@RequestBody.`

- **2. Message Converters**

- Các `HttpMessageConverter` chịu trách nhiệm chuyển đổi dữ liệu từ body của yêu cầu HTTP ví dụ: JSON, XML thành đối tượng Java và ngược lại. Điều này đặc biệt quan trọng khi sử dụng @RequestBody.
    - Khi nhận yêu cầu: Một HttpMessageConverter sẽ đọc dữ liệu từ InputStream của yêu cầu và chuyển đổi nó thành một đối tượng Java.

    - Khi gửi phản hồi: Một HttpMessageConverter sẽ chuyển đổi đối tượng Java thành định dạng phản hồi (ví dụ: JSON) và ghi vào OutputStream của phản hồi.

- Các converter phổ biến bao gồm `MappingJackson2HttpMessageConverter` cho JSON và `Jaxb2RootElementHttpMessageConverter` cho XML.

## Các Component cụ thể của Servlet 

-  Mặc dù Spring MVC trừu tượng hóa nhiều chi tiết, nó vẫn hoạt động trên nền tảng của Servlet API.

    - `HttpServletRequest` Đối tượng này chứa toàn bộ thông tin của yêu cầu HTTP, bao gồm params, headers, và body. Spring sử dụng đối tượng này để trích xuất các thông tin cần thiết.

    - `DispatcherServlet`: Đây là front controller của Spring MVC. Nó nhận tất cả các yêu cầu và ủy quyền cho các thành phần khác để xử lý, bao gồm việc tìm controller phù hợp và chuyển giao dữ liệu đầu vào.



