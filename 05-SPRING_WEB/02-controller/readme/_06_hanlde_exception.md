## Cách xử lý nghoiaj lệ trong Spring 

- Để xử lý ngoại lệ (exception handling) trong Spring MVC, có ba cách chính, mỗi cách phù hợp với một phạm vi và mục đích khác nhau.

- **1. Per-Controller Exception Handling (@ExceptionHandler)**

- Đây là cách xử lý ngoại lệ `cục bộ` trong một controller cụ thể, Bạn định nghĩa một phương thức trong controller và đánh dấu nó bằng `@ExceptionHandler`, chỉ định loại ngoại lệ mà nó sẽ bắt.
- Ưu điểm: Dễ triển khai, thích hợp cho việc xử lý các ngoại lệ chỉ liên quan đến một controller duy nhất.
- Nhược điểm: Không thể tái sử dụng. Bạn sẽ phải viết lại code xử lý cho cùng một loại ngoại lệ trong các controller khác.

```java
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/users/{id}")
    public String getUser(@PathVariable int id) {
        if (id == 0) {
            throw new IllegalArgumentException("ID cannot be 0.");
        }
        return "User found with ID: " + id;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
```

- **2. Global Exception Handling (@ControllerAdvice)**

- Đây là phương pháp được khuyến khích nhất. Bạn tạo một lớp riêng, đánh dấu bằng `@ControllerAdvice` và sử dụng `@ExceptionHandler` bên trong để xử lý ngoại lệ cho `toàn bộ ứng dụng`  
-  `Ưu điểm`: Xử lý tập trung, giúp code gọn gàng, có khả năng tái sử dụng cao. Bạn chỉ cần viết một lần cho các ngoại lệ phổ biến như `MethodArgumentNotValidException` (lỗi validation).

- Nhược điểm: Có thể trở nên cồng kềnh nếu bạn xử lý quá nhiều loại ngoại lệ khác nhau trong cùng một lớp.


```java
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return new ResponseEntity<>("An internal server error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
```

- **3. Implementing HandlerExceptionResolver**

- Đây là cách xử lý ngoại lệ cấp thấp, cho phép bạn kiểm soát toàn bộ quá trình xử lý ngoại lệ một cách chi tiết. Bạn tạo một lớp và implement interface HandlerExceptionResolver.

- Ưu điểm: Cung cấp khả năng kiểm soát tuyệt đối, hữu ích khi bạn cần xử lý các trường hợp phức tạp, ví dụ như chuyển hướng người dùng tới một trang lỗi tùy chỉnh dựa trên loại ngoại lệ.

- Nhược điểm: Rất phức tạp, không được khuyến khích cho các trường hợp thông thường.

```java
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

public class MyHandlerExceptionResolver implements HandlerExceptionResolver {

    @Override
    public ModelAndView resolveException(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Object handler,
                                         Exception ex) {
        // Tùy chỉnh logic xử lý ngoại lệ tại đây
        ModelAndView modelAndView = new ModelAndView("error-page");
        modelAndView.addObject("message", "A generic error occurred.");
        return modelAndView;
    }
}
```

- So sánh và Lựa chọn
    - Hầu hết các trường hợp: Nên sử dụng @ControllerAdvice để xử lý ngoại lệ tập trung và hiệu quả. Nó là sự cân bằng tốt nhất giữa tính linh hoạt và sự đơn giản.
    - Các trường hợp đặc biệt: Sử dụng @ExceptionHandler trong controller nếu bạn chỉ cần xử lý một ngoại lệ duy nhất cho một phương thức hoặc controller cụ thể.
    - Các trường hợp nâng cao: Chỉ cân nhắc HandlerExceptionResolver khi bạn có yêu cầu xử lý ngoại lệ rất phức tạp, vượt quá khả năng của hai cách trên.