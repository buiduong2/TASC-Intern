## Factory Method

-   Tìm hiểu về mục đích, cách thức triển khai, ưu điểm của

-   **Tóm tắt**

-   _Định nghĩa_
    - Khác với Simple Factory (nhược điểm là ko kế thừa được)

    -   là một DP thuộc nhóm `Creational DP` mà nó cung cấp một interface cho việc tạo Object trong một Superclass, Nhưng cho phép các subclass thay đổi kiểu của Object sẽ được tạo

-   _Mục đích_

    -   `tách rời việc lựa chọn sản phẩm` ra khỏi phần logic chính - Di chuyển các cuộc gọi `new Contructor` ra một vị trí duy nhất để quản lý
    -   Giảm dàng buộc giữa các thành phần trong code với triển khai cụ thể của một Interface - Vì ta ko biết chính xác sẽ có bao nhiêu kiểu dữ liệu Object có thể có sau này
    -   Không thể thay đổi kiểu triển khai của một implementation trong runtime ( Code client ko thấy sự thay đổi giữa các triển khai - tất cả đều cham chiếu đến một Abs hoặc interface - Và biết rằng chắc chắn có các hành vi mà Client code mong đợi . )
    -   Đôi khi tạo ra một cách để người dùng code mở rộng Các component bên trong
    -   Có thể quản lý các sản phẩm được tạo bởi Factory (vì nó được tạo tập trung 1 nơi)

-   _Cách triển khai_

    -   Cho tất cả các `Product` (của factory) cùng triển khai một Interface. Mà Interface này có các method mà nó có nghĩa cho các Product
    -   Tạo ra một Empty Factory Method bên trong `Creator` class.. Return Type của method này sẽ là `Product`
    -   Bên trong code của `Creator` (các extends đã override lại `factory method`) và tìm tất cả các tham chiếu đến các Constructor có thể có của Product
    -   Tạo ra tất cả các kiểu dữ liệu của `Concrete Product` và nó sẽ được liệt kê ở trong Factory Method.
    -   Ghi đè factory Method trong các `subClass (of Creator)` của nó

-   _Khi nào sử dụng_

    -   Không biết có chính xác bao nhiêu kiểu và các object phụ thuộc vòa code mà chúng ta sẽ làm việc
    -   Sử dụng Factory Method chúng ta mong muốn cung cấp cho người dùng một thư viện. hoặc framework một cách để mở rộng với các Component bên trong
    -   Muốn tiếp kiệm tài nguyên bằng cách tái sử dụng - tránh việc tái sử dụng vì phải khởi tạo cấp phát tài nguyên tốn công

-   _Ưu nhược điểm_

    -   Ưu điểm

        -   Bạn né tránh được ràng buộc giữa Người khởi tạo (Creator - client sử dụng implementation) và các sản phẩm cụ thể (Concrete Product)
        -   Áp dụng được định lý Single Responsibility. Chúng ta có thể di chuyển việc khởi tạo Product Code vòa một nơi duy nhất trong ứng dụng
        -   Định lý đóng mở - Chúng ta có thể thêm các kiểu dữ liệu mới của Product vào trong chương trình mà ko phá vỡ các Code đã tồn tại sẵn

    -   Nhược điểm
        -   Tăng số lượng Class
        -   Phức tạp hóa cấu trúc code. So với việc cứ gọi thẳng thì ta phải thông qua việc tạo một method khác
        -   Khó kiểm soát khi thay đổi `Product` (VD các subclass có các logic khác để khởi tạo. Một constructor khác đi một chút - ta phải nghĩ cách khác)

-   **Factory Method là gì**

```
-   Factory Method là một Design Pattern thuộc nhóm khởi tạo.
-   Nó định nghĩa một interface cho việc khởi tạo một Object . Nhưng nó cho phép các subclass quyết định class nào được khởi tạo . FactoryMethod cho phép một class trì hoãn việc khởi tạo của nó cho các Subclasses
```

-   Factory method design pattern . Hay gọi ngắn là `Factory pattern` là một Pattern thuộc nhóm `Creational Design Pattern`. Nhiệm vụ của Factory Pattern là quản lý và trả về đói tượng theo yêu cầu, giúp cho việc khởi tạo đối tượng một cách linh hoạt hơn.

-   `Factory Pattern` đúng nghĩa là một `nhà máy` và nhà máy này sẽ `sản xuất` các đối tượng theo yêu cầu của chúng ta.

-   Trong Factory Pattern, Chúng ta tạo đối tượng mà không để tiết lộ logic tạo đối tượng ở phía người dùng (bên ngoài class hiện tại) và tham chiếu đến đối tượng mới tạo bằng cách sử dụng một interface chung (tất cả các triển kiểu dữ liệu trả về đều implement một interface duy nhất)

-   Factory Pattern được sử dụng khi có một class cha (super-class) với nhiều class con (sub-class), dựa trên đầu vào phải trả về những class đó (đại loại có một input vào trong một method sử dụng switch case sẽ trả về một cách triển khai trong nhiều cách triển khai)

-   **Cài đặt Factory Pattern như thế nào -- Simple Factory**

-   Một factory Pattern bao gồm các thành phần cơ bản sau

    -   `SuperClass` một supper class trong Factory Patternc ó thể là một `interface` hoặc `abstract class` hay một class thông thường
    -   `Sub Classes` các sub class sẽ triển khai các phương thức của class cha `superClass` theo nghiệp vụ của riêng nó
    -   `Factory Class` một class chịu trách nhiệm khởi tạo tất cả các đối tượng Subclass dựa theo tham số đầu vào. Class này `có thể là SINgleton` hoặc là một `public static method` cho việc truy xuất và khởi tạo đối tượng. Factory class sử dụng `if-else` hoặc `switch-case` để xác định class con đầu ra.

-   Ví dụ: Tất cả hệ thống ngân hàng có cung cấp API để truy cập đến hệ thống của họ . Đội được giao nhiệm vụ thiết kế một API để client có thể sử dụng dịch vụ của một ngân hàng bất kì. Hiện tại phía Client chỉ cần sử dụng dịch vụ của 2 ngan hàng là Vietcombank và TPBank. Tuy nhiên để có thể dễ mở rộng sau này. Và phía client mong muốn không cần phải thay đổi code của họ khi cần sử dụng thêm dịch của ngân hàng khác. Với yêu cầu như vậy chúng ta có thể sử dụng một pattern phù hợp là Factory Method

```
// bank
```

-   Như chúng ta có thể thấy, phía client chỉ cần gọi duy nhất một phuong thức `BankFactory.getBank()` là có thể sử dụng được dịch vụ của một ngân hàng bất kì

-   Khi hệ thống muốn cung cấp thêm dịch của ngân hàng khác, chẳng hạn VietCombank, thì cần tạo thêm một class mới implement từ interface Bank, và thêm vào logic khởi tạo Bank trong Factory là xong. Nó không ảnh ảnh hưởng code ở module kahsc

-   **Sử dụng FactoryPattern khi nào**

-   Factory Pattern được sử dụng khi:

    -   Chúng ta có một superClass với nhiều class con và dựa trên đầu vào, chúng ta cần trả về một class con. Mô hình này giúp chúng ta đưa trách nhiệm khởi tạo một lớp từ nghĩa người dùng (client) sang lớp Factory
    -   Chúng ta ko biết sau này sẽ có những lớn con nào nữa. Khi cần mở rộng, hẫy tạo ra subclass và implement thêm vào Factory method cho việc khởi tạo sub Class này

-   **Lợi ích của Factory Pattern là gì**

-   Giúp giảm thiểu sự phụ thuộc giữa các module (loose coupling): cung cấp 1 hướng tiếp cận với interface thay vì các implement. GIúp chương trình độc lập với những concrete class , code của class không bị ảnh hưởng khi thay đổi logic ở phía factory class hay sub class

-   Mở rộng code dễ dàng hơn: khi cần mở rộng chỉ cần tạo ra sub-class và implement thêm vào factory method
-   Khởi tạo các Objects mà che giấu đi logic của việc khởi tọa (có thể yêu cầu nhiều thứ config phức tạp). Người dùng không biết logic thực sự được khởi tạo bên dưới phương thức factory
-   Dễ quản lý vòng đời của các Object được tạo bởi Factory Pattern (họ có thể giữ tham chiếu đến các object được khởi tạo chẳng hạn)
-   Thống nhất Namein convention: giúp cho các developer có thể hiểu về cấu trúc source code
