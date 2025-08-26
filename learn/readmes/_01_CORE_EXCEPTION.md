## - Phân biệt throw và throws

-   **Throw**

-   Ý nghĩa: Dùng để ném `throw` một `exception cụ thể` trong code
-   Vị trí : Bên trong `method` hoặc `khối block`
-   Số lượng: Chỉ có thể ném `một exception` tại một thời điểm
-   Đối tượng: sau throw `phải là một instance` của một lớp `Throwable` (thường là `Exception`) hoặc `Error`

```java
public void checkAge(int age) {
    if (age < 18) {
        throw new IllegalArgumentException("Age must be >= 18");
    }
}
```

-   **Throws**

-   Ý nghĩa: Dùng để khai báo rằng method có thể ném ra một exception nào
-   Vị trí : ở phần khai báo `method` (sau dấu `(`)
-   Số lượng: có thể khai bóa `nhiều excpetion`, cách nhau bằng đấu `,`
-   Đối tượng: Chỉ áp dụng checked exception (ngoại trừ `RunTimeException` và `Error`)

```java
public void readFile(String fileName) throws IOException {
    FileReader reader = new FileReader(fileName);
    reader.close();
}
```

## - try catch , try with resource khác nhau như thế nào ?

-   **try-catch**

-   Mục đích: bắt và xử lý `exception`

-   Cơ chế Nếu trong `try` có lỗi xảy ra -> `catch` sẽ bắt và xử lý
-   Quản lý tài nguyên : Không tự động giải phóng, close tài nguyên, phải làm `thủ công` trong `finally`
-

```java
public void readFile() {
    FileReader reader = null;
    try {
        reader = new FileReader("test.txt");
        int data = reader.read();
        System.out.println((char)data);
    } catch (IOException e) {
        e.printStackTrace();
    } finally {
        try {
            if (reader != null) {
                reader.close(); // phải đóng thủ công
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

-   **try-with-resource** (Java 7+)

-   Mục đích: Vừa xử lý excpetoin, vừa tự động giải phóng tài nguyên (rerouce) sau khi dùng xong

-   Điều kiện: tài nguyên phải implmenet interface `AutoCloseable` VD `FileReader` `BufferReader` `Connection`

-   Cơ chế: sau khi `try` chạy xong, Java tự động gọi `close()` cho tất cả các resource trong ngoặc `()` mà không cần `finally`

```java
public void readFile() {
    try (FileReader reader = new FileReader("test.txt")) {
        int data = reader.read();
        System.out.println((char)data);
    } catch (IOException e) {
        e.printStackTrace();
    }
    // reader sẽ tự động được đóng ở đây, không cần finally
}
```

## - Làm thế nào để tạo được 1 custom exception ?

-   **Kế thừa từ Exception (Checked Exception)**

-   **Kế thừa từ RuntimeException (Unchecked Exception)**
