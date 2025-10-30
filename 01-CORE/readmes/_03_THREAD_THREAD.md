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

-   `Thread` (hay còn gọi là luồng) trong lập trình là một chuỗi thực thi độc lập nhỏ nhất `nằm trong một tiến trình` (process).

-   `Thread (Luồng)` là một phần của `tiến trình`. Một tiến trình có thể chứa nhiều luồng. Các luồng này `chia sẻ chung` không gian bộ nhớ và tài nguyên của tiến trình cha, nhưng mỗi luồng có ngăn xếp (stack) và bộ đếm chương trình (program counter) `riêng để thực hiện các công việc khác nhau cùng lúc (đồng thời).`

-   **Lợi ích của Thread**

-   Việc sử dụng thread, còn gọi là đa luồng (multithreading), mang lại nhiều lợi ích quan trọng:

    -   `Tăng tốc độ xử lý (Hiệu suất):` Bằng cách cho phép chương trình thực hiện nhiều tác vụ đồng thời. Ví dụ, trong một ứng dụng đồ họa, một luồng có thể xử lý giao diện người dùng, trong khi luồng khác đang thực hiện tính toán phức tạp ở nền.

    -   `Phản hồi tốt hơn: `Giúp giao diện người dùng không bị "đứng" (freeze) khi chương trình đang thực hiện một tác vụ nặng. Luồng giao diện vẫn hoạt động bình thường, trong khi các luồng khác xử lý công việc.

    -   `Sử dụng hiệu quả tài nguyên CPU:` Đặc biệt quan trọng với các CPU đa nhân (multi-core), vì các luồng có thể được phân phối và chạy trên các nhân CPU khác nhau.

-   Trong một `process`. Có thể có nhiều `Thread` cùng chạy song song. Chia sẻ cùng một bộ nhớ và tài nguyên bên trong Process đó

-   **Thread**

-   `Đối tượng Thread trong Java` là một thực thể `đại diện cho một luồng thực thi` (a single thread of control) trong chương trình của bạn. 🧵 Nó cho phép bạn thực hiện các `tác vụ song song hoặc đồng thời trong Java.`

-   **Đặc điểm**
    -   Chia sẻ Tài nguyên
    -   Độc lập Xử lý

## Có bao nhiêu cách để tạo 1 thread trong java

-   Có khoảng 2 cách. một là tạo 1 `class extends từ Thread`. hoặc `implement một interface Runnable`

-   **Tạo luồng bằng cách extends từ lớp Thread**

-   Khai báo một class extends Thread
-   Override lại method run: những gì trong method này sẽ được thực thi khi luồng bắt đầu chạy. Sau khi luồng chạy xong tất cả các câu lệnh trong method run thì luồng cũng tự hủy
-   Tạo 1 instance của class ta vừa khai báo
-   gọi method `start()` để bắt đầu thực thi luồng

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

-   Method `start()` là một method đặc biệt trong java mà được xây dựng sẵn ở trong class `Thread`. Method này sẽ cáp phát tài nguyên cho thread mới rồi chạy method `run()` trong luồng mới đó

-   **Tạo luồng bằng cách implement từ interface Runnable**
-   1. Khai báo 1 lớp implement từ interface Runnable
-   2. implmenet method `run()`. Nhưng gì trong `run()` sẽ được thực thi ở một luồng mới độc lập. Chạy xong run() này thì luồng cũng tự hủy
-   Tạo 1 instance của class vừa khai báo
-   Tạo một instance của Thread bằng constructor `Thread(Runnable target)`

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

-   **Cách hay được sử dụng nhất**

-   sử dụng `interface Runnable`. Bởi vì nó `ko yêu cầu extends từ lớp Thread`. Trong trường hợp ứng dụng thiết kế yêu cầu đa kế thừa. Chỉ có Interface mới giải quyết được vấn đề

-   **ThreadPool**

-   **CompleteFuture**

## - Thế nào là multi thread ? Sử dụng multi thread mang lại ưu nhược điểm gì ?

-   **Định nghĩa**:

    -   `Đa luồng (Multithreading)` là một khả năng của chương trình hoặc hệ điều hành cho phép `thực hiện nhiều luồng (thread) `hoặc nhiều phần của một chương trình `một cách đồng thời (concurrently) `hoặc `song song (parallelly).`

-   **Ưu điểm**

    -   `Phản hồi Tốt hơn:` Giúp ứng dụng không bị "đứng" (freeze). Ví dụ, một luồng xử lý tải dữ liệu nặng, trong khi luồng khác vẫn duy trì giao diện người dùng hoạt động bình thường.

    -   `Sử dụng Tài nguyên Hiệu quả` Tận dụng tối đa sức mạnh của các bộ xử lý đa nhân, cho phép chương trình hoàn thành công việc nhanh hơn.

    -   `Tiết kiệm Chi phí` Việc tạo và chuyển đổi giữa các luồng tốn ít tài nguyên và thời gian hơn nhiều so với việc tạo và chuyển đổi giữa các tiến trình (process) hoàn toàn mới.

-   **Nhược điểm**

    -   `Phức tạp trong Lập trình và Gỡ lỗi (Debugging)`
    -   `Nguy cơ về Đồng bộ hóa và Tranh chấp Dữ liệu`
    -   `Chi phí Quản lý và Overhead`:
        -   Context Switching Overhead
        -   Tăng Mức sử dụng Bộ nhớ

-   **So sánh đồng thời và song song**

    -   `Đồng thời (Concurrency):` Xảy ra khi một CPU đơn nhân (single-core) quản lý nhiều tác vụ bằng cách chuyển đổi qua lại rất nhanh giữa chúng (time-slicing). Mặc dù có vẻ như chúng chạy cùng lúc, nhưng thực chất tại một thời điểm, chỉ có một luồng đang chạy.

    -   `Song song (Parallelism): `Xảy ra khi các luồng được thực thi cùng một lúc trên các nhân CPU (cores) khác nhau của bộ xử lý đa nhân. Đây là cách thực sự giúp tăng tốc độ xử lý tổng thể.

## - Làm thế nào để biết được 1 thread, multi thread đã hoàn thành hay chưa?

-   **Với việc tạo Thread thông thường**
-   `Thread t = new Thread(...);`
-   Ta có thể sử dụng method `t.isAlive()`

-   `join()`

-   Ta có thể dùng `join()` (ưu tiên nhất)

-   **Với ExecutorService (đa luồng theo Pool)**

-   Chạy nhiều task với `ExecutorService`

-   Sử dụng `Future`

    -   `future.isDone()` giống với `Thread.isAlive()`
    -   `future.get()` sẽ đợi đến khi thread chạy xong và return một giá trị

-   Sử dụng `shutDown()`

    -   Dừng hoạt đồng Thread pool khi không còn Thread nào alive

-   **Sử dụng CompleteFuture (async kiểu mới)**

-   Sử dụng `then...()` để có thể truyền vào một Lamba sẽ được chạy khi mà cái Future kia kết thúc
-   Hoặc sử dụng `Join()` hoặc `get()` vì return là một kế thừa của `Future` giống của ObjectPool

## - Có giới hạn việc tạo ra bao nhiêu thread trong 1 ứng dụng java hay không?

-   Không . trong Java `Không giới hạn cứng chính xác về số lượng Thread` mà chúng ta có thể tạo ra trong một ứng dụng.
-   Nhưng `vẫn tồn tại các giới hạn thực tế` phụ thuộc vào

-   **Giới hạn từ hiệu điều hành và JVM**

-   Mỗi `Thread` trong java thực chất là một `OSThread`
-   Mỗi Thread cần một `ThreadStack` (ngăn xếp riêng), dung lượng mặc định thấp 512Kb - 1Mb. nếu chúng ta tạo quá nhiều Thread -> Tốn RAM -> có thể gặp lỗi `OutOfMemoryError: unnable to create new native Thread` (Ta có thể cấu hình lại tăng hoặc giảm -> Số lượng Thread sẽ khác)

-   **Giới hạn từ tài nguyên máy**

-   Tổng số Thread bị giới hạn bởi

    -   Bộ nhớ RAM (stack của mỗi Thread)
    -   Giới hạn OS (các hệ điều hành cũng giới hạn số lượng Thread tối đa)
    -   CPU: quá nhiều thread -> giảm hiêu năng

-   **Thực tế**
-   Hiếm khi có ứng dụng nào tạo quá nhiều Thread (hàng chục ngìn)
-   HỌ sẽ cố gắng tái sử dụng bằng `ThreadPool`

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
