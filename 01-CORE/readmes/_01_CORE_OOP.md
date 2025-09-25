## OOP

-   OOP là viết tắt của `Object-Oriented Programming` (Lập trình hướng đối tượng).

-`OOP` là phương pháp thiết kế và tổ chức chương trình máy tính bằng cách sử dụng các thực thể gọi là đối tượng để `mô hình hóa các thực thể` của thế giới thực hoặc các khái niệm trừu tượng.

-   Mỗi đối tượng kết hợp dữ liệu (thường gọi là các thuộc tính hay state) và hành vi (thường gọi là các phương thức hay behavior) vào một đơn vị tự trị.

-   Mục đích chính của OOP là mô hình hóa các thực thể trong thế giới thực thành các đối tượng độc lập, có thể tương tác với nhau để giải quyết bài toán.

-   **Class**

    -   Là một khuôn mẫu (blueprint) hoặc một định nghĩa trừu tượng để tạo ra các đối tượng.
    -   Nó định nghĩa các `thuộc tính `(dữ liệu) và `phương thức` (hành vi) mà các đối tượng thuộc lớp đó sẽ có.

-   **Đối Tượng (Object):**
    -   Là một `thể hiện` (instance) cụ thể được tạo ra từ một Lớp.
    -   Nó có trạng thái (state, giá trị cụ thể của các thuộc tính) và hành vi (các phương thức có thể thực hiện).

## Tính đóng gói (Encapsulation)

-   **Định nghĩa**: Tính Đóng Gói là cơ chế ràng buộc (gói) dữ liệu (thuộc tính) và các mã nguồn thao tác trên dữ liệu đó (phương thức) vào một đơn vị duy nhất – được gọi là Lớp (Class) hoặc Đối Tượng (Object).

    -   Nó hoạt động như một "vỏ bọc bảo vệ" ngăn chặn sự truy cập trực tiếp từ bên ngoài vào các dữ liệu bên trong của đối tượng.

-   **Mục đích:**

    -   ` Gói Gọn (Bundling)`
        -   Tập hợp dữ liệu và hành vi lại với nhau. Điều này giúp tổ chức mã nguồn một cách hợp lý và dễ quản lý, vì mọi thứ liên quan đến một thực thể đều nằm ở một nơi.
    -   `Che Giấu Thông Tin (Information Hiding)`
        -   Ẩn đi các chi tiết triển khai nội bộ của đối tượng (dữ liệu) và chỉ cho phép truy cập thông qua một giao diện công khai (Public Interface) là các phương thức. Đây là mục tiêu quan trọng nhất của Đóng gói.

-   **Cơ chế thực hiện**

    -   `bộ chỉ định truy cập (Access Specifiers):`

-   **Lợi Ích Quan Trọng**

    -   `Kiểm Soát Dữ Liệu:`

        -   Đóng gói cho phép lập trình viên kiểm soát chính xác cách dữ liệu được truy cập và thay đổi. Ví dụ: Bạn có thể viết logic kiểm tra trong phương thức Setter để đảm bảo tuổi (Age) không bao giờ là số âm.

    -   `Tính Toàn Vẹn (Integrity):`
        -   Bảo vệ `trạng thái` (state) của đối tượng khỏi bị thay đổi một cách ngẫu nhiên hoặc không hợp lệ từ bên ngoài.
    -   `Linh Hoạt và Bảo Trì`
        -   Nếu bạn cần thay đổi cách lưu trữ dữ liệu nội bộ (ví dụ: đổi tên thuộc tính), bạn chỉ cần sửa đổi bên trong lớp. Các phần còn lại của chương trình sử dụng các phương thức public (Getter/Setter) sẽ không bị ảnh hưởng, vì giao diện bên ngoài không thay đổi.

## Tính kế thừa (Inheritance)

-   **Định nghĩa**

    -   `Tính Kế Thừa` là cơ chế cho phép một lớp mới (gọi là `lớp con`, lớp dẫn xuất - Child Class/Derived Class) nhận `(thừa hưởng)` các `thuộc tính` (dữ liệu) và `phương thức` (hành vi) từ một lớp đã có (gọi là `lớp cha`, lớp cơ sở - Parent Class/Base Class).
    -   Nó thiết lập một mối quan hệ `"is-a" `(là một) giữa các lớp. Ví dụ: "Xe Đạp là một Phương Tiện Giao Thông", "Chó là một Động Vật".

-   **Mục Tiêu:**

    -   `tái sử dụng mã nguồn.`

-   **Cơ chế**

    -   Thành phần công khai (`public`) và được bảo vệ (`protected`): được `kế thừa`
    -   Thành phần riêng tư (`private`): `cũng được kế thừa` về mặt kỹ thuật, `nhưng không thể truy cập trực tiếp`

-   **Lợi Ích Quan Trọng**

    -   `Tái Sử Dụng Mã`
    -   `Thiết Lập Phân Cấp`
        -   Tạo ra một cấu trúc phân cấp (hierarchy) rõ ràng và logic cho các thực thể, giúp chương trình có tính tổ chức cao.
    -   `Dễ Mở Rộng`
        -   Khi cần thêm một thực thể mới có chung đặc điểm với thực thể đã có, ta chỉ cần tạo một lớp con mới và thừa hưởng.

-   **JAva**

    -   Đơn Kế Thừa (Single Inheritance)

## Tính đa hình (Polymorphism)

-   **Định nghĩa**

    -   `Tính Đa Hình` (nghĩa đen là "đa dạng hình thái" hay "nhiều hình thức") là `khả năng cho phép một giao diện chung` (common interface, SuperClass) được sử dụng để `đại diện cho nhiều hình thức thực thi` (multiple underlying forms) khác nhau.Hay trạng thái khác nhau

    -   Nói cách khác, nó cho phép cùng một `tên phương thức` có thể gọi những hành vi khác nhau trên các đối tượng khác nhau. Điều này làm cho chương trình trở nên linh hoạt và dễ mở rộng hơn rất nhiều.

-   **Đa Hình Tĩnh (Static/Compile-time Polymorphism)**

    -   `Cơ chế`: Thường được thực hiện thông qua Nạp Chồng Phương Thức (`Method Overloading`).
    -   `Giải thích` : Các phương thức có cùng tên nhưng khác nhau về số lượng hoặc kiểu dữ liệu của các tham số đầu vào. Trình biên dịch (compiler) sẽ xác định phương thức nào sẽ được gọi ngay tại thời điểm biên dịch (compile time).

```java
CalculateArea(int side) → Tính diện tích hình vuông.

CalculateArea(int length, int width) → Tính diện tích hình chữ nhật.

```

-   **Đa Hình Động (Dynamic/Runtime Polymorphism)**

    -   `Cơ chế:` Thường được thực hiện thông qua Ghi Đè Phương Thức (`Method Overriding`) và `Kế Thừa`.
    -   `Giải thích` : Lớp con định nghĩa lại (override) một phương thức đã có sẵn trong lớp cha. Khi phương thức này được gọi thông qua `một tham chiếu của lớp cha` `trỏ đến đối tượng của lớp con`, hệ thống sẽ quyết định hành vi cụ thể (của lớp con) sẽ được thực thi tại thời điểm chạy (runtime).

-   **Lợi ích**

    -   `Tính Linh Hoạt`: Cho phép lập trình viên xử lý các đối tượng khác nhau một cách thống nhất thông qua một giao diện chung (tham chiếu lớp cha), đơn giản hóa việc viết mã xử lý.

        -   Khi `overload`: Tính Nhất Quán và Dễ Đọc. Hỗ Trợ Dữ Liệu Đa Dạng

    -   `Khả Năng Mở Rộng`: Dễ dàng thêm các lớp con mới mà không cần thay đổi mã đã viết cho lớp cha hoặc các hàm xử lý chung. Chỉ cần đảm bảo lớp con triển khai (override) phương thức chung đó.

    -   `Tách Biệt Lập Trình Giao Diện/Triển Khai`Tính Đa Hình là chìa khóa để lập trình theo giao diện (Interface Programming), cho phép tách biệt rõ ràng giữa phần định nghĩa (giao diện) và phần thực thi (triển khai), làm tăng tính mô-đun hóa (modularity) của hệ thống.

-   **Thêm**
    -   person và Student cũng là một loại đa hình động
    -   `Hành Vi (Behavior) là Trọng Tâm`: OOP quan tâm đến việc các đối tượng `hành xử` như thế nào. Đa hình là khả năng hành xử khác nhau dù được gọi chung một lệnh.

## Tính trừu tượng

-   **Định nghĩa**:

    -   `Tính Trừu Tượng` là quá trình `ẩn đi các chi tiết triển khai phức tạp` (implementation details) và chỉ `hiển thị những tính năng cốt lõi` (essential features) hoặc `giao diện cần thiết` (interface) ra bên ngoài cho người dùng hoặc các phần khác của hệ thống.

    -   Mục tiêu của nó là quản lý sự phức tạp bằng cách tập trung vào `"Cái gì mà đối tượng làm?"` (What it does?) thay vì `"Làm như thế nào?" (How it does it?).`

-   **Khía cạnh**

    -   `Trừu Tượng Dữ Liệu (Data Abstraction)	`
        -   Ẩn đi cách thức dữ liệu được lưu trữ và thao tác bên trong.
        -   Cơ chế : Chủ yếu thông qua Tính Đóng Gói (sử dụng các thuộc tính private và phương thức public để truy cập).
    -   `Trừu Tượng Hành Vi (Process/Control Abstraction)`
        -   Ẩn đi các bước thực thi chi tiết của một chức năng.
        -   Chủ yếu thông qua `Lớp Trừu Tượng (Abstract Class)` và `Giao Diện (Interface).`

-   **Cơ chế triển khai**

    -   Lớp Trừu Tượng (Abstract Class)
    -   Giao Diện (Interface)

-   **Lợi ích**

    -   `Đơn Giản Hóa Hệ Thống` Giúp người lập trình tập trung vào thiết kế cấp cao (high-level design) mà không bị phân tâm bởi các chi tiết cấp thấp (low-level details).

    -   `Tăng Tính Bảo Mật`: Bằng cách ẩn dữ liệu và logic nhạy cảm khỏi sự truy cập không cần thiết (kết hợp với Đóng Gói).

    -   `Dễ Bảo Trì và Mở Rộng`: Khi các chi tiết triển khai thay đổi, miễn là giao diện (interface) không đổi, các phần còn lại của chương trình vẫn hoạt động bình thường (đây là nguyên tắc cốt lõi cho Tính Đa Hình).

### - Access modifier trong java có những loại nào ? Nêu đặc điểm của từng loại

-   `Access Modifier` là các từ khóa được sử dụng để thiết lập `mức độ khả năng truy cập` (visibility) của các thành viên (member) trong một lớp (như thuộc tính, phương thức, constructor) và bản thân lớp đó.

| Access Modifier | Class | Package | Subclass | World (Other Packages) |
| --------------- | ----- | ------- | -------- | ---------------------- |
| **public**      | ✔     | ✔       | ✔        | ✔                      |
| **protected**   | ✔     | ✔       | ✔        | ✘                      |
| **default**     | ✔     | ✔       | ✘        | ✘                      |
| **private**     | ✔     | ✘       | ✘        | ✘                      |

### - Phân biệt class và instance

-   **Class**

    -   Là một khuôn mẫu (blueprint) hoặc một định nghĩa trừu tượng để tạo ra các đối tượng.
    -   Nó định nghĩa các `thuộc tính `(dữ liệu) và `phương thức` (hành vi) mà các đối tượng thuộc lớp đó sẽ có.

-   **Đối Tượng (Object):**
    -   Là một `thể hiện` (instance) cụ thể được tạo ra từ một Lớp.
    -   Nó có trạng thái (state, giá trị cụ thể của các thuộc tính) và hành vi (các phương thức có thể thực hiện).

| Đặc điểm  | Class (Lớp)                              | Instance (Đối tượng)                   |
| --------- | ---------------------------------------- | -------------------------------------- |
| Khái niệm | Bản thiết kế, khuôn mẫu                  | Thực thể cụ thể được tạo từ Class      |
| Tạo ra    | Được định nghĩa bằng từ khóa `class`     | Được tạo bằng từ khóa `new`            |
| Bộ nhớ    | Không chiếm dữ liệu cho đối tượng cụ thể | Mỗi instance chiếm vùng nhớ riêng      |
| Sử dụng   | Khai báo thuộc tính & phương thức chung  | Có giá trị riêng cho từng đối tượng    |
| Số lượng  | Định nghĩa một lần trong chương trình    | Có thể tạo nhiều instance từ một class |

## - Phân biệt Abstract và Interface , Nêu trường hợp sử dụng cụ thể. Nếu 2 interface hoặc 1 abstract và 1 interface có 1 function cùng tên, có thể cùng hoặc khác kiểu trả về cùng được kế thừa bởi một class, chuyện gì sẽ xảy ra?

### Abstract

-   Abstract Class là một lớp đặc biệt được sử dụng để định nghĩa `một khuôn mẫu chung` cho một nhóm các lớp con có quan hệ gần gũi (mối quan hệ `"is-a"`).

-   **Đặc điểm cốt lõi**
-   `Tính Khởi Tạo: Không thể` tạo đối tượng (Instance) trực tiếp từ một Abstract Class. Nó chỉ có vai trò làm lớp cha để kế thừa.
-   `Thành Phần`: Có thể chứa cả hai loại thành viên:

    -   Phương thức trừu tượng (abstract method):Chỉ có khai báo (chữ ký), không có phần thân (logic). Bắt buộc lớp con phải triển khai.

    -   Phương thức cụ thể (concrete method):Có cả khai báo và phần thân (logic triển khai).

    -   Có thể có thuộc tính (biến), hàm khởi tạo (constructor).

-   `kế thừa`: Một lớp con chỉ có thể kế thừa từ một Abstract Class duy nhất (theo nguyên tắc Đơn Kế Thừa).

-   `Mục đích`: Thích hợp khi bạn muốn cung cấp một `triển khai cơ sở` (base implementation) chung cho các phương thức, nhưng vẫn để lại một số phương thức cốt lõi phải được `tùy chỉnh bởi lớp con.`

### Interface

-   Interface định nghĩa một hợp đồng (contract) về các dịch vụ hoặc hành vi mà một lớp cam kết sẽ cung cấp. Nó hoàn toàn `tách biệt phần "giao diện" khỏi phần "triển khai".`

-   **Đặc điểm**

    -   `Tính Khởi Tạo: Không thể` tạo đối tượng trực tiếp từ một Interface.

    -   `Thành Phần`

        -   Chỉ chứa các phương thức trừu tượng công khai (`public abstract methods`).
        -   Chỉ chứa các hằng số (`public static final` constants).

    -   `Kế Thừa/Triển Khai`Một lớp có thể triển khai (implement) `nhiều Interface`. Đây là cách giải quyết vấn đề Đa Kế Thừa.

-   **Mục đích**

    -   Thích hợp khi bạn muốn một `tập hợp các lớp` không liên quan (`không có quan hệ kế thừa trực tiếp`) `cam kết thực hiện` một `hành vi chung` nào đó.

## Phân biệt Abstract và Interface

| Đặc Điểm                 | Abstract Class                                                     | Interface                                                                 |
| ------------------------ | ------------------------------------------------------------------ | ------------------------------------------------------------------------- |
| `Mối Quan Hệ`            | Quan hệ chặt chẽ ("`is-a`").                                       | Quan hệ hành vi lỏng lẻo (có khả năng `"can-do"`).                        |
| `Triển Khai`             | Có thể chứa cả phương thức trừu tượng và cụ thể (có logic), state. | Theo bản chất, chủ yếu chứa các phương thức trừu tượng (chỉ có khai báo). |
| `Thuộc Tính (Variables)` | Có thể có các biến bình thường (private, protected).               | Chỉ có thể có hằng số tĩnh (public static final).                         |
| Số Lượng                 | Một lớp chỉ có thể `kế thừa một` Abstract Class.                   | Một lớp có thể triển khai nhiều Interface.                                |
| Hàm Khởi Tạo             | Có thể có `Constructor` để khởi tạo các thành viên cụ thể.         | `Không thể có` Constructor.                                               |

## Nêu trường hợp sử dụng cụ thể của Interface và Abstract

-   Khi nào nên dùng **abtract class**

    -   có hành vi chung cho nhiều lớp con
    -   Có quan hệ "is-a" rõ ràng : Cho phép việc tái sử dụng code mà ko cần phải viết lại ở từng class
    -   Khi cần mở rộng mà ko phá vỡ code cũ
        -   Nếu chúng ta thêm một abstract method vào abstract class , các subclass có thể kế thừa mà ko bắt buộc override nó

-   Khi nào dùng **interface**

    -   Định nghĩa hợp đồng

        -   Dùng interface khi muốn đảm bảo nhiều class `tuân thủ cùng một hành vi` bất kể chúng nằm ở đâu trong hệ thống kế thừa (tức là chúng ko nhất thiết phải kế có quan hệ sâu xa ở mức độ kế thừa mà chúng đều chỉ tuân thủ một interface đảm bảo luôn có hành vi chỉ định)

-   Hỗ trợ đa kế thừa hành vi

    -   Java không cho phép đa kế thừa class, Nhưng cho phép `implements nhiều interface`

-   Dùng để thiết kế hệ thống linh hoạt

    -   interface giúp thay đổi hoặc mở rộng mà không ảnh hưởng code hiện có
    -   VD như trong SpringFramework
        -   `Jpa`, `CrudRepository` là interface , và ta có thể dễ dàng thay đổi các implement khác `Hỉbernate`, `MongoDB` mà ko làm thay đổi code nghiệp vụ

## Nếu 2 interface hoặc 1 abstract và 1 interface có 1 function cùng tên, có thể cùng hoặc khác kiểu trả về cùng được kế thừa bởi một class, chuyện gì sẽ xảy ra?

-   KO cần biết là interface hay Abstract class:

-   khi 2 method abtract cùng tên cùng tham số cùng kiểu trả về. Thì subClass extends và đồng thời implemenets cẩ 2 abstract interface đó thì chỉ cần `implement 1 lần`

-   Khi cùng tên. cùng tham số , khác return -> lỗi compiler do ko phân biệt đâu mới là returnType cần thiết

-   Các kiểu khác còn lại Java coi đó là một kiểu của `overloading` và tiến hành implement các method như thường

## Thế nào là Overriding và Overloading

-   Overloading: nạp chồng phương thức

    -   Định nghĩa: Cùng một phương thức nhưng `khác nhau về tham số` (số lượng hoặc kiểu dữ liệu)
    -   Thời điểm kiểm tra :
    -   ReturnType: có thể khác, nhưng không đủ để phân biệt (chỉ dùng để phân biệt khi tham số cũng khác)
    -   Tính chất: Tăng `tính linh hoạt` cho phép cùng tên phương thức nhưng xử lý dữ liệu khác nhau

```java
class Calculator {
    int add(int a, int b) {
        return a + b;
    }
    double add(double a, double b) {
        return a + b;
    }
    int add(int a, int b, int c) {
        return a + b + c;
    }
}
```

-   Overriding (Ghi đè phương thức)

    -   Định nghĩa: Class con viết lại phương thức của class cha `cùng tên` , `cùng tham số` , `cùng kiểu trả về`

    -   Thời điểm kiểm tra: Xảy ra ở `runtime`

    -   Phạm vi: luôn giữa class cha và class con

    -   Access Modifer

        -   Có thể mở rộng. Nhưng không được thu hẹp

    -   ReturnType: phải giống hoặc covariant (returnType của class con phải là một subClass của Class ReturnType của class cha)
    -   Tính chất : dùng cho tính đa hình

```java
class Animal {
    void sound() {
        System.out.println("Some sound");
    }
}

class Dog extends Animal {
    @Override
    void sound() {
        System.out.println("Woof Woof");
    }
}
```

## Một function có access modifier là private or static có thể overriding được không?

-   **private**
-   `Không thể`
-   VÌ `private` method chỉ có thể truy cập bên trong class khai báo nó. Class con `không thể thây ` method đó. nên ko thể `override`
-   Nếu ta viết trong class con một method có cùng tên, cùng tham số, nó chỉ là một method `mới hoàn toàn` (không phải override)

-   **static**

-   `Không thể override`. Nhưng có thể hiding (giấu)

-   Khi một class con khai báo một static method cùng tên. Cùng tham số với class cha thì class con `không override` mà chỉ che khuất `hide` method static của class cha

-   Quyết định gọi method nào phụ thuộc vào `kiểu tham chiếu` không phải Object thực sự

```java
class Parent {
    static void display() {
        System.out.println("Parent static display()");
    }
}

class Child extends Parent {
    static void display() { // hiding, không phải override
        System.out.println("Child static display()");
    }
}

public class Test {
    public static void main(String[] args) {
        Parent p = new Child();
        p.display();  // Output: Parent static display()
    }
}
```

## - Một phương thức final có thể kế thừa được không ?

-   `final` method là phương thức không thể bị `override` bởi lớp con

-   Nhưng vẫn có thể kế thừa (`inherit`) để dùng lại y nguyên từ class cha.

## - Phân biệt hai từ khóa This và Super

-   **this**

-   Ý nghĩa: đại diện cho `đối tượng hiện tại`
-   `this()` -> Gọi constructor của lớp hiện tại
-   `this.methodName()` -> gọi method trong `class hiện tại` (có override thì dùng bản override)
-   `this.fieldName()` -> Tham chiếu đến biến instance hiện tại ( dùng khi trùng với tên biến local/paramter)
-   Truy cập vào thành phần trong class đó
-   Phải là lệnh đầu tiên nếu gọi `this()`

-   **super**
-   Ý nghĩa: Đại diện cho `đối tượng class cha` (gần nhất)
-   Gọi contructor của lớp cha gần nhất
-   `super.methodName()` -> gọi method của `class cha` (kể cả khi đã bị override)
-   `super.fieldName()` tham chiếu đến íntance của class cha (nếu bị shaddowed - có tên field giống nhau ở cả class cha và con)
-   Truy cập đến thành phần của class cha
-   Phải là lệnh đầu tiên nếu gọi `super()`

## Shadowing (Che Khuất) là gì?

-   `Shadowing (Che Khuất)` là hiện tượng xảy ra khi bạn khai báo một thành viên (`biến hoặc phương thức tĩnh`) trong `lớp con` với `cùng tên` với một thành viên trong `lớp cha` (Base Class).

-   Khi đó, `thành viên của lớp con sẽ che khuất` (làm mờ) `thành viên của lớp cha` khi truy cập `thông qua một đối tượng (instance) của lớp con`.

| Đặc Điểm                 | Shadowing (Che Khuất)                                                                                               | Overriding (Ghi Đè)                                                                                                 |
| :----------------------- | :------------------------------------------------------------------------------------------------------------------ | :------------------------------------------------------------------------------------------------------------------ |
| **Mục đích**             | Tạo ra một thành viên (thuộc tính/phương thức) mới và độc lập trong lớp con, ẩn đi thành viên cùng tên của lớp cha. | Thay đổi hoặc tùy chỉnh hành vi của phương thức đã có từ lớp cha, áp dụng cho lớp con.                              |
| **Áp dụng cho**          | **Thuộc tính** (Fields/Variables) và **Phương thức Tĩnh** (`static methods`).                                       | **Phương thức** (Instance Methods), không áp dụng cho thuộc tính.                                                   |
| **Tính Đa Hình**         | **KHÔNG** thể hiện Đa Hình Động.                                                                                    | **CÓ** thể hiện Đa Hình Động (Dynamic Polymorphism).                                                                |
| **Thời điểm Giải Quyết** | **Thời điểm Biên dịch (Compile-time):** Quyết định dựa trên **kiểu tham chiếu** (kiểu khai báo của biến).           | **Thời điểm Chạy (Runtime):** Quyết định dựa trên **kiểu thực tế** của đối tượng.                                   |
| **Chữ ký (Signature)**   | Tên giống nhau. Chữ ký (tham số) **có thể giống hoặc khác** (đối với phương thức).                                  | Tên, kiểu trả về và chữ ký (tham số) **PHẢI giống nhau** (hoặc tương thích).                                        |
| **Từ khóa Bắt buộc**     | Thường là ngầm định (Java), hoặc dùng từ khóa **`new`** (C#) để báo hiệu việc ẩn.                                   | Thường là **`@Override`** (Java) hoặc **`override`** (C#) và phải có từ khóa **`virtual`** (C#) ở lớp cha.          |
| **Truy cập Lớp Cha**     | Có thể truy cập thành viên bị che khuất bằng cách sử dụng từ khóa **`super`** (Java) hoặc ép kiểu rõ ràng (C#).     | Có thể gọi phương thức của lớp cha từ bên trong phương thức ghi đè bằng từ khóa **`super`** (hoặc `base` trong C#). |
