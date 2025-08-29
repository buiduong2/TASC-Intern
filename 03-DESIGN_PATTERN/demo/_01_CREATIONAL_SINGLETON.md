## 1. CREATIONAL PATTERN

-   Creational Patterns
    -   Giúp `tạo Object` theo cách linh hoạt , kiểm soát việc tạo instance , tránh phụ thuộc trực tiếp vào Constructor
-   Structural Patterns
    -   Giúp `tổ chức lớp và object` sao cho các phần của hệ thống kết nối linh hoạt , dễ mở rộng
        -   Adapter: interface chuyển đổi
        -
-   Behavioral Patterns
    -   Giúp `quản lý cách tách các Object tương tác và truyền thông tin với nhau`
    -   Tách các trách nhiệm (chức năng), giảm ràng buộc giữa các Object, xử lý các hành vi phức tạp

## 2. Singleton

-   Tìm hiểu về mục đích, cách thức triển khai, ưu điểm của:

-   **Tóm tắt**

-   _Mục đích :_

    -   Đảm bảo rằng class chỉ có một instance
        -   kiểm soát sự truy cập vào một vài tài nguyên được chia sẻ (database, file)
    -   Cung cấp một điểm truy cập toàn cầu cho một instance
        -   Không an toàn khi lưu một obj vào một varaible toàn cầu. Nó có thể bị gán lại ở bất kì đâu
    -   Bảo vệ instance khỏi bị ghi đè trong code

-   _Cách triển khai_

    -   Tạo một private constructor
        -   Để ngăn chặn sử dụng từ khóa new
    -   Tạo một static method mà nó hoạt động như một constructor
        -   Bên trong sẽ gọi đến private constructor và lưu nó vào một field static
        -   Tất cả các lần gọi tiếp theo sẽ return về một Object đã được Cache lại

-   _Ưu điểm :_

    -   Chúng ta đảm bảo rằng một class chỉ có một instance duy nhất
    -   Chúng ta nhận được điểm truy cập toàn cầu (thông qua gọi static method)
    -   Có thể khởi tạo một cách lazy chỉ khi nó được yêu cầu lần đầu tiên

-   _Nhược điểm:_
    -   Vi phạm định lý đơn trách nhiệm (2 nv: quản lý vòng đời + business)
    -   Được cho là một thiết kế tồi. khi mà các components của một chương trình biết đến nhau quá nhiều (hardCode gọi class.getIsntace() - Khó Mock)
    -   Pattern yêu cầu một cách đối xử đặc biệt trong đa luồng
    -   Khó để UnitTest. Bởi vì nhiều test framework dựa vào kế thừa khi sinh ra các MockObject (tức là ta ko thể mock cái method static được)
-   **Mục đích**:

-   Đôi khi, trong quá trình phân tích thiết kế một hệ thống, chúng ta mong muốn có những đối tượng cần tồn tại duy nhất và có thể truy xuất mọi lúc mọi nơi. Để hiện thực việc trên. Chúng ta có thể nghĩ tới việc sử dụng biến toàn cục (Global Variable: public static final). Tuy nhiên việc sử dụng biến toàn cục phá vỡ quy tắc của `OOP (encapsulation) - Tức là các tính kế thừa, abstract, đóng gói coi như vứt đi`. Để giải bài toán trên, người ta hướng đến một giải pháp là sử dụng `Singleton Pattern`

-   **NỚi sử dụng**

    -   Connection Pool Manager:
    -   Logger : nếu không sẽ ghi ra nhiều file khác nhau mất tính đồng nhất
    -   Config Manager: Mỗi instance giữ một config khác nhau -> ứng dụng chạy không nhất quán
    -   Cache Manager: Mỗi instance giữ cache khác nhau -> dữ liệu bị lệch pha ( do ko evict cache)

-   **Tại sao có thể bị phá**

    -   `Clone()` -> khi sử dụng `clone()` JVM ko gọi lại constructor . Mà chỉ copy vùng nhị phân của bộ nhớ . Để trống clone() thì ghi đè nó,
    -   `Reflection()`
    -   `Serialization` , `Deserialization` -> đọc file rồi tạo obj từ dữ liệu trong file
    -   `Multithreading` -> nếu không cài đặt DoubleCheck Locking

-   **Singleton so với static**

    -   Static class không thể kế thừa được

        -   Muốn kế thừa singleton có thể sử dụng `protected` hoặc lưu trữ một `Composition`

    -   cách tính như đa hình cũng vứt đi
        -   Không thể implemented interface
        -   Không thể extends một class bình thường khác
    -   Các tính đóng gói (state của một Object có lẽ tốt hơn một State của static ?)

    -   Không thể lazy loading

-   **Định nghĩa**

-   `Singleton` là một trong 5 pattern của nhóm Creation DesignPattern

-   `Singleton` đảm bảo chỉ có `duy nhất một thể hiện (instance)` được tạo ra và nó sẽ cung cấp cho chúng ta một method để có thể truy xuất được thể hiện duy nhất đó mọi lúc mọi nơi trong chương trình

```java
class Singleton {
    private Singleton instance;

    private Singleton() {

    }

    public static Singleton getInstance() {

    }
}
```

-   Đảm bảo rằng chỉ có một íntance của lớp

-   Việc quản lý truy cập tốn hơn vì chỉ có một thể hiện duy nhất
-   Có thể quản lý số lượng íntance trong một giới hạn chỉ định

-   **Cách triển khai**

-   Có rất nhiều cách để triển khai Singleton Pattern. nhưng cho việc implement bằng cách nào đi nữa cũng dựa vào nguyên tắc cơ bản dưới đây

    -   `private constructor`: để hạn chế truy cập từ class bên ngoài
    -   `private static final varaible` đảm bảo biến chỉ được khởi tạo trong class
    -   có một method `public static` để `return instance` được khởi tạo ở trên

-   **Eager Initialization**

-   `Singleton` class được khởi tạo ngay khi được gọi đến. Đây là cách dễ nhất nhưng nó có một số nhược điểm mặc dù instance đã được khởi tạo mà có thể sẽ không dùng tới

```java
public class EagerInitializedSingleton {
    private static final EagerInitializedSingleton INSTANCE = new EagerInitializedSingleton();

    // Sử dụng private constructor.
    // Như vậy bên ngoài class sẽ ko thể khởi tạo instance bằng constructor nưa
    private EagerInitializedSingleton() {
    }

    public static EagerInitializedSingleton getInstance() {
        return INSTANCE;
    }
}

```

-   Eager initializatoin là cách tiếp cận tốt, dễ cài đặt, tuy nhiên, nó dễ bị phá vỡ bởi Reflection

-   **Static Block Initialization**

-   Cách làm tương tự như `Eager initialization` chỉ khác phần `static block` cung cấp thêm lựa chọn cho việc handle exception hay cách xử lý khác

```java

public class StaticBlockInitialization {

    // Static Block initialization for exception handling
    static {
        try {
            INSTANCE = new StaticBlockInitialization();
        } catch (Exception e) {
            throw new RuntimeException("Exception occured in creating isntance");
        }
        // Tiến hành việc code theo logic khởi tạo. Thêm thắt tài nguuyen

    }
    private static final StaticBlockInitialization INSTANCE;

    //
    private StaticBlockInitialization() {

    }

    public static final StaticBlockInitialization getInstance() {
        return INSTANCE;
    }

}
```

-   **Lazy Initialization**

-   Là một cách làm mang tính mở rộng hơn 2 cách làm trên và hoạt động tốt trong môi trường đơn luồng

```java
public class LazyInitializedSingleton {
    // ko có final nữa
    private static LazyInitializedSingleton instance;

    private LazyInitializedSingleton() {

    }

    public static LazyInitializedSingleton getInstance() {
        if (instance == null) {
            instance = new LazyInitializedSingleton();
        }
        return instance;
    }
}

```

-   **Thread Safe Singleton**

-   Cách làm này khắc phục được nhược điểm của `Eager initialization` chỉ khi nào `getInstance()` được gọi thì instance mới được khởi tạo. Tuy nhiên cách này chỉ sử dụng tốt trong trường hợp đơn luồng `single-thread`. Trường hợp nếu có nhiều luồng (multi-thread) cùng chạy và cùng gọi hàm `getInstance()` tại cùng một thời điểm thì có thể có nhiều hơn một instance. Để khắc phực nhược điểm này . Chúng ta có thể sử dụng `ThreadSafeSingleton`

```java
public class ThreadSafeSingleton {

    // volatile để có thẻ khai báo một giá trị có thể được sử dụng bởi nhiều Thread
    // - tránh việc cache
    private static volatile ThreadSafeSingleton instance;

    private ThreadSafeSingleton() {

    }

    // Sử dụng từ khóa synchornized ở cấp độ của static method
    public static synchronized ThreadSafeSingleton getInstance() {
        if (instance == null) {
            instance = new ThreadSafeSingleton();
        }

        return instance;
    }
}
```

-   Cách này có nhược điểm là một phương thức `synchronized` sẽ chạy rất chậm và tốn hiệu năng, bất kì Thread nào gọi đến đều phải chờ nếu có một Thread khác đang sử dụng. Có những tác vụ xử lý trước và sau khi tạo thể hiện không nhất thiết phải block (tức là chỉ cần đồng bộ việc tạo và kiểm tra thôi). Vì vậy chúng ta cần cải tiến nó một chút với `Double Check Locking Singleton`

-   **Double Check Locking Singleton**

-   Để triển khai theo cách này, chúng ta sẽ kiểm tra sự tồn tại `instance` với sự hỗ trợ của đồng bộ hóa , hai lần trước khi khởi tạo. Phải khai báo `violatile` cho íntance để tránh việc không chính xác do quá trình tối ưu hóa của trình biên dịch (caching)

```java
public class DoubleCheckLockingSingleton {
    private static volatile DoubleCheckLockingSingleton instance;

    private DoubleCheckLockingSingleton() {

    }

    public static DoubleCheckLockingSingleton getInstance() {
        // Làm gì đó trước khi lấy instance

        // không syncrhonzied ở đây luôn vì nếu đã khởi tạo rồi thì đa luồng hay không chỗ này không còn quan trọng nữa
        // nếu ta cố gắng synchornized từ chỗ này thì mọi truy cập vào sẽ bị chặn không cần thiết
        if (instance == null) {
            // Làm các công việc trước khi khởi tọa instance...
            // Khóa các thread khác không thể đi vào trong trong khi khởi tạo
            synchronized (DoubleCheckLockingSingleton.class) {
                // Kiểm tra một lần nữa. Có thể các thread khác đã khởi tạo trước đó
                if (instance == null) {
                    instance = new DoubleCheckLockingSingleton();
                }
            }
        }

        return instance;
    }
}

```

-   **Bill Pugh Singleton Implementation**

-   Với cách này chúng ta sẽ tạo ra `static nested class` với vai trò 1 Helper khi muốn tách biệt chức năng cho một class function rõ ràng hơn. Đây là cách thường sử dụng và có hiệu suất tốt

```java
public class BillPughSingleton {
    // Class Singleton không lưu trữ tham chiếu của Singleton nữa

    private BillPughSingleton() {

    }

    public static BillPughSingleton getInstance() {
        return SingletonHelper.INSTANCE;
    }

    // Inner static private class lại lưu trữ tham chiếu đến instance
    private static class SingletonHelper {
        private static final BillPughSingleton INSTANCE = new BillPughSingleton();
    }

}
```

-   Khi Singleton được tải vào bộ nhớ thì SingletonHelper (inner private static Class) chưa được tải vào . Nó chỉ được tải khi và chỉ khi phương thức getInstance() được gọi (đọc đến dòng code dùng tên class)/ Cách này tránh được lỗi cơ chế khởi tạo instance của Singleton trong Multi-thread, Performance cao do tách biệt được quá trình xử lý. Do đó cách làm này được đánh giá là cách triển khai Singleton nhanh và hiệu quả nhất

-   **Phá vỡ cấu trúc Singleton pattern bằng reflection**

-   `Reflection` có thể được dùng để phá vỡ Pattern của `EagerInitialization` ở trên

```java
package com.gpcoder.patterns.creational.singleton;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ReflectionBreakSingleton {

    public static void main(String[] args)
            throws InstantiationException, IllegalAccessException, InvocationTargetException {

        EagerInitializedSingleton instanceOne = EagerInitializedSingleton.getInstance();
        EagerInitializedSingleton instanceTwo = null;

        Constructor<?>[] constructors = EagerInitializedSingleton.class.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            constructor.setAccessible(true);
            instanceTwo = (EagerInitializedSingleton) constructor.newInstance();
        }

        System.out.println(instanceOne.hashCode());
        System.out.println(instanceTwo.hashCode());
    }
}
```

-   Tương tự `Eager Initialization` implement theo `Bill Pugh Singleton` cũng có thể bị phá vỡ bởi Reflection

// Ta có thể ngăn chặn bằng cách thêm if-else trong contructor luôn

```java
    private Singleton() {
        if (instance != null) {
            throw new RuntimeException("Use getInstance() instead");
        }
    }
```

-   Nhưng cách này vẫn còn hạn chế bằng cách nó chỉ có thể hạn chế sau khi mà getInstance() đã được chạy mà thôi

-   Để an toàn tuyệt đối. Chúng ta sử dụng `EnumSingleton`

    -   JVM đảm bảo không thể tạo isntance mới Reflection
    -   Enum cũng chống được clone và deserizlizatoni

    -   Vậy còn vấn đề về OOP

        -   Enum cũng có field, method contrcutor như bình thường
        -   cũng có thể implement interface
        -   cũng có thể override method (của interface)

    -   Hạn chế của Enum:
        -   Không thể kế thừa class khác (vì Enum đã kế thừa Enum)

-   **Enum SIngleton**

-   Khi dùng `enum` thì các params (Tức là các phần mà gọi trực tiếp như Level.INFO hay Level.DANGER ấy) chỉ được khởi tạo 1 lần duy nhất. Đây cũng là cách giúp tạo ra Singleton instance

```java

public enum EnumSingleton {
    // Về lý thuyết đây cũng là LAZY
    // Khi mà chúng ta chưa chạm đến EnumSingleton.someThing thì nó chưa được khởi  tạo
    // Nhưng nếu có nhiều params thì tất cả parasm sẽ đồng thời được khởi tạo
    INSTANCE;

    private EnumSingleton() {
        System.out.println("Enum Constructor được gọi - được khởi tạo");
    }

    private int value = 0;

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void doSomething() {
        System.out.println("Enum Singleton is working! Current Value: =" + value);
    }
}

```

-   Enum có thể sử dụng như một Singleton, nhưng nó có nhược điểm là không thể `extends` từ một class được, nên khi sử dụng cần xem xét vấn đề này

-   Hàm `Constructor` của `enum` là `LAZY` nghĩa là khi được sử dụng thì mới chạy hàm khởi tạo và nó chỉ chạy 1 lần duy nhát một lần. Nếu muốn sử dụng như một EAGER Singleton thì cần gọi thực thi trong một `static Block` khi start chương trình

-   So sánh giữa 2 cách sử dụng `enum initialization` và `static block initiazation method`. Enum cũng có một điểm rất mạnh khi giải quyết vấn đề `Serialization/ Deserialization`

-   **Serialization và Singleton**

-   Đôi khi trong các hệ thống phân tán (distributed System) chúng ta cần implmenet intarface `Serialization` trong lớp Singleton để chúng ta thể lưu trữ trạng thái của nó trong file hệ thống và truy xuất lại nó sau

```java
 public static void serializedSingleton() {
        SerializedSingleton serializedSingleton = SerializedSingleton.getInstance();
        EnumSingleton enumSingleton = EnumSingleton.INSTANCE;
        SerializedSingleton serializedSingleton2 = null;
        EnumSingleton enumSingleton2 = null;

        try (ObjectOutput out = new ObjectOutputStream(new FileOutputStream("fileOutPutStream.txt"))) {
            // Mỗi Obj ghi một dòng
            // B1: tiến hành ghi cả 2 singleton Instance vào trong một file
            out.writeObject(serializedSingleton);
            out.writeObject(enumSingleton);
        } catch (IOException e) {

            e.printStackTrace();
        }

        try (// B2: tiến hành giải mã chuyển đổi nó thành Obj
                ObjectInput in = new ObjectInputStream(new FileInputStream("fileOutPutStream.txt"))) {

            serializedSingleton2 = (SerializedSingleton) in.readObject();
            enumSingleton2 = (EnumSingleton) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("Singleton Object bình thường có bị phá vỡ khởi tạo mới không: "
                + (serializedSingleton == serializedSingleton2)); // false - Object - Đã bị khởi tạo 1 đối tượng mới
        System.out.println("Singleton Enum có bị phá vỡ khởi tạo mới không: " + (enumSingleton == enumSingleton2));
        // true - Interface vẫn là một instance cũ
    }
```

-   Như trong ví dụ trên `Deserialize` đối tượng `SerializedSingleton` khác với đối tượng gốc. Tuy nhiên vấn đề này `không xảy ra khi sử dụng enum`

-   Thực tế thì vẫn có nhiều cách khắc phục khi sử dụng class `SerializedSingleton` là triển khai một method `readResolve()`. Nhưng khi chúng ta thực sự gặp phải vấn đề cần sử dụng `Serialize/ Deserialize` thì nên sử dụng `enum` vẫn đơn giản hơn

-   **Sử dụng Singleton Pattern khi nào**

-   Dưới đây là một số trường hợp sử dụng SIngleton Pattern thường gặp

-   Một Số DesignPattenr khác cũng sử dụng Singleton để triển khai : `AbstractFactory` , `Builder`, `Prototype`

-   Dùng `LazyInitializedSingleton` cho những ứng dụng làm việc với `single-thread` và sử dụng `DoubleCheckLockSingleton` khi làm việc với ứng dụng `Multi-thread`
