## @Transactional 

- @Transactional là một chú thích (annotation) trong Spring Framework, được sử dụng để quản lý các giao dịch (transactionals). Trong `SpringBOot JPA`, nó đặc biệt quan trọng vì nó đảm bảo tính toàn vẹn của dữ liệu khi thực hiện các thao tác trên cơ sở dữ liệu

## Cách sử dụng @Transactional 

- Khi chúng ta thêm `@Transactional` vào một phương thức (method) hoặc một lớp (class) trong Springboot  sẽ tự động xử lý việc bắt đầu và kết thúc giao dịch cho chúng ta 

- **Ví dụ đơn giản**

- Giả sử chúng ta có một dịch vụ để cập nhật thông tin người dùng

```java
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void updateUser(User user) {
        // Thao tác 1: Cập nhật tên người dùng
        userRepository.updateName(user.getId(), user.getName());

        // Thao tác 2: Giả sử có một lỗi xảy ra ở đây
        // throw new RuntimeException("Có lỗi xảy ra!");

        // Thao tác 3: Cập nhật email người dùng
        userRepository.updateEmail(user.getId(), user.getEmail());
    }
}
```

- Nếu có một ngoại lệ (exception) xảy ra sau khi `updateName` được gọi nhưng trước khi `updateEmail` được gọi. Spring sẽ tự động `quay ngược lại` `rollback` cả 2 thao tác. Điềun ày đảm bảo rằng không có thay đổi nào được lưu vào CSDL và dữ liệu vẫn giữ được tính nhất quán

## Mức độ sử dụng

-  `Trên phương thức`: đây là cách phổ biến nhát. Giao dịch sẽ bắt đầu khi phương thức được gọi và trước khi phương thức kết thúc

- `Trên lớp`: tất cả các phương thức công khai (public methods) trong lớp đó đều được áp dụng giao dịch


## Các thuộc tính quan trọng

- `@Transactional` có một thuộc tính giúp chúng ta tùy chỉnh hành vi của giao dịch

    - `readOnly = true` khi ta chỉ đọc dữ liệu mà không thay đổi gì. hãy sử dụng thuộc tính này. Vì nó tối ưu hóa hiệu suất vì Spring có thể áp dụng các chiến lược tối ưu hơn cho các giáo dịch chỉ đọc

```java
@Transactional(readOnly = true)
public User findUserById(Long id) {
    return userRepository.findById(id).orElse(null);
}
```

- `rollack` và `noRollbackFor`: ta có thể chỉ định loại ngoại lệ nào sẽ gây ra rollback và loại nào thì không,. mặc định `@Transactional` sẽ rollback cho các `RuntimeException` và `Error` nhưng không rollback cho các `CheckedException`

```java
@Transactional(rollbackFor = CustomException.class)
public void processData() throws CustomException {
    // ...
}
```

- `propagation`: thuộc tính này xác định cách cấc giao dịch lồng nhau hoạt động. `Propagation,.REQUIRED (mặc định)`  sẽ sử dụng giao dịch hiện tại nếu có, hoặc một giao dịch mới nếu chưa có . `Progapogation.REQUIRES_NEW` luôn tạo một giao dịch mới độc lập


## Khi nào nên sử dụng `@Transactional`

- Ta nên sử dụng `@Transactional` trên các phương thức dịch vụ (Service methods) mà chỉ thực hiện các thao tác `ghi, cập nhaamt hoặc xóa` dữ liệu. nó không chỉ đảm bảo tính toàn vẹn dữ liệu mà còn quản lý tài nguyên CSDL một cách hiệu quả

- `Lưu ý quan trọng` : `@Transascitonal` hoạt động thông qua một cơ chế gọi là `AOP (Aspect-Oriented Programing)` . Điều này có nghĩa là nó sẽ không hoạt động nếu ta gọi một thức đánh dấu `@Transactional` từ một phương thức khác `cùng trong một dối tượng` (self invocation). Dể giải quyết vấn đề này, ta có thể tách phương thức đó ra một đối tượng khác. Hoặc sử dụng `AopContext.currentProxy()`

## Sự khác biệt giữa Rollback cho checked và uncheckedException.

- THeo mặc định `@Transasctional` chỉ rollback cho giao dịch khi gặp các `UncheckedException` (như `RuntimeException`) và `Error`. nó sẽ không rollback khi gặp `CheckedException` như `IOE Exception` hay các `Exception` tùy chỉnh mà không kế thừa từ `RuntimeException`

- Để thay đổi hành vi này, chúng ta có thể sử dụng các thuộc tính `rollbackFor` và `noRollbackFor`

    - `rollBackFor = MyCheckedException.class` : sẽ rollback khi gặp `MyCheckedExcpetion`

    - `NoRollbackFor = MyUncheckedExcpetion.class`> sẽ không rollback khi gặp `MyUnCheckedException` ngay cả khi nó là một `RuntimException`

### @Transascitonl ở tầng Service

- Thực tiễn tốt nhât là đặt `@Transactional` ở `tầng service` không phải COntroller hay Repository
    - `Lý do`: tầng Service là nơi chứa logic nghiệp vụ (business logic). Một phương thức Service có thể thực hiện nhiều thao tác Repository. Việc nhóm các thao tác này vào một giao dịch duy nhất ở tầng Service đảm bảo tính toàn vẹn của dữ liệu cho toàn bộ logic nghiệp vụ