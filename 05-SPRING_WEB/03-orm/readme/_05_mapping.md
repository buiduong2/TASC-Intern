## Tìm hiểu cách convert DTO -> Entity và ngược lại sử dụng 2 cách : dùng thư viện và sử dụng java reflection

## DTO là gì

-   DTO (Data Transfer Object) là một đối tượng dùng để truyền dữ liệu giữa các taagnf khác nhau của ứng dụng. Đặc biệt là giữa tầng giao diện (UI/COntroller) và tầng nghiệp vụ (Serivce Repository). DTO thường chỉ chứa các truowgnf cần thiết cho việc truyền tải, không có logic nghiệp vụ

-   Việc chuyển đổi giayuwx DTO và entity là một tác vu jphoor biến để tách biện mô hình dữ liệu của CSDL (entity) và mô hìh hiển thị người dùng

## 1. Sử dụng thư viện recommemeded

-   Cách tốt nhất đẻ chuyển đổi giữa DTO và Entity là sử dụng các thư viện chuyên dụng. Chúng giupso giảm đáng kể lương jcode lpawj lại tăng tốc độ phát triển và giảm thiểu lỗi

-   `ModelMapper` và `Mapstruct` là 2 thư viện phổ biến nhất

-   a. MOdelMapepr: Sử dụng `reflection` để tự động ánh xạ các trường có cùng tên giữa 2 đối tuowgnj

    -   ưu điểm:

        -   Đễ sử dụng, khoogn cần cấu hình phức tạp
        -   Linh hoạt có thể tùy chỉnh ánh xạ

    -   Nhược điểm
        -   Có thể chậm hơn một chút so với Mapstruct vì sử dụng reflection trong quá trình chạy (runtime)

```java
// Thêm dependency vào pom.xml
// <dependency>
//     <groupId>org.modelmapper</groupId>
//     <artifactId>modelmapper</artifactId>
//     <version>3.1.1</version>
// </dependency>

// Khởi tạo ModelMapper (thường là một Bean trong Spring)
ModelMapper modelMapper = new ModelMapper();

// Chuyển đổi từ Entity sang DTO
UserDTO userDTO = modelMapper.map(user, UserDTO.class);

// Chuyển đổi từ DTO sang Entity
User user = modelMapper.map(userDTO, User.class);
```

-   b. Mapstruct:

    -   là một `annotation processor` hoạt động trong quá trình biên dịch (complie-time) nóp tự động sin hra code chuyển đổi
    -   ưu điểm:

        -   hiêu jsuaast cao, không có chi phí overlaod của reflection vì code được sin hra trước
        -   An toàn kiểu (type-safe)
        -   Giảm thiểu lỗi do đánh máy

    -   Nhược điểm :
        -   cần thêm một inetrface ánh xạ và phải biên dihcj lại ứng dụng

```xml
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.5.5.Final</version>
</dependency>
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct-processor</artifactId>
    <version>1.5.5.Final</version>
    <scope>provided</scope>
</dependency>
```

```java
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toUserDTO(User user);
    User toUser(UserDTO userDTO);
}

@Service
public class UserService {
    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public UserDTO getUser(Long id) {
        User user = userRepository.findById(id).orElse(null);
        return userMapper.toUserDTO(user);
    }
}
```

## 2. Sử dụng Java Reflection (không được khuyến khích)

-   ta có thể tư jvieets code chuyển đổi bawgndf Java Reflection . Cách này phức tạp và dễ gây ra lỗi chỉ nên dùng đẻ tìm hiểu hoặc trong những trường hợp cực kì đặc biệt
-   Cơ chế hoạt động

    -   Sử dụng `Class.getCDeclarerdFields()` để lấy tất cả các đối tượng
        -   Sử dụng `Field.setAccessible(true)` để truy cập vào các truowgnf private
        -   Sử dụng `Field.set()` và `field.get()` để đọc và ghi giá trị từ đối tượng này sang đối tương jkhasc

-   Nhược điểm:
    -   hiệu xuất kém: reflection chậm hơn nhiều so với việc gọi phương thức trực tiếp
    -   Dễ phát sinh lỗi: code rất phức tạp và khó bảo trùi
    -   Mất an toàn kiểu (typed-safety): lỗi sẽ chỉ xuất hiện trong quá trình chạy (runtime không phải khi biên dihcj)
    -   Vi phạm nguyên tắc đống gói (encapsulation) của ôp vì truy cập vào các trường private

```java
public class DTOConverter {
    public static <S, T> T convert(S source, Class<T> targetClass) throws Exception {
        T target = targetClass.getDeclaredConstructor().newInstance();
        for (Field sourceField : source.getClass().getDeclaredFields()) {
            try {
                Field targetField = targetClass.getDeclaredField(sourceField.getName());
                sourceField.setAccessible(true);
                targetField.setAccessible(true);
                targetField.set(target, sourceField.get(source));
            } catch (NoSuchFieldException e) {
                // Bỏ qua nếu trường không tồn tại trong đối tượng đích
            }
        }
        return target;
    }
}

// Cách sử dụng
UserDTO userDTO = DTOConverter.convert(user, UserDTO.class);
```

-
