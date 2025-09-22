## Spring bean, life cycle bean, bean scope. Các annotation sử dụng để khai báo bean trong Spring.

## Spring Bean là gì?

- Bean = đối tượng do IoC Container của Spring tạo,. Nó được tọa bởi các cấu hình metadata hoặc các Annotation
- Là các đối tượng mà sau này sẽ được sử dụng để tiêm vào vị trí mở rộng nào đó trong chương trình

## Các annotation sử dụng để khai báo bean trong Spring

- @Component
    - đánh dấu trên class giúp Spring biết nó là Bean 

- @Configuration
    - Nơi tạo ra Bean là một class. và các method bên trong nó mà được đánh dấu là `@Bean` sẽ là chỉ thị cho các Container rằng các Object return từ method này nên được đăng kí như các Bean

## life cycle bean
- **Chu kì khởi tạo**
- 1. `Instantiate` : tạo instance (gọi constructor)
- 2. `Populate`: tiêm dependencies (constructor, setter, field)
- 3. `Aware callback`
    - BeanNameAware, BeanFactoryAware, ApplicationContextAware
    - là các interface. Nó sẽ có các method mà Spring tại chu kì của Bean nó sẽ gọi. Và truyền vào các tham số tương ứng
    - BeanNameAware
        - Truy cập được vào name của bean hiện tại
    - BeanFactoryAware
        - Lấy ra BeanFactory : để có thể lấy ra Bean khác = code logic
    - ApplicationContextAware: lấy ApplicationContext
        - Một điểm truy cập toàn cục 

- 4. `before init BeanPostProcessor`
    -  Mọi `BeanPostProcessor#postProcessBeforeInitialization(...)` chạy qua Bean (VD người ta cấu hình sao cho nếu Bean thỏa mãn điều kiện thì gắn cho nó một logic)
- 5. `Init callbacks`
    - `@PostConstruct`
    - InitializingBean#afterPropertiesSet()
    - `@Bean(initMethod="...")`

- 6. BeanPostProcessor – after init
    - `postProcessAfterInitialization(...)` (thường là lúc áp proxy cuối cùng).
- 7. Ready
    - Sẵn sàng phuc chạy khi chúng ta sử dụng method như một instance thật

- **Chu kì hủy**

- DestructionAwareBeanPostProcessor#postProcessBeforeDestruction(...) (nếu có)
- @PreDestroy
- DisposableBean#destroy()
- @Bean(destroyMethod="...")
    - Prototype thì ko có pha hủy như này

- **MỘt số callback anang cao**
    - SmartInitializingSingleton: gọi sau khi toàn bộ singleton đã khởi tạo — hữu ích cho code “warm-up”.
    - SmartLifecycle: start/stop theo phase (hữu ích cho dịch vụ nền, consumer Kafka, scheduler…).

## Bean Scope 
    - singleton
        - 1 instance cho toàn container (gần như tất cả chúng ta đều sử dụng)
    - prototype
        - Mỗi lần gọi -sẽ tạo ra một instance mới Không auto-destroy

    - request
        - Mỗi khi một Http request đi vào trong ứng dụng nó sẽ làm việc với một bean khác (khác instance nhưng cùng class)
        - Thuộc về WebApplicationCOntext

    - session 
        - mỗi Một Session sẽ sử dụng các instance khác nhau
        - Thuộc về WebApplicationCOntext

    - application
        - 1 isntance cho một `ServletContext`
    - websocket
        - Mỗi Session của WebSocket sẽ được tạo và sử dụng các instance riêng biệt

- Nếu chúng ta cố gắng tiêm một Bean có vòng đời ngắn hơn vào một Bean có vòng đời rộng hơn: -> Nếu ko có cấu hình đặc biệt thì nó sẽ chỉ được tiêm 1 lần
    - Cần sử dụng PROXY cho Bean 
    - hoặc dùng các Functional Interface ObjectProvider<T> / Provider<T> / ObjectFactory<T>

    - Ta có thể thay bằng acsc Annotation khác như @RequestScope, @SessionScope, @ApplicationScope

## Khai báo & Câu hình Bean

- Được nạp thông qua @ComponentScan
    - @Component: gốc
    - @Service: các bean dạng nghiệp vụ
    - @Repository: bean phục vụ truy xuất dữ liệu
    - @Controller / @RestController: Bean phục vụ nhận dữ liệu từ request vào trong bussiness

- @Configuration @Bean
    - Khai báo Bean theo dạng lập trình

## Cấu hình 

- Sử dụng @Autowired để tiêm vào cá constructor , setter, field
- @Primary trên khai báo tạo Bean, Bean nào được đnáh dấu thì ưu tiên inject bean cho Bean yêu cầu
- @Qualifier("beanName"): Chỉ nhận bean nào có tên trùng tên tương ứng
- @Value("${...}"): Tiêm cấu hình String từ fiel configuration
- ObjectProvider<BeanClass> / Provider<BeanClass>: tiêm theo dạng Lazy, hoặc có hoặc không
- @Lazy: trên class. Thì nó chỉ được tạo khi có yêu cầu/ trên field: chỉ được inject khi nó được gọi
- 

## Bài tập về nhà

- Xem về ORM 
- Project nghiệp vụ A thêm user JPA
- Lấy thong tin chi tiết sản phẩm : JDBCTemplate
- NamedJDBCTemplate
- Join viết store
- 

- Tìm kiếm viết riêng
- 1. là dùng hàm PROCEDRE
- 2. specificicatioon
- 3. native query tìm kiếm có join - projection
- 4. 