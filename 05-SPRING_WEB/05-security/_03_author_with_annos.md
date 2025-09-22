## Phân quyền sử dụng annotion

### 1. Phân quyền trên phương thức `@PreAuthorize`

-   Đây là một annotatio nđược sử dụng rộng rãi nhất để kiểm soát quyền truy cập trước khi một phương thức được thực thi. Ta có thể sử dụng các biểu thức ngôn ngữ SPring Expression Language (SpEL) để định nghĩa các quy tắc phức tạp

#### Biểu thức phổ biến

-   `hasRole('ROLE_ADMIN')`: người dùng phải có vai trò `ROLE_ADMIN`
-   `hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')`: người dùng có nhất một trong các vai trò

-   `hasAuthority('READ_PRODUCTS')` người dùng có quyền `READ_PRODUCTS`

-   `hasAnyAuthority('READ_PRODUCTS', 'WRITE_PRODUCTS')` : người dùng có một trong các quyền

-   `permitAll()`: cho phép truy cập công khai mà không xác thực

-   `denyAll()`: từ chối tất cả các yêu cầu

-   `isAuthentication()`: cho phép người dùng đã xác thực (đăng nhập)

```java
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @GetMapping
    public String getAllProducts() {
        return "Danh sách tất cả sản phẩm (Public)";
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String createProduct() {
        return "Tạo sản phẩm mới (Chỉ dành cho ADMIN)";
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String deleteProduct(@PathVariable Long id) {
        return "Xóa sản phẩm (Chỉ dành cho ADMIN hoặc MANAGER)";
    }
}
```

-   Trong ví dụ trên

    -   Phương thức `getAllProducts()` không có annotation nào nên mặc định sẽ được truy cập công khai (nếu ta đã cấu hình như vậy trong SecurityFilterChain)

    -   phương thức `createProduct()` chỉ có thể được gọi bởi người dùng có vai trò `ADMIN`
    -   phương thức `deleteProduct()` chỉ có thể được gọi bỏi dùng có vai trò `ADMIN` hoặc `MANAGER`

### 2. Phân quyền trên lớp `@PreAuthorize` và `@Secured`

-   ta có thể đặt annotation `@PreAuthorize` trực tiếp trên một lớp. Khi đó , tất cả các phương thức trong lớp đó sẽ kế thừa quy tắc phân qquyeenf, trừ khi một phương thức có quy tắc riêng

```java
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')") // Tất cả các phương thức trong lớp này đều yêu cầu vai trò ADMIN
public class UserController {

    @GetMapping
    public String getAllUsers() {
        return "Danh sách tất cả người dùng";
    }

    @GetMapping("/{id}")
    public String getUserById(@PathVariable Long id) {
        return "Thông tin người dùng có ID: " + id;
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        return "Xóa người dùng có ID: " + id;
    }
}
```

### 3. Kích hoạt Annotation-based Security

-   Để các annotation `@PreAuthorize` hoạt động. ta phải kích hoạt nó trong lớp cấu hình bảo mật

```java
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class MethodSecurityConfig {
    // Không cần thêm code, chỉ cần annotation này
}
```

-   `@EnableMethodSecurity` đây là annotation chính để bật tính năng bảo mật trên phương thức
-   `prePostEnabled = true` : kích hoạt `@PreAuthozier` và `@PostAuthozie`

-   `securedEnabled = true` kích hoạt annotation `@Secured` (đơn giản hơn chỉ chấp nhận danh sách và vai trò)

### 4. So sánh @PreAuthize va @Secured

| Tiêu chí        | `@PreAuthozie`                                     | `@Secured`                                       |
| --------------- | -------------------------------------------------- | ------------------------------------------------ |
| Biểu thức       | Hỗ trợ Spring Expression Language (SpEL) linh hoạt | Chỉ chấp nhận danh sách chuỗi VD `ROLE_ADMIN`    |
| Kiểu phân quyền | Kiểm tra trước khi phương thức chạy                | Kiểm tra trước khi phương thức chạy              |
| Tính linh hoạt  | rất cao có thể sử dụng các biểu điều kiện phức tạp | Đơn giản chỉ dùng để gán vai trò                 |
| Khuyến cáo      | `nên sử dụng` vì nó linh hoạt và mạnh mẽ hơn nhiều | Hạn chế, chỉ sử dụng cho các trường hợp đơn giản |
