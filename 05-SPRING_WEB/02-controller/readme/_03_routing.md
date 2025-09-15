## Routing

- Trong SPring Web. Routing chính là cách ánh xạ Mapping một HTTP request đến một handler (Controller method). Spring cung cấp cơ chế linh hoạt để định nghĩa routing, từ mức annotation đến API lập trình.

## Dispatcher Servlet là gì 

- Chính là `FrontController` trong mô hình `FrontController pattern`
- Tức là : thay vì mỗi Controller tực tiếp nhận request từ clietn , thì tất cả các request HTTP trước tiên sẽ đi qua `DispatcherServlet`
- `DispatcherServlet` sẽ điều phối các request đến đúng `controller` phù hợp, xử lý kết quả và  trả response về cho client

## Luồng xử lý cơ bản

- 1. Client gửi HTTP request
    - VD `GET /users/123`
- 2. DispatcherServlet nhận request
    - Nó kiểm tra xem trong SPringContext có những `handlẻrMapping `nào 

- 3. Tìm chọn Controller phù hợp
    -  Dựa trên `HandlerMapping`. Dispatcher Servlet chọn ra method phù hợp với URL `/users/123`
    - (ở mức độ code chính là ánh xạ annotation `@GetMapping("/users/{id}")`)

- 4. Chuẩn bị dữ liệu đầu vào cho Controller
    - DispatcherServlet thông qua `HandlerApdapter`  để gọi đúng method
    - Nó cũng lo inject các tham số `@PathVariable`, `@RequestParam`, `@RequestBody` thậm chí cả `Model`

- 5.  Gọi Controller method
    - ứng dụng của bạn chạy bussiness logic, truy vấn DB,  chuẩn bị `MOdel` và trả về một `viewName` hoặc `ResponseBOdy`

- 6. Xử lý View (nếu có)
    - Nếu ta trả về `tên view` VD (`userDetails`) -> DispatcherServlet dùng `ViewRessolver` để tìm file JSP/Thymeleaf tương ứng
    - Nó nhét dữ liệu từ `Model` vào view này

- 7. Trả Response về cho Client 
    - Nếu là View -> JSP/ Thymeleaf sẽ render HTML trả về 
    - nếu là Rest API (@RestController) -> dữ liệu (JSON/XML) được viết thẳng bào `HTTP response body`


## Cách câu hình ROuting thông qua ANnotation

- Đầu tiên chúng ta cần đánh dấu một controller bằng annotation `@Controller` hoặc `@RestController`
- Đặt route bằng `@RequestMapping` (hoặc các annotation rút gọn như `@GetMapping` `@PostMapping`, ...)
- SPring tự động cấu hình `DispatcherServlet`, nên chúng ta ko cần viết XML như xưa

- `RequestMapping` là cách cấu hình tổng quát để chỉ định value path , method http, procedures, consumes

- Để trích xuất dữ liệu tư trong request 
    - `@PathVariable`: lấy giá trị từ url `/users/{id}`
    - `@RequestParam`: lấu query paramêtr `/users?id=1`
    - `@RequestBody`: lấy JSON/XML body
    - `@RequestHeader`: lấy ra header
    - `@CookieValue` -> Lấy ra cookie