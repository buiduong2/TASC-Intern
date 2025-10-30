## Java Core Basic

## 2. String

### Tìm hiểu về các đặc điểm và tính chất của String trong java

-   **Immutatable (bất biến)**

-   `khái niệm:` Khi một đối tượng String đã được tạo ra,` giá trị của nó không thể bị thay đổi (immutable).`

-   `Hành vi`: nếu ta thực hiện một phép gán hay thao tác thay đổi (nối chuỗi) . Java sẽ tạo ra một chuỗi mới thay vì chuỗi cũ

-   `lý do bất biến`

    -   `An toàn Luồng (Thread Safety)` Vì giá trị không thay đổi, các luồng khác nhau có thể chia sẻ cùng một đối tượng String mà không cần cơ chế đồng bộ hóa (synchronization), giúp chúng tự động an toàn cho đa luồng.

    -   `Bảo mật (Security)`: Chuỗi thường được dùng để lưu trữ thông tin nhạy cảm (như mật khẩu, URL kết nối). Tính bất biến đảm bảo rằng giá trị của chuỗi không bị thay đổi bất ngờ sau khi kiểm tra bảo mật.

    -   `String Pool`: Tính bất biến cho phép Java sử dụng `String Constant Pool` (xem mục 2) để tiết kiệm bộ nhớ, vì nhiều biến có thể trỏ đến cùng một đối tượng chuỗi mà không sợ bị thay đổi.

    -   `Sử dụng trong HashMap`: Chuỗi là khóa phổ biến trong HashMap. Nếu String là khả biến, mã băm (hashCode()) của nó có thể thay đổi sau khi chèn, khiến việc truy xuất dữ liệu bị sai lệch.

-   **Lưu trữ trong StringPools**
-   Nhiều biến có thể trỏ tới cùng một `String` thay vì tạo ra nhiều bản sao

-   Các chuỗi `String` được tạo bằng `literal` `String s = "hello"` được lưu trong một vùng nhớ đặc biệt gọi là `StringPool`
-   nếu tạo ra một chuỗi Literal trùng giá tị . Java sẽ `tái sử dụng` chuỗi đó. Mà không tạo ra một đối tượng mới

-   **CÓ thể so sánh nội dung với equals**

-   Vì `String` là đối tượng. Dùng `==` sẽ so sánh tham chiều không phải nội dung.
-   Để so sánh nội dung dùng :

```java
a.equals(b); // true
```

-   **Là một đối tượng Object**

-   `String` cung cấp nhiều phương thức xử lý chuỗi VD:

    -   `length()` độ dài
    -   `charAt(index)` , lấy ra kí tự tại vị trí
    -   `substring(start,end)` cắt chuỗi
    -   `indexOf()` , `lastIndexOf()`
    -   `trim()`, `toLowerCase()` , `toUpperCase()`
    -   `split()` , `startsWith()` , `endsWith()`
    -   `contains()`

-   **Có thể nối chuỗi bằng toán tử +**

-   Thực chất khi mà có quá nhiều kí tự cần nối. Java sẽ chuyển đổi về `StringBuilder` ở thời gian biên dịch cho hiệu xuất tốt hơn

-   **Dãy ký tự**

```java
char[] chars = s.toCharArray();
```

-   Hoặc tạo chuỗi từ mảng kí tự

```java
String s2 = new String(chars);
```

-   **Comparable**

-   `String` implements `Comparable<String>` -> có thể sắp xếp theo thứ tự từ điển

### Có bao nhiêu cách để tạo 1 biến String

-   **Tạo bằng String literal**

```java
String s1 = "Hello World"
// Sẽ được lưu trong `StringPool`
```

-   **Tạo bằng toán tử `new`**

```java
String s2 = new String("Hello");
// Luôn tạo một đối tượng mới trong Heap.
// Không dùng lại StringPool

```

-   **Tạo từ mảng kí tự**

```java
char[] chars = new char[] {'J', 'a', 'v', 'a'};
String s3 = new String(chars); // Java
```

-   **Tạo từ các phương thức tiện ích**

-   Dùng `String.valueOf();` . Hoặc `String.format()`

```java
String s7  = String.valueOf(123); // "123"
String s8 = String.valueOf(true); // "true"
```

-   Dùng `StringBuilder` / `StringBuffer`

```java
StringBuilder sb = new StringBuilder("Hello");
sb.append(" World")
String s10 = sb.toString(); // "Hello World";
```

-   **Nôi chuỗi**

```java
String s11 = "Hello " + "World"; // "Hello World"
String s12 = 123 + "Hello"; //123Hello - Tự động biến đổi các kiểu dữ liệu khác thành String trước khi thực hiện làm việc với String
```

### Tìm hiểu về String pool?

-   **Khái niệm**

-   `String Pool` hay là `internPool` . Là một `vùng nhớ đặc biệt trong Heap` mà JVM dành riêng để lưu trữ các String Literal

-   Khi ta tạo một chuỗi bằng `literal` VD (`String  s= "Java"`) JVM sẽ kiểm tra trong String Pool

    -   Nếu chuỗi đó đã tồn tại -> Dùng lại tham chiếu cũ
    -   Nếu chưa -> tạo mới và thêm vào pool

-   Giúp tiết kiệm bộ nhớ, tăng hiệu xuất

-   **Cách được lưu trong pool**
-   sử dụng Literal : thì sẽ cố gắng cache lại String
-   Sử dụng từ khóa `new` tạo ra một đối tượng mới hoàn toàn ko tương tác với pool

-   **intern()**

-   method này để ép một `String` (kể cả tạo bằng `new`) vào trong pool

```java
String c = new String("Java");
String d = c.intern();

String a = "Java";

System.out.println(a == d); // true
```

### Làm sao để so sánh hai chuỗi trong java

-   **So sánh tham chiều `==`**

-   **So sánh nội dung `equals()`**

-   **So sánh không phân biệt hoa thường**

-   **So sánh thứ tự từ điển `compareTo()`**
