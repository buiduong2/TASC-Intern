## Static

### Thế nào là static

-   Trong Java. từ khóa `static` được sử dụng để quản lý bộ nhớ tốt hơn và nó có thể truy cập trực tiếp thông qua `class` mà ko cần khởi tạo

-   Từ khóa `static` thuộc về class chứ ko thuộc về instance của class đó

-   Những chỗ có thể áp dụng từ khóa Static

    -   Biến Static (static Variable)
    -   static method
    -   Static block
    -   static class
    -   import static
    
-   **Static varaible**

-   các biến khai báo ở trong một class có thể được khai báo với từ khóa `static`. Và lúc đó nó được gọi là `class Variable`

-   Việc cấp phát bộ nhớ cho biến static chỉ xảy ra một lần khi class được nạp vào bộ nhớ

-   Giá trị mặc định khi khai báo biến non-static và static là giống nhau

-   **Static method**

-   Một method được khai báo với từ khóa `static` thì phương thức đó được gọi là phương thức static

-   Đặc điểm:

    -   Một static method thuộc class chứ ko thuộc instance
    -   Một static method có thể được gọi mà ko cần khởi tạo (íntance) của một class
    -   Static method có thể truy cập vào `static varible` và thay đổi giá trị của nó
    -   Một static method chỉ có thể gọi một static method khác `Không thể gọi một method non-static`
    -   Không thể sử dụng từ khóa `this` hoặc `super`
    -   Không thể ghi đè static method trong java. Bởi vì kĩ thuật ghi đề `override` phương thức được dựa trên gán động khi chương trình đang chạy (runtime) và những phương thức static được gán tĩnh trong thời gian biên dịch. Phương thức tĩnh ko ràng buộc với intance của class nên static method ko thể bị ghi đè

-   **Static Block**

-   Khối static được sử dụng để khởi tạo hoặc thay đổi giá trị của các biên static

-   Nó được thực thi trước method `main` tại thời gian tải lớp

-   Một class có thể có nhiều static block

```java
public class UsingStaticExample {
    private static String subject;
    static {
        System.out.println("Khối Static được gọi")
    }

    static {
        subject = "Khối static (static block)";
    }

    public UsingStaticExample() {
        System.out.println("hàm main() được gọi");
        System.out.println("Subject = " + subject);
    }

    public static void main(String[] args) {
        UsingStaticExample ex1 = new UsingStaticExample();
    }
}
```

-   **Static class**

-   Một class có thể được đặt là một static class chỉ khi nó là một `nestedClass` (tức là nằm trong một lớp khác). Một `nestd static Class` có thể được truy cập không cần một Object của OuterClass bên ngoài

-   **Import static**

-   Java cho phép `import` các thành viên tĩnh (static member) của một class hoặc package vào một class khác bằng cách sử dụng từ khóa import và sau đó sử dụng chúng như là một thành viên của lớp

### Phương thức, thuộc tính khai báo bằng từ khóa static được sử dụng khi nào

-   **Thuộc tính khai báo Static được sử dụng khi**

-   Biến static có thể được sử dụng làm thuộc tính chung, để dùng chung dữ liệu cho tất cả Objects (hoặc instance) của class và điều đó giúp cho chương trình tiết kiệm bộ nhớ hơn

-   tạo ra các hằng số với từ khóa `static final` - Như vậy có thể tiết kiệm bộ nhớ chia sẻ dễ đàng với các class khác
-   Bộ đếm counter

```java
public class MyWebsite {
    public static String WEBSITE = "gpcoder.com";
}
```

-   **Static Method được sử dụng khi**

-   khi một phương thức ko phụ thuộc vào trạng thái của đối tượng, nghĩa là không cần sử dụng bất kì dữ liệu nào của đối tượng, mọi thứ đều được truyền như các tham số (paramter)

-   Các phương thức là một trường hợp được sử dụng nhiều nhất trong Java. Vì chúng có thể được truy cập bằng tên class mà ko cần phải tạo bất kì thể hiện nào. Class java.lang.Math là một ví dụ trong trường hợp sử dụng static method

```java
Math.max(1,2,3) // 3;
```

-   Sử dụng trong các DesignPattern cần truy cập như `SingletonPattern` ,`FactoryPattern`

```java
public class Service {
    private Service instance;

    private Service() {

    }

    // Static method
    public static Service getInstance() {
        if(Service.instance == null) {
            Service.instance = new Service();
        }
        return instance;
    }
}
```

### Làm thế nào để truy cập được tới phương thức, thuộc tính static

-   Để truy cập vào thuộc tính static `TenClass.TenStaticField`
-   Để truy cập vào method Static `TenClass.tenMethod`

## Final

?  ? Nếu được cho ví dụ minh họa.

## Thế nào là final

-   Từ khóa `final` trong Java được sử dụng để hạn chế thao tác của người dùng

-   Các trường hợp sử dụng - `Biến Final`: khi một biến khai báo với từ khóa final, nó chỉ chứa một giá trị duy nhất trong toàn bộ vòng đời của chương trình (hay dễ hiểu hơn là biến `hằng`)

-   `Phương thức final`. Phương thức final: khi một phương thức được khai báo với từ khoá final, các class con kế thừa sẽ không thể ghi đè (override) phương thức này.

-   `Lớp final`: khi từ khoá final sử dụng cho một lớp, lớp này sẽ không thể được kế thừa.
-   `Biến static final trống`: Một biến final mà không được khởi tạo tại thời điểm khai báo được gọi là biến final trống.

### Khai báo 1 biến final khác gì với static, biến khai báo bằng final có thể chỉnh sửa được không

- Mục đích khai báo biến final thì ta chắc chắn rằng trong suốt vòng đời của nó ko được phép gán lại lần nữa

- Còn với biến static: ta sử dụng với mục đích đem cho nó thuộc về sở hữu của class . Mà ko thuộc về instance của Object nưa. Từ đó có thể truy cập dễ dàng thông qua class mà ko cần tạo đối tượng

- Biến khai báo bằng final không chỉnh sửa được
