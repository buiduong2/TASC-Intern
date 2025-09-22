## Các điều bất thường Anomalies trong database

## 1. Dirty Read( đọc dữ liệu bản)

-   Điều gì xảy ra :
    -   Một transaction đọc dữ liệu từ một transaction khác chưa được `commit `
-   tại sao nó lại là vấn đề ? :
    -   Transasction đầu tiên sau đó bị `rollback`. Dữ liệu mà transaction đọc được không bao giờ tồn tại trong cSDL 

- VD: 
    - TA bắt đầu, cập nhật số dư tài khoản từ 100 thành 50
    - Transaction B đọc số dư mới là 50
    - Transaction A gặp lỗi là `rollback`. Số dư tài khoản trở lại 100
    - Transaction B đã đọc phải giá trị sai (50)

## 2. Non-Repeatable REad (đọc không nhất quán)
- Điều gì xảy ra: 
    - Một transasction đọc cùng một hagnf dữ liệu hai lần, nhưng giá trị đã thay đổi giữa 2 lần đọc

- Tại sao nó là vấn đề: 
    - Điều này làm hỏng tính nhất quán của một transactoin

- VD: 
    - Transaciton A đọc số dư tài khoản là 100
    - Transaction B cập nhật và `commit` số dư thành 50
    - Transaction A đọc lại số dư và thấy nó là 50. Giá trị đã thay đổi trogn khi transaction A vẫn đang chạy

## Phantom reads (đọc dữ liệu ma)

- Điều gì xảy ra ?
    - Một transaction đọc lạp tập hợp các hàng dữ liệu (VD bằng câu lệnh SELECT .... WHERE ...) và thấy có thêm các hàng mới

- Tại sao nó là vấn đề 
    - Tương tự như non-repeatable read. nó phá vỡ tính nhất quán trong cùng một transaction, nhưng ở mức độ tập hợp hàng

- VD: 
    - Transaction A đếm tất cả các đơn hagnf chưa thanh toán thây có 5 đơn hàng
    - Transaction B thêm một đơn hàng mới và `commit`
    - Transaction A đếm lại và thấy 6 đơn hàng. Hàng thứ 6 xuất hiện như một con ma ma transaction A không nhìn thấy từ lúc đầu

## Mối liên hệ với các mức cô lập

- Các mức độ cô lập mà chúng ta đã nói đến trước đó được thiết kế để ngăn chặn những vấn đề này
    - `READ UNCOMMITED` : không ngăn chặn bất kì vấn đề nào
    - `READ COMMITED`: ngăn chặn `dirtyReads`
    - `READREPEABLE READ` : ngăn chặn `dirty Reads` và `non-repeatable reads`
    - SERIALIZABLE : ngăn chặn cả 3 vấn đề trên


## Branch

- Distributed Transactions (Giao dịch phân tán)

- Database Isolation Protocols (Các giao thức cô lập):

- Concurrency Control (Kiểm soát đồng thời):