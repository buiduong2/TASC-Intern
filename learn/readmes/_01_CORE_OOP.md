## OOP

## - Nêu ra các tính chất quan trọng của hướng đối tượng

-   **Tính đóng gói (Encapsulation)**

-   Định nghĩa: là cơ chế che giấu thông tin bên trong đối tượng, chỉ cung cấp ra bên ngoài những gì cần thiết thông qua các phương thức getter và setter

-   Mục đích:

    -   Bảo vệ dữ liệu tránh bị thay đổi trực tiếp từ bên ngoài - Khó debug
    -   Tăng tính bảo mật , dễ kiểm soát dữ liệu

-   **Tính kế thừa (Inheritance)**

-   Một lớp có thể `kế thừa` các thuộc tính và phương thức từ lớp khác

-   Mục đích:
    -   Tai sử dụng mã nguồn
    -   tạo ra mối quan hệ is-a (là một của ai đó)

```java
class Animal {
    public void eat() {
        System.out.println("Animal is eating...");
    }
}

class Dog extends Animal {
    public void bark() {
        System.out.println("Dog is barking...");
    }
}

public class Test {
    public static void main(String[] args) {
        Dog d = new Dog();
        d.eat();  // kế thừa từ Animal
        d.bark(); // phương thức riêng
    }
}
```

-   **Tính đa hình (Polymorphism)**

-   Định nghĩa: Một đối tượng có thể thể hiện nhiều `hình thái khác nhau`

-   Có 2 dạng:

    -   `Compile-time Polymorphism (Overloading):`; nhiều phương thức cùng tên nhưng khác tham số

    -   `Runtime Polymorphism (Overriding)`: Lớp con ghi đè phương thức lớp cha.

```java
// Overloading
class MathUtils {
    int add(int a, int b) { return a + b; }
    double add(double a, double b) { return a + b; }
}

// Overriding
class Animal {
    void sound() { System.out.println("Animal sound"); }
}

class Cat extends Animal {
    @Override
    void sound() { System.out.println("Meow"); }
}
```

-   **Tính trừu tượng**

-   Định nghĩa: Chỉ thể hiện những đặc điểm cần thiết, ẩn đi chi tiết triển khai.
-   Thực hiện bằng
    -   Abstract class (lớp trừu tượng).
    -   Interface
-   Mục đích
    -   Giúp tập trung vào "cái gì làm" thay vì "làm thế nào"
    -   Giảm sự phụ thuộc vào giữa các thành phần

```java
abstract class Animal {
    abstract void sound(); // phương thức trừu tượng
}

class Dog extends Animal {
    void sound() { System.out.println("Woof"); }
}

class Cat extends Animal {
    void sound() { System.out.println("Meow"); }
}
```

### - Access modifier trong java có những loại nào ? Nêu đặc điểm của từng loại

-   Trong Java, `Access MOdifer` là các từ khóa dùng để xác định pham vị truy cập (visibility) của biến, phương thức lớp. Có 4 mức độ truy cập chính

| Access Modifier | Class | Package | Subclass | World (Other Packages) |
| --------------- | ----- | ------- | -------- | ---------------------- |
| **public**      | ✔     | ✔       | ✔        | ✔                      |
| **protected**   | ✔     | ✔       | ✔        | ✘                      |
| **default**     | ✔     | ✔       | ✘        | ✘                      |
| **private**     | ✔     | ✘       | ✘        | ✘                      |

### - Phân biệt class và instance

-   **Class là gì?**

-   Class là bản thiết kế (blueprint / template) để tạo ra các đối tượng (instance).
-   định nghĩa thuộc tính (fields) và hành vi (methods) nhưng không phải là đối tượng cụ thể.

-   **Instance là gì?**
-   Instance là một đối tượng cụ thể được tạo ra từ một class bằng từ khóa new
-   Mỗi instance có dữ liệu riêng biệt, mặc dù dựa trên cùng một bản thiết kế (class).

```java
// Đây là class
class Car {
    String brand;
    int speed;

    void drive() {
        System.out.println(brand + " is driving at " + speed + " km/h");
    }
}

public class Main {
    public static void main(String[] args) {
        // Tạo 2 instance từ class Car
        Car car1 = new Car();
        car1.brand = "Toyota";
        car1.speed = 100;

        Car car2 = new Car();
        car2.brand = "Honda";
        car2.speed = 120;

        // Gọi phương thức
        car1.drive(); // Toyota is driving at 100 km/h
        car2.drive(); // Honda is driving at 120 km/h
    }
}
```

| Đặc điểm  | Class (Lớp)                              | Instance (Đối tượng)                   |
| --------- | ---------------------------------------- | -------------------------------------- |
| Khái niệm | Bản thiết kế, khuôn mẫu                  | Thực thể cụ thể được tạo từ Class      |
| Tạo ra    | Được định nghĩa bằng từ khóa `class`     | Được tạo bằng từ khóa `new`            |
| Bộ nhớ    | Không chiếm dữ liệu cho đối tượng cụ thể | Mỗi instance chiếm vùng nhớ riêng      |
| Sử dụng   | Khai báo thuộc tính & phương thức chung  | Có giá trị riêng cho từng đối tượng    |
| Số lượng  | Định nghĩa một lần trong chương trình    | Có thể tạo nhiều instance từ một class |

## - Phân biệt Abstract và Interface , Nêu trường hợp sử dụng cụ thể. Nếu 2 interface hoặc 1 abstract và 1 interface có 1 function cùng tên, có thể cùng hoặc khác kiểu trả về cùng được kế thừa bởi một class, chuyện gì sẽ xảy ra?

-   **Phân biệt Abstract và Interface**

| Đặc điểm               | Abstract Class                                                                                                               | Interface                                                                                                                                |
| ---------------------- | ---------------------------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------- |
| Từ khóa khai báo       | `abstract class`                                                                                                             | `interface`                                                                                                                              |
| Kế thừa                | Một class chỉ kế thừa **một abstract class**                                                                                 | Một class có thể **implements nhiều interface**                                                                                          |
| Thành phần có thể chứa | - Abstract methods (không code) <br> - Concrete methods (có code) <br> - Thuộc tính (biến instance, biến static, biến final) | - Chỉ chứa **hằng số (public static final)** <br> - Abstract methods (mặc định) <br> - Từ Java 8: có `default` methods, `static` methods |
| Modifier mặc định      | Có thể dùng `private`, `protected`, `public`                                                                                 | Tất cả phương thức mặc định là `public abstract`                                                                                         |
| Mục đích sử dụng       | Dùng khi muốn chia sẻ code chung và bắt buộc con triển khai phần còn lại                                                     | Dùng để định nghĩa **hợp đồng (contract)** mà class phải tuân thủ                                                                        |
| Khi nào dùng           | Khi có **mối quan hệ “is-a”** rõ ràng, cần chia sẻ code                                                                      | Khi cần nhiều class khác nhau tuân theo cùng 1 chuẩn hành vi                                                                             |
| Ví dụ                  | `abstract class Animal { abstract void sound(); }`                                                                           | `interface Flyable { void fly(); }`                                                                                                      |

-   **Nêu trường hợp sử dụng cụ thể**

-   Khi nào nên dùng abtract class

    -   có hành vi chung cho nhiều lớp con
    -   Có quan hệ "is-a" rõ ràng : Cho phép việc tái sử dụng code mà ko cần phải viết lại ở từng class
    -   Khi cần mở rộng mà ko phá vỡ code cũ
        -   Nếu chúng ta thêm một abstract method vào abstract class , các subclass có thể kế thừa mà ko bắt buộc override nó

-   Khi nào dùng interface

    -   Định nghĩa hợp đồng

        -   Dùng interface khi muốn đảm bảo nhiều class `tuân thủ cùng một hành vi` bất kể chúng nằm ở đâu trong hệ thống kế thừa (tức là chúng ko nhất thiết phải kế có quan hệ sâu xa ở mức độ kế thừa mà chúng đều chỉ tuân thủ một interface đảm bảo luôn có hành vi chỉ định)

-   Hỗ trợ đa kế thừa hành vi

    -   Java không cho phép đa kế thừa class, Nhưng cho phép `implements nhiều interface`

-   Dùng để thiết kế hệ thống linh hoạt

    -   interface giúp thay đổi hoặc mở rộng mà không ảnh hưởng code hiện có
    -   VD như trong SpringFramework
        -   `Jpa`, `CrudRepository` là interface , và ta có thể dễ dàng thay đổi các implement khác `Hỉbernate`, `MongoDB` mà ko làm thay đổi code nghiệp vụ

-   **Nếu 2 interface hoặc 1 abstract và 1 interface có 1 function cùng tên, có thể cùng hoặc khác kiểu trả về cùng được kế thừa bởi một class, chuyện gì sẽ xảy ra?**

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
