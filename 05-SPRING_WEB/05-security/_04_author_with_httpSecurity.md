## Phân quyền sử dụng httpSecurity

- Phân quyền bằng `httpSecurity` trong spring security là một cách tiếp cận mạnh mẽ và chi tiết hơn so với annotation. nó cho phép ta cấu hình quy tắc phân quyền cho từng URL hoặc đường dẫn cụ thể, thường được thực hiện trong một lớp cấu hình

### 1. Cơ bản về `HttpSecurity`

- `HttpSecurity` là một đối tượng trong spring Security cho phép ta tùy chỉnh việc bảo vệ HTTP trong ứng dụng của mình. Ta có thể định nghĩa các quy tắc cho từng `request` (yêu cầu) dựa trên URL, phương thức HTTP (GET, POST,PUT, DELETE) và các vai trò quyền của người dùng

### 2. Cấu hình bằng `HttpSecurity`

- Để sử dụng `HttpSecurity` ta cần tạo một lớp cấu hình (thường là một class có `@Configuration`) và định nghĩa một `Bean` của `SecurityFilterChain`

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/public/**").permitAll() // URL bắt đầu bằng /public thì cho phép truy cập công khai
                .requestMatchers("/api/admin/**").hasRole("ADMIN") // URL bắt đầu bằng /api/admin thì chỉ dành cho người có vai trò ADMIN
                .requestMatchers("/api/user/**").hasAnyRole("ADMIN", "USER") // URL bắt đầu bằng /api/user thì dành cho cả ADMIN và USER
                .anyRequest().authenticated() // Tất cả các URL khác đều phải được xác thực (đăng nhập)
            )
            .formLogin(form -> form.permitAll()) // Cho phép form đăng nhập mặc định
            .logout(logout -> logout.permitAll()); // Cho phép đăng xuất

        return http.build();
    }
}
```

- trong ví dụ trên: 
    - `requestMathcers("/public/**").permitAll()`. mọi yêu cầu đến đường dẫn `/public` và các đường dẫn con của nó đều được truy cập mà không cần xác thực. Đây là cách ta tạo các endpoint công khai như trang chủ, trang đăng kí

    - `requestMathcers("/api/admin/**").hasRole("ADMIN")`. Mọi yêu cầu đến các đường dẫn `/api/admin` chỉ được phép nếu người dùng có vai trò ADMIN

    - `requestmathcers("/api/user/**").hasAnyRole("ADMIN","USER")`: mọi yêu cầu đến các đường dẫn `/api/user`  chỉ được phép nếu người dùng có vai trò `ADMIN` hoặc `USER`

    - `anyRequest().authenticated()` : đây là một quy tắc catch-all (quy tắc cuối cùng). Nó đảm bảo bát kì yêu cầu nào không khớp với các quy tắc trên đều phải được xác thực

### 3. ưu điểm so với `Annotation`

- `Tập trung và tổng quan`:  Ta có thể xem và quản lý tất cả các quy tắc phân quyền cho các URL tại một nơi duy nhất. Điều này giúp ta dễ dàng nắm bắt tổng thể về cách ứng dụng được bảo mật

- `Linh hoạt hơn`: Ta có thể quy tắc cho cả một nhóm URL, mà không chỉ riêng từng phương thức. Điều này rất hữu ích khi ta có nhiều endpoint với cùng một yêu cầu bảo mật

- `Tách biệt mối quan tâm`: Logic phân quyền nên được tách ra khỏi logic của Controller, giúp code của ta sạch sẽ và dễ bảo trì hơn

### 4. Kết hợp cả 2 phương pháp

- Trên thực tế, ta có thể kết hợp cả 2 phương pháp để tận dụng ưu điểm của từng loại 
    - Sử dụng `HttpSecuirity` để phân quyền ở cấp độ cao. Bảo vệ các nhóm URL lớn (VD `/api/admin/**` chỉ dành cho quản trị viên) 

    - Sử dụng `@PreAuthorize` để phân quyền chi tiết hơn cho cấp độ phương thức, áp dụng các quy tắc phức tạp hơn dựa trên dữ liệu đầu vào hoặc trạng thái của đối tượng (`@PreAuthozie('#userId == principal.id')`) để đảm bảo người dùng chỉ có thể truy cập tìa nguyên của chính họ