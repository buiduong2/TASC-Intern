## Khi Parent class và child Class có cùng field thì điều gì xảy ra.

| STT | Loại thành phần | Cha           | Con           | Có hiding?       | Ghi chú ngắn                |
| --- | --------------- | ------------- | ------------- | ---------------- | --------------------------- |
| 1   | Instance Field  | field         | field         | Có               | Không override, chỉ hiding  |
| 2   | Static Field    | static field  | static field  | Có               | Hiding đúng nghĩa           |
| 3   | Final Field     | final field   | final field   | Có               | Hiding như field thường     |
| 4   | Constant        | static final  | static final  | Có               | Hiding                      |
| 5   | Static Method   | static method | static method | Có               | Hiding, không override      |
| 6   | Instance Method | method        | method        | Không (Override) | Không gọi là hiding         |
| 7   | Private Field   | private field | field         | Không            | Không truy cập được         |
| 8   | Local Variable  | field         | local         | Shadowing        | Che khuất trong phạm vi nhỏ |
| 9   | Parameter       | field         | parameter     | Shadowing        | Che khuất tham số           |

| STT | Loại hiding   | Cách truy cập       | Ý nghĩa/Đặc điểm                            | Ví dụ                            |
| --- | ------------- | ------------------- | ------------------------------------------- | -------------------------------- |
| 1   | Field hiding  | Tham chiếu kiểu cha | Truy cập field của cha                      | `((Parent)obj).field`            |
| 2   | Field hiding  | Tham chiếu kiểu con | Truy cập field của con                      | `((Child)obj).field`             |
| 3   | Field hiding  | this                | Field của class hiện tại (con)              | `this.field`                     |
| 4   | Field hiding  | super               | Field của class cha                         | `super.field`                    |
| 5   | Static hiding | Tên class           | Truy cập field hoặc static method của class | `Parent.field`, `Child.field`    |
| 6   | Static hiding | Tham chiếu biến     | Theo kiểu biến tham chiếu                   | `ref.field`                      |
| 7   | Inner class   | Outer.this / this   | Truy cập field ở outer/inner/local class    | `Outer.this.field`, `this.field` |

-   **Override (Ghi đè)**

-   Khi `Phương thức` của lớp con có cùng tên , cùng tha msoso với super class. Thì nó là ghi dè
-   Nếu Gọi qua tham chiếu của lớp cha -> gọi method của isntance

-   Chạy: `runtime` sửa đổi hành vi

-   **Overloading (nạp chồng phương thức)**

-   Khi `nhiều phương thức` cùng tên nhưng `khác tham số` (khác số lượng , kiểu tham số, (có thể khác cả kiểu return))

-   Chạy : `Compile time` sẽ cố gắng thay đổi làm rút gọn các method thực sư jđượcc gọi ở file jar

-   **Hiding (che khuất)**

-   `Field` hoặc `static method` `trùng tên` với super class. Field, method của lớp cha bị che khuất. Không bị override
-   `Không có đa hình`. Khi cần thì gọi field / method qua kiểu tham chiếu nào thì lấy của class đó

-   Chạy : `Compile time` sẽ cố gắng thay đổi làm rút gọn các method thực sư jđượcc gọi ở file jar

-   **Kiểu tham chiếu**

-   Là biến dùng để "Trỏ đến (tham chiếu dến)" một đối tượng trong bộ nhớ (heap)
-   Kiểu của tham chiếu (reference type) `quyết định ta được phép nhìn thấy thành phần nào của Object` khi dùng dấu `.` để truy cập các field hoặc method

-

-   Đặt tên biến = xác định kiểu tham chiếu (còn `new ...` là kiểu object thực tế)

-   `method sẽ bị override`, còn `field` thì sẽ nhìn thấy thông qua kiểu tham chiếu. không quan tâm object thực tế là gì

-   **Các truy cập field bị hiding**

-   _Truy cập thông qua biến tham chiếu_

```java
Parent p = new Parent();
Child c = new Child();
Parent pc = new Child();

System.out.println(p.name);  // "Parent" - field của Parent
System.out.println(c.name);  // "Child"  - field của Child
System.out.println(pc.name);
```

-   _Truy cập thông qua từ khóa `super` và `this`_

-   `thí.name` ->luôn là field của class hiện tại
-   `super.name` -> luôn là field của class cha gần nhất

```java
class Child extends Parent {
    public String name = "Child";

    public void printName() {
        System.out.println(name);        // "Child"
        System.out.println(this.name);   // "Child"
        System.out.println(super.name);  // "Parent"
    }
}
```

-   _Truy cập thông qua ép kiểu (casting)_

```java
Child c = new Child();
System.out.println(c.name);           // "Child"
System.out.println(((Parent)c).name); // "Parent"
```

-   _Truy cập static field bị hiding_

-   Ta sẽ cố gắng ép kiểu rồi gọi instance mà không thể thông qua className.fieldName được

```java
class Parent {
    public static String type = "Parent";
}
class Child extends Parent {
    public static String type = "Child";
}

System.out.println(Parent.type); // "Parent"
System.out.println(Child.type);  // "Child"

Parent p = new Child();
System.out.println(p.type);      // "Parent"
```

-   _Trong Inner Class sử dụng `Outer.this/this`_

```java
class Outer {
    String value = "Outer";

    class Inner {
        String value = "Inner";

        void printValues() {
            String value = "Local";
            System.out.println(value);           // "Local"
            System.out.println(this.value);      // "Inner"
            System.out.println(Outer.this.value);// "Outer"
        }
    }
}
```

-   _Cha có kiểu private (ko thực sự hiding- nhưng vãn tồn tại)_

-

## Làm soa để có Runnable mà sau khi nó chạy xong thì ta lấy dữ liệu

-   **While**

-   Sử dụng vòng lặp while . Kiểm tra bao giờ chúng ta biết được SharedObjet đã bị làm bẩn. Thì chúng ta thoát vòng lặp và có tài nguyên đầy đủ

-   Điều này khiến cho việc CPU không hiệu quả tốn kém tài nguyên

-   **Sử dụng wait() và notify()**

-   Sử dụng phần mở rộng của khóa nội tại trong Synchronize

-   Nhược điểm là khó quản lý, dễ lỗi phức tạp, khó đọc

-   **BlockingQueue**

-   Sử dụng mô hình Producer- Consumer

-   Nhược điểm: chủ yếu dùng cho đòng dữ liệu, không phải thiết kế cho logic

-   **Future**

-   Trả về một Future khi gọi một thread . Và có thể gọi complete khi cần để lấy ra dữ liệu ở Thread khác

-   Một vài giải pháp khác. Cho việc xử lý bất đồng bộ. Cần kết quả từ Thread khác, trong lập trình bất đồng bộ

-   **Observer DP**

-   Sử dụng

-   Nhược điểm: Callback nhiều memory Leak

-   **Callback**

-   Khai báo trước một Funtional Interface để có thể xử lý gọi khi có dữ liệu

-   Nhược điểm khó đọc, Dễ bị callback hell, memory leak
