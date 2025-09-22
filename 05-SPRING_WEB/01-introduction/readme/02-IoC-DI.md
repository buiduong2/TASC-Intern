## Tight coupling (phụ thuộc chặt chẽ)

- `Tight coupling` là một `tự tạo ra hoặc gắn chặt`

```java
public class UserService {
    private UserRepository userRepository = new UserRepository();
}
```

- `userService`tự new`UserRepository`
- Nghĩa là UserService chỉ hoạt động được với đúng UserRepository này
- Nghĩa là nếu muốn thay thế `UserRepository` bằng một class khác (VD `JpaRepository`)
  ta `phải sửa code bên trong` `UserSerivce` -> gây rắc rối và kém linh hoạt

- **Khó test**

- nếu muốn viết UniTest cho `UserService`, ta có thể muốn sử dụng Mock thay vif usserRepository thaajt

    - Nhuwg neus UserService tự tạo ra `new UserRepository()`, thì bạn không thể thay thế bằng mock
    - Điều này khiến việc test khó khăn và phức tạp hơn.

    - Ta muốn thay đổi DB mỗi lần test sẽ ko thể thay đổi được. Còn kia tận dụng đa hình nên thích làm gì thì làm
    - thay đổi hành vi của một field để có thể test dễ dàng (chèn vòa mock, chèn vòa triển khai ko làm gì cả, một triển
      khai cho môi trường cấu hình khác)
    -

- Khó thay đổi / mở rộng

## DI

- **Định nghĩa**

- Dependency Injection là việc các Object nên phụ thuộc vào các Abstract Class và thể hiện chi tiết của nó sẽ được
  Inject vào đối tượng lúc runtime

- **Vấn đề DI giải quyết**
- Class A cần dùng đến Class B.. nếu sử dụng `new` thi A bị gắn chặt với implementation cụ thể của B → khó thay đổi, khó
  test.

- `Đảo ngược việc tạo dependency`
    - A chỉ cần khai báo tôi cần mọt B
    - Một bên thứ ba (IoC Container, hoặc code bên ngoài) sẽ tiêm (inject) B vào cho A.
- A không cần biết B được tạo thế nào, chỉ cần biết interface của B để sử dụng.

- **Nguyên lý cốt lõi**

    - Class cấp cao (High-level module: Service) không phụ thuộc trực tiếp vào class cấp thấp (Low-level module:
      Repository).Cả hai cùng phụ thuộc vào abstraction (interface).

- **Cách triển khai DI (Injection types)**

    - Constructor Injection – dependency truyền qua constructor,. đảm bảo dependency luôn có, dễ test
    - Setter Injection – dependency truyền qua setter..Linh hoạt hơn, nhưng dependency có thể bị bỏ quên.
    - Field Injection – dependency gán thẳng vào field. Ngắn gọn nhưng khó test → ít khuyên dùng.

- Class không nên chịu trách nhiệm tạo ra dependency mà nó dùng.
- Class chỉ nên tuyên bố nó cần gì.
- Việc “cung cấp cái cần” được ủy quyền cho bên ngoài.

- **trong Spring**

- _1. Spring như một "container" ứng dụng_

    - Nhờ `IoC/DI` Spring trở thành một container `quản lý tất cả thành phần của hệ thống`

        - Tất cả các hệ thống như Controller , Service, Repository, Config, Security, Messaging Cache, Transaction
        - Đều là Bean do Spring quản lý

    - Mọi thành phần trong hệ thống có thể `kết nối với nhau một cách thống nhất` thông qua container (ta ko cần phải
      tìm nơi và chèn thay đổi hành vi của mình), nhưng đoạn new rời rạc

- _2. Mở đường cho Modularity và Plugin_

    - Có thể `bật tắt và thay thế` module rất dễ dàng:
        - Muốn thêm tính năng security -> tải thêm starter
        - Chuyển đổi Cache từ `inmemory` sang `Redis`-> chỉ config, không đụng code
        - Chuyển từ Monothic sang Microservice -> SPring Cloud cắm thêm vòa mà không làm vỡ code

- _3. Khả năng tích hợp công nghệ ngoài cực dễ_

    - Database: JPA, Hibernate, MyBatis…
    - Messaging: Kafka, RabbitMQ, ActiveMQ…
    - Cloud: AWS, Azure, GCP…
    - Monitoring: Actuator, Micrometer, Prometheus…

    - thích cái gì chỉ cần cắm vào. Nó sẽ được chèn vào các điểm mà chúng ta mong muốn , Mà không cần thay đổi toàn bộ
      logic hay các code cũ (vì chúng thiết kế theo hướng giảm buộc sẽ ràng buộc)

- _4. Thế giới auto-configuration_
    - BootBoot nhìn vào classpath → biết bạn muốn gì.
    - Boot tạo bean, inject vào app
    - Bạn có thể override dễ dàng bằng bean của mình.
    - Ứng dụng chỉ cần tạo ra các điểm mở rộng. Chúng ta có thể khia báo các nghiệp vụ mà Spring sẽ tư jgawns vào
-

## Khái niệm IoC

- `IoC (inversion of Control)`: nghĩa là `Đảo ngược quyền điều khiển`

- **Định nghĩa**

    - Dependency Injection giúp giảm thiểu sự phụ thuộc giữa các dependency với nhau, dễ mở rộng code. Tuy nhiên nếu một
      class có hàng chục dependency, việc inject thủ công chúng có thể đem lại chút bất tiện. . Nên nó sẽ tiêm cá sự phụ
      thuộc đó cho chúng ta

- trong lập trình thông thường, `chính code của chúng ta` sẽ `tạo ra đối tượng và điều khiển luồng xử lý`
- Với IoC `SpringFramework` (hay IoC Container) sẽ chịu trách nhiệm `tạo ra, quản lý vòng đời và tiêm các đói tượng` khi
  cần thiết
- Nói đơn giản: thay vì `tự tạo new Object`, thì Spring sẽ đưa Object cho chúng ta dùng

```java
public class UserService {
    private UserRepository userRepository = new UserRepository(); // Tự tạo
    // user service tự tạo userRepository
    // Gây ra sự phụ thuộc chặt chẽ (tight coupling) -> khó test, khó thay đổi
}


```

- VD về Spring

```java

@Component // ứng dụng sẽ tự tạo Các , và được Spring quản lý Bean
public class UserRepository {
    // code ...
}

@Service
public class UserService {
    private final UserRepository userRepository;

    // Spring sẽ inject UserRepository vào UserService
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
```

- Lợi ích:

    - Giảm phụ thuộc chặt chẽ
    - Dễ test (mock dependency)
    - Quản lý vòng đời bean: tạo, hủy,.. scope đều do Spring quản lý
    - Tái sử dụng code và tăng tính linh hoạt

- **Xuất hiện trong Spring**

- Starter trong Spring Boot và IoC
    - Nó mang theo các bean (controller, service, repository, config…) đã được định nghĩa sẵn.
    - IoC Container sẽ quét và khởi tạo chúng cho bạn.
    -
- Custom behavior

    - IoC giúp bạn dễ dàng `ghi đè` hoặc `bổ sung hành vi` mà starter cung cấp.
    - @bean, @Controller (tự động xử dụng trong SPring)

- Auto-configuration

    - Spring Boot dùng cơ chế @EnableAutoConfiguration.
    - Dựa vào các dependency trong classpath, nó quyết định sẽ tạo những bean nào.
    - mà chỉ cần khai báo “mình muốn dùng tính năng này

- Tập trung vào logic nghiệp vụ thay vì quản lý luồng thực thi.

    - Chỉ tạo các class làm việc với bussiness còn vận hành hệ thống thì để framework lo
    -

- **So sánh với Library**
- `Library` tập hợp các hàm/lớp bạn `có thể gọi khi cần`
- Chúng ta điều khiển luồng chạy, quyết định khi nào gọi, gọi cái gì

```java
List<String> list = new ArrayList<>();
Collections.

sort(list); // bạn chủ động gọi
```

- `framework`
    - một bộ khung , định nghĩa sẵn luồng chạy của ứng dụng
    - `Gọi ngược lại code của chúng ta` : tại những điểm mở rộng
    - Đây chính là IoC ở cấp độ toàn hệ thống
        - Không phải chúng ta điều khiển framework
        - Mà framework điều khiern chúng ta

```java

@Controller
public class HelloController {
    @GetMapping("/hello")
    public String sayHello() {
        return "hello";
    }
}
// Ta không bao giờ gọi `DispatcherServlet`
// Ngược lại Framework gọi ngược lại `HelloController`
```

## IOC

- **IoC**

- IoC có nghĩa là đảo ngược quyền kiểm soát

- Với IoC, quyền kiểm soát việc tạo và quản lý các đối tượng được chuyển từ đối tượng của bạn sang một "container" bên ngoài. Thay vì tự mình tạo ra dependency, đối tượng của bạn sẽ được "cung cấp" các dependency đó.

- Ví dụ: Thay vì lớp Car tự tạo ra lớp Engine, một IoC container sẽ tạo ra Engine và "đưa" nó cho Car.

- **DI**

- DI là một phương pháp triển khai cụ thể của IoC. Đây là cách IoC container "tiêm" (inject) các dependency vào một đối tượng.

- IoC là nguyên tắc hoặc mô hình thiết kế, trong khi DI là công cụ hoặc kỹ thuật để thực hiện nguyên tắc đó. Bạn có thể coi IoC là "tư tưởng" và DI là "hành động". Không có DI thì không thể thực hiện IoC một cách hiệu quả.

- **Lợi ích của IoC và DI**
    - Giảm sự phụ thuộc chặt chẽ (tight coupling): 
    - Tăng khả năng kiểm thử (testability): 
    - Tăng khả năng mở rộng (extensibility):
    - Tăng khả năng tái sử dụng (reusability):


