# Java Core Basic

## 1. Primitive and Object data type

### Phân biệt kiểu dữ liệu nguyên thủy và kiểu dữ liệu object.

-   **Kiểu giá trị nguyên thủy**

-   Định nghĩa: Là các kiểu cơ bản. `Không phải là đối tượng`, lưu `giá tị trực tiếp`
-   Các kiểu `byte`, `short` , `int` , `long`, `float`, `double` `chảr`, `boolean `

-   Đặc điểm

    -   Lưu giá trị trực tiếp trong bộ nhớ `stack`
    -   Kích thước cố định (VD `int` = 4 bytes)
    -   Nhanh hơn Object vì không cần tạo đối tượng
    -   Không có phương thức đi kèm, chỉ chứa giá trị
    -   Có giá trị mặc định khi là Field:
        -   `int` , `byte` , `short` , `long` -> giá trji là `0`
        -   `float` , `double` -> giá trị là `0.0`
        -   `char` -> giá tị là `/u0000`
        -   `boolean` -> `false`

-   **Kiểu dữ liệu Object (Reference types)**
-   Định nghĩa: là các kiểu `lưu tham chiếu đến đối tượng`. Giá trị thật nằm trên `heap` . Biến chỉ lưu giữ `địa chỉ`
-   Bao gồm

    -   Lớp sẵn có `String` , `Integer` , `Double` ... (các loại Wrapper)
    -   `String `
    -   Lớp do người dùng tự định nghĩa `Student` `Car`
    -   Array `int[], string[]` cũng là Object
    -   Một số metadata `Class Field Method`
    -   Thường được khởi tạo thông qua từ khóa new

-   Đặc điểm :
    -   Lưu `tham chiếu đến đối tượng` trên heap
    -   Có thể là null nếu chưa khởi tạo
    -   Có `phương thức` thao tác linh hoạt hơn
    -   Tốn bộ nhớ hơn và chậm hơn primitive

### Có thể chuyển đổi giữa hai kiểu dữ liệu này không ?

-   Có , Trong java ta có thể chuyển đổi giữa kiểu primtive và Object (wrapper class) nhờ cơ chế `autoboxing` và `unboxing`

-   **AutoBoxing: Primitive -> wrapper Object**

-   Java tự động tạo `Object` từ `primitive` khi cần

```java
int x = 10;
Integer  y = x; // AUtoboxing . int -> Integer
Double d = 3.14; //Autoboxing double -> Double
```

-   Khi đó trong hoạt động ngầm hoặc khi biên dịch snag file jar sẽ như sau

```java
Integer  y  = Integer.valueOf(x);
Double d = Double.valueOf(3.14d);

```

-   **Unboxing Wrapper Object -> primitive**

-   Java `tự động lấy giá trị primitive từ object` khi cần

```java
Integer a= 20;
int b = a;
int sum = a + 5;
```

-   Được biên dịch thành

```java
Integer a = Integer.valueOf(20);
int b = a.intValue();
int sum = a.intValue() + 5;
```

-   Vì `wrapper clas là immutable`, nên khi gán lại sẽ tạo Object mới, không thay đổi Object cũ
-   AUtoboxing và unboxing hoạt động với tất cả các class `Integer` , `Double` `Boolean`, `Character`

### Có thể so sánh hai kiểu dữ liệu này với nhau không?

-   Có . Nhưng `cách so sánh sẽ khác nhau ` tùy thuộc vào kiểu dữ liệu

-   **So sánh giữa các primitive**

-   Dùng toán tử `==` để so sánh `giá trị`

```java
int a = 5;
int b = 5;
System.out.println(a == b) // true
```

-   Có thể sử dụng các toán tử khác như `!=`, `<` , `>` , `<=` , `>=`

-   **So sánh giữa các Object (Wrapper hoặc custom Object)**

-   `==` So sánh `tham chiếu reference` -> hai biến có trỏ đến cùng một Object hay không

-   `equals()` so sánh 2 giá trị hoặc nội dung , (tùy theo cách triển khai của method `equals()` của mỗi class hoặc kế thừa từ Object)

-   **So sánh giữa primitive và Wrapper**

-   Khi so sánh primtive với wrapper . Java `unboxing` wrapper -> primitive rồi sử dụng `==` so sánh 2 giá trị

```java
int a = 50;
Integer b = 50;
System.out.println(a == b) ; // true , b sẽ được unbox thành int rồi so sánh s
```

-   Ta cũng có thể sử dụng `equals()` (method của wrapper)

```java
System.out.println(b.equals(a));//true
```

-   **So sánh giữa các Object ngoài Wrapper**

-   Toán tử `==` so sánh tham chiếu
-   với `String literal`, có cơ chế `interning` nên sẽ giống như so sánh giá trị

-   Phương thức `equals()`

    -   Kiểm tra `nội dung bên trong một object`
    -   Mặc định theo `Object.equals()` cũng giống `==` so sánh tham chiếu
    -   Nếu `override` lại thì tùy theo logic đã triển khai

-   So sánh với các primitive
-   Không thể trực tiếp sử dụng `==` với các` Object khác ngoài wrapper`

### Giá trị khi khởi tạo biến với hai loại kiểu dữ liệu này là gì?

-   Trong Java. `Giá trị khởi tạo của biến phụ thuộc đó là pritmive hay Object và vị trí khai báo (field hay local variable)`

-   Tất cả các biến localvarialbe đều yêu cầu phải gán trước khi sử dụng

-   Còn field (static variable, instnace) thì được gán các giá trị mặc định

-   **Primitive type**

-   `Field`

    -   byte, short, int : `0`
    -   long `0L`
    -   float `0.0f`
    -   double `0.0d`
    -   char `\u0000` (kí tự unicode 0)
    -   boolean `false`

-   **Object types (Reference Types)**

-   KHi khao báo các varaible ở cấp độ feild của tất cả các Object sẽ luôn là `null` VD `String, Integer, Double - Wrapper, int[], String[] - Array, Person - custome Object`


