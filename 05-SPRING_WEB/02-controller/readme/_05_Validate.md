## Validate input trong controller của Spring MVC

-   bạn có thể sử dụng Bean Validation API,Spring Validation

-   **Sử dụng dependency**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
<!-- nó là sự kết hợp của hibernate-validator và jakarta.validation-api -->
```

-   **2. Định nghĩa các Rules trong Model Class**

-   Sử dụng các annotation validation trực tiếp trên các trường (fields) của model class (DTO - Data Transfer Object) mà bạn nhận trong `@RequestBody.`

```java
public class User {
    @NotNull(message = "Tên không được để trống")
    @Size(min = 2, max = 50, message = "Tên phải có từ 2 đến 50 ký tự")
    private String name;

    @Min(value = 18, message = "Tuổi phải lớn hơn hoặc bằng 18")
    private int age;

    @Email(message = "Email không hợp lệ")
    private String email;

    // getters and setters
}
```

-   Một số annotation phổ biến khác bao gồm: `@NotEmpty`, `@NotBlank`, `@Positive`, `@Negative`, `@Pattern.`

-   **3. Kích hoạt Validation trong Controller**
-   Trong controller method, thêm annotation `@Valid` hoặc `@Validated` trước tham số `@RequestBody` để Spring biết rằng nó cần chạy quá trình validation cho đối tượng đó.

```java
import jakarta.validation.Valid;

@PostMapping("/users")
public ResponseEntity<String> createUser(@Valid @RequestBody User user) {
    // Nếu validation thành công, code trong method sẽ được thực thi.
    // Nếu validation thất bại, một ngoại lệ MethodArgumentNotValidException sẽ được ném ra.
    return ResponseEntity.ok("Người dùng đã được tạo thành công.");
}
```

-   Khi validation thất bại, Spring sẽ ném ra một ngoại lệ `MethodArgumentNotValidException`. Bạn cần tạo một `Global Exception Handler` để bắt và xử lý ngoại lệ này một cách tập trung, thay vì để mỗi `controller` tự xử lý.
-   Sử dụng annotation `@ControllerAdvice` để tạo một lớp xử lý ngoại lệ toàn cục:

```java
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage()));
            //Khi có lỗi, thay vì trả về lỗi 500, ta sẽ trả về một phản hồi 400 Bad Request với một JSON chứa thông tin chi tiết về các lỗi validation.
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
```

-
