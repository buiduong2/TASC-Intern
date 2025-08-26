## Thread

## Process là gì

-   `Process(Tiến trình)`

    -   Process (Tiến trình) là `một chương trình đang chạy` trong hệ điều hành
        -   Khi chúng ta double-click vòa một ứng dụng. hệ điều hành sẽ tạo ra một process
        -   Một process có `không gian địa chỉ bộ nhớ riêng biệt`, các tài nguyên riêng (file descriptor, network connection, vùng nhớ heap, stack ...)
        -   Mỗi Process thường độc lập với các process khác -> process này không thể trực tiếp truy cập vòa vùng nhớ của process khác (trừ khi có cơ chế liên lạc như IPC - Inter- Process Communication) (giao tiếp giữa các tiến trình có chi phí cao)

-   trong Java
    -   Khi chúng ta chạy một chương trình Java VD( `MyApp`), JVM (Java Virtual Machine) chính là một process do hệ điều hành tạo ra
    -   JVM lại quản lý nhiều `Thread` bên trong nó

## Thread là gì


-   `Thread` luồng. là một `đơn vị nhỏ nhất của CPU` có thể được lên lịch để thực thi
-   Trong một `process`. Có thể có nhiều `Thread` cùng chạy song song. Chia sẻ cùng một bộ nhớ và tài nguyên bên trong Process đó

-   Trong Java
    -   Thread là 1 Luồng `thực thi độc lập` bên trong chương trình
    -   Khi một chương trình Java chạy nó sẽ tạo ra ít nhất 1 thread là 1 ThraedMain để thực thi phương thức `main`
    -   Ta có thể tạo thêm nhiều thread khác để thực hiện công việc song song (như xử lý dữ liệu, đọc file, giao tiếp mạng)
    -   là một tiến trình con `(sub-process)

- So sánh với Process
    - Các luồng chia sẻ không gian địa chỉ ô nhớ giống nhau
    - Luồng là nhẹ
    - Sự giao tiếp giữa các luồng có chi phí thấp

```java
public class MyThreadDemo {
    public static void main(String[] args) {
        // Thread chính (main thread)
        System.out.println("Hello from main thread");

        // Tạo một thread mới bằng cách dùng lambda (Runnable)
        Thread worker = new Thread(() -> {
            System.out.println("Hello from worker thread");
        });

        worker.start(); // chạy thread mới song song với main
    }
}
// Khi chạy thì min thread và woker threaad chạy song song
```

-   **Đặc điểm của Thread**

-   Là một thực thể của `java.lang.Thread` hoặc được tạo thông qua `Runnable` / `Callable`
-   Mõi Thread có ngăn xếp `stack riêng` , nhưng `chung bộ nhớ heap` với các thread khác trong cùng một chương trình
-   CHương trình được dùng để

    -   Chạy `đa nhiệm multitasking`
    -   Tăng hiệu năng trên `đa lõi CPU`
    -   Xử lý tác vụ nền (background task)

-
-   **So sánh ngắn gọn**
-   `process`: chương trình đang chạy, nặng, độc lập, bộ nhớ riêng
-   `Thread` Luồng thực thi trong process, chia sẻ bộ nhớ

-   **Vòng đời của Thread**
-   Vòng đời của Thread trong Java được kiểm soát bởi JVM. Java Định nghĩa các trạng thái của Luồng bằng thuộc tính tatic của class `Thread.State`
    -   `NEW`: đây là trạng thái khi luồng vừa được tạo bằng phương thức khởi tạo của Thread, nhưng chưa được `start()`. Ở trạng thái này, luồng được tạo ra nhưng chưa được cấp phát tài nguyên và cũng chưa chạy. Nếu luồng đang ở trạng thái này mà chúng ta gọi các phương thức ép buộc như `stop`, `resume` ,`suspend`. sẽ là nguyên nhân xảy ra một exception `IllegalThreadStateException`
    -   `RUNABLE`: sau khi gọi phương thức `start()` thì luồng test đã được cấp phát tài nguyên và các lịch điều phối CPU cho luồng test cũng bắt đầu có hiệu lực. Ở đây, chúng ta dùng trạng thái là runnable chứ không phải là running, vì luồng không thực sự luôn chạy mà tùy vào hệ thống mà có sự điều phối CPU khác nhau
    -   `WAITING`: Thread chờ không giới hạn trong một thời gian nhất định, hoặc là không có luồng nào đánh thức nó
    -   `BLOCKED` Đây là một trạng thái "Not runnable" là trạng thái Thread vẫn còn sống, nhưng hiện tại không được chọn để chayj. Thread chờ một monitor để unlokc một đối tượng mà nó cần
    -   `TERMINATED` : Một thread trong trạng thái terminated hoặc dead khi phương thức `run()` của nó bị thoát

## Có bao nhiêu cách để tạo 1 thread trong java

- Có khoảng 2 cách. một là tạo 1 `class extends từ Thread`. hoặc `implement một interface Runnable`

- **Tạo luồng bằng cách extends từ lớp Thread**

- Khai báo một class extends Thread
- Override lại method run: những gì trong method này sẽ được thực thi khi luồng bắt đầu chạy. Sau khi luồng chạy xong tất cả các câu lệnh trong method run thì luồng cũng tự hủy
- Tạo 1 instance của class ta vừa khai báo
- gọi method `start()` để bắt đầu thực thi luồng 

```java
package com.gpcoder.simple;
 
public class TheadSimple extends Thread {
    public void run() {
        System.out.println("thread is running...");
    }
 
    public static void main(String args[]) {
        TheadSimple t1 = new TheadSimple();
        t1.start();
    }
}
```

- Method `start()` là một method đặc biệt trong java mà được xây dựng sẵn ở trong class `Thread`. Method này sẽ cáp phát tài nguyên cho thread mới rồi chạy method `run()` trong luồng mới đó

- **Tạo luồng bằng cách implement từ interface Runnable**
- 1. Khai báo 1 lớp implement từ interface Runnable 
- 2. implmenet method `run()`. Nhưng gì trong `run()` sẽ được thực thi ở một luồng mới độc lập. Chạy xong run() này thì luồng cũng tự hủy
- Tạo 1 instance của class vừa khai báo 
- Tạo một instance của Thread bằng constructor `Thread(Runnable target)`

```java
public class RunnableSimple implements Runnable {
    public void run() {
        System.out.println("thread is running...");
    }
 
    public static void main(String args[]) {
        RunnableSimple runable = new RunnableSimple();
        Thread t1 = new Thread(runable);
        t1.start();
    }
}
```

- **Cách hay được sử dụng nhất**

- sử dụng `interface Runnable`. Bởi vì nó `ko yêu cầu extends từ lớp Thread`. Trong trường hợp ứng dụng thiết kế yêu cầu đa kế thừa. Chỉ có Interface mới giải quyết được vấn đề

- **ThreadPool**

##  - Thế nào là multi thread ? Sử dụng multi thread mang lại ưu nhược điểm gì ?

- **Định nghĩa**: 
    - là một tiến trình (Process) thực hiện nhiều luồng đồng thời. Một ứng dụng Java ngoài luồng chính có thể có các luồng khác thực thi đồng thời làm ứng dụng chạy nhanh và hiệu quả hơn 

- **Ưu điểm**
    - Nó không chặn người sử dụng vì các luồng là độc lập và ta có thể thực hiện nhiều công việc cùng lúc
        - Như luồng giao diện, và gửi kết quả
    - Mỗi luồng có thể dùng chung và chia sẻ nguồn tài nguyên trong quá trình chạy, nhưng có thể thực hiện một cách độc lập
    - Exception có Throw thì ko ảnh hưởng đến Thread khác

- **Nhược điểm**
    - Càng nhiều luồng thì xử lý càng phức tạp
    - Xử lý vấn đề tranh chấp bộ nhớ, đồng bộ dữ liệu phức tạp
    - Cần tránh các luồng chết (deadlock) , luồng chạy mà không làm ji cả trong ứng dụng
## câu hỏi

-   Vậy Thread so với Java là tương đương với process so với hệ điều hành à

-   Trong hệ điều hành
    -   Process là đơn vị thực thi chính
    -   Bên trong Process sẽ có các Thread chạy song song
-   Trong Java (JVM)

    -   Cả chương trình Java chạy bên trong một Process
    -   Bên trong nó Java quản lý nhiều `thread`
    -   Java Thread chính là phiên bản thu nhỏ của Process trong OS: nó cũng là một đơn vị thực thi độc lập. Nhưng nhẹ hơn process vì Thread `chung tài nguyên` (bộ nhớ, file, socket) của JVM Process

-   Vậy Process có chạy song song với nhau không

-   stack riêng cho từng THread là sao

-   `đơn vị nhỏ nhất của CPU` là gì
    -   OS (Qua CPU Schedule) `không lên lịch cho từng hàm hay chạy từng dòng code của chúng ta`, nà lên lịch ở mức Thread
    -   Thread cũng thuộc OS. Một đơn vị thực thi mã để CPU có thể lên lịch chạy
