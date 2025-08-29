## 1. Synchronous vs Asynchronous

-   **Synchronous (đồng bộ)**

-   Các tác vụ được thực hiện `theo thứ tự`, task sau `phải chờ task trước hoàn thành` mới chạy
    -Đặc điểm : - Dễ hiểu, dễ debug - Có thể block thread - Hiệu suất thấp nếu task mất thời gian lâu

-   **Asynchronous (Bất đồng bộ)**
-   Task được `gửi đi và chạy nền`, thread chính `không phải chờ` kết quả. Kết quả được xử lý sau thông qua callBack, Future, hoặc Event

-   Đặc điểm
    -   Hiệu suất cao hơn
    -   Không block main thread
    -   Cần xử lý callback, Future , Promise

## - Phân biệt trường hợp sử dụng, ưu nhược điểm của async và sync

-   **Synchronos (đồng bộ)**

-   Trường hợp sử dụng

    -   Các `tasks phải  thực hiện theo thứ tự`, task sau phụ thuộc kết quả vào task trước
    -   Khi `dữ liệu phải sẵn sàng thì mới xử lý được tiếp`
    -   VD:
        -   Lấy dữ liệu Database rồi hiển thị ngay
        -   API nội bộ nhỏ, nhanh, không lo lắng vấn đề block thread

-   Ưu điểm
    -   Dễ hiểu, dễ debug
    -   Dễ kiểm soát luồng và Logic xử lý
    -   Ít về race condition (vì task tuần tự)
        -   Tranh chấp tài nguyên giữa cac Thread khi cùng truy cập vào một tài nguyên
-   Nhược điểm

    -   Block Thread -> giảm hiệu suất nếu task (I/O , API chậm)
    -   Không tối ưu khi cần xử lý nhiều tác vụ cùng lúc
    -   UI server có thể bị đứng nếu chờ task quá lâu

-   **Asynchronous (bất đồng bộ)**

-   Trường hợp sử dụng
    -   Task `Không cần kết quả ngay` có thể xử lý sau
    -   Task `I/O`, hoặc `nhiều tác vụ có thể chạy song song`
    -   VD:
        -   Gửi Email, Push notification -> có thể thực hiện ở Background
        -   Producer-Consumer, Message Queue (Nhà tiêu thụ người sản xuất)
-   Ưu điểm

    -   Không block Thread -> Tối ưu hiệu xuất, xử lý nhiều request cùng lúc
    -   UI hoặc server không bị đứng khi task mất thời gian
    -   Cho phép scaleứng dụng tốt hơn
        -   Có thể tăng nhiều CORE hơn để các Thread chạy cùng lúc hiệu quả hơn
        -   Tận dụng được phần cứng của máy tính hiện đại

-   Nhược điểm
    -   Logic phức tạp hơn -> cần callback, Future , Promise
    -   Khó debug hơn (task chạy song song, Kết quả trả về sau)
    -   Dễ gặp Race condition, cần đồng bộ hoặc quản lý trạng thái

## Tìm hiểu từ khóa : synchronized trong java

-   từ khóa `synchonized` trong Java, một trong những cơ chế cơ bản đẻ đồng bộ hóa đa luồng

-   **Khái niệm**

    -   `Synchornize` là từ khóa trong Java để `bảo vệ một block hoặc method`, đảm bảo `chỉ một thread được truy cập cùng lúc`
    -   Mục đích là `tránh race condition` khi nhiều Thread cùng đọc ghi các tài nguyên chia sẻ chung

-   **Cách sử dụng**

-   _Đồng bộ method_

```java
class Counter {
    private int count = 0;

    public synchronized void increment() {
        count++;
    }

    public synchronized int getCount() {
        return count;
    }
}
```

-   Khi một thread gọi hàm `getCount()` -> nó sẽ khóa object `Counter`

-   _Đồng bộ block_

```java
class Counter {
    private int count =0;

    public void increment() {
        synchronized(this) {// chỉ khóa Object này
            count++;
        }
    }
}
```

-   Chúng ta có thể chỉ cần khóa một đoạn code. Thay vì cả method sẽ `hiệu quả hơn`

-   _Đồng bộ static method_

```java
class Utils {
    public static synchronized void printMessage(String msg) {
        System.out.println(msg);
    }
}
```

-   Lock áp dụng trên `class Object của Utils.class` không phải instance

-   **Cơ chế hoạt động**

-   Mỗi `Object trong Java có một monitor (lock)`
-   khi thread vào synchronous block/ method -> `giữ monitor`
-   Thread khác muốn vào synchrounous block/method trên `object` đó-> `phải chờ monitor được giải phóng`

-   **Lợi ích**

-   Ngăn `race condition` khi nhiều thread cùng truy cập vào instance
-   Đảm bảo `actomic operation` (tập hợp các bước thực hiện trong cùng một thao tác và ko thread khác có quyền nhảy vào giữa)

-   **Ví dụ thực tế**

-   Cập nhật inventory sản phẩm mua trong e-commerce

```java
class Inventory {
    private int stock = 100;

    public synchronized boolean buyItem(int qty) {
        if(stock >= qty) {
            stock -= qty;
            return true;
        }
        return false;
    }
}
```
