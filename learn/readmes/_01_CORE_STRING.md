## Java Core Basic

## 2. String

### Tìm hiểu về các đặc điểm và tính chất của String trong java

-   **Immutatable (bất biến)**

-   Khi một `String` được tạo ra. Giá trị của nó `không thể thay đổi`

-   nếu ta thực hiện một phép gán hay thao tác thay đổi (nối chuỗi) . Java sẽ tạo ra một chuỗi mới thay vì chuỗi cũ

```java
String s1 = "Hello";
String s2 = s1.concat(" World");

System.out.printn(s1); // Hello
System.out.println(s2); // Hello World
// `s1` vẫn giữ nguyên giá trị. `s2` là một chuỗi mới
```

-   **Lưu trữ trong StringPools**
-   Nhiều biến có thể trỏ tới cùng một `String` thay vì tạo ra nhiều bản sao

-   Các chuỗi `String` được tạo bằng `literal` `String s = "hello"` được lưu trong một vùng nhớ đặc biệt gọi là `StringPool`
-   nếu tạo ra một chuỗi Literal trùng giá tị . Java sẽ `tái sử dụng` chuỗi đó. Mà không tạo ra một đối tượng mới

```java
String a = "Java";
String b = "Java";
System.out.println( a== b) // true , Cùng tham chiếu đến một địa chỉ ô nhớ
```

-   Nếu sử dụng `new String("Java")` thì đối tượng mới sẽ được tạo ngoài pool

```java
String c = new String("Java");
System.out.println(a == c); //false
```

-   **CÓ thể so sánh nội dung với equals**

-   Vì `String` là đối tượng. Dùng `==` sẽ so sánh tham chiều không phải nội dung.
-   Để so sánh nội dung dùng :

```java
a.equals(b); // true
```

-   **Có nhiều phương thức tiện ích**

-   `String` cung cấp nhiều phương thức xử lý chuỗi VD:

    -   `length()` độ dài
    -   `charAt(index)` , lấy ra kí tự tại vị trí
    -   `substring(start,end)` cắt chuỗi
    -   `indexOf()` , `lastIndexOf()`
    -   `trim()`, `toLowerCase()` , `toUpperCase()`
    -   `split()` , `startsWith()` , `endsWith()`
    -   `contains()`

-   **Có thể nối chuỗi bằng toán tử +**

-   Java hỗ trợ `nối chuỗi` bằng toán tử `+`

```java
String s = "Hello" + " " + "World";
System.out.println(s) ;// Hello World
```

-   Thực chất khi mà có quá nhiều kí tự cần nối. Java sẽ chuyển đổi về `StringBuilder` ở thời gian biên dịch cho hiệu xuất tốt hơn

-   **Có thể chuyển đổi với mảng kí tự**

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
