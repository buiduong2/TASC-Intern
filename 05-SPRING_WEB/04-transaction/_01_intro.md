## Transaction là gì?

- Trong Database ,một `transaction (giao dịch)` là một chuỗi các thao tác trên cơ sở dữ liệu được thực hiện như một `đơn vị công việc logic duy nhất và không thể chi nhỏ`. Điều này có nghĩa là: 
    - Tất cả các thao tác trong transaction phải `thành công` hoặc `thất bại hoàn toàn`

    - Không có trạng thái "thực hiện một phần". nếu một thao tác bất kì thât bại, toàn bộ transaction sẽ được hủy bỏ, và cơ sở duy liệu được khôi phục về trạng thái ban đầu. Quá trình này gọi là `rollback`

- Mục đích chính của transaction là đmảb ảo `tính toàn vẹn và nhất quán` của dữ liệu, đặc biệt trong các hệ thống người dùng truy cập và thay đổi dữ liệu cùng lúc

## Tính chất của Transaction ACID 

- Để đảm bảo độ tin cậy , các transaction trong database phải tuân thủ bốn tính chất cơ bản được viết tắt là `ACID`
    - `A- Atomicity`  (tính nguyên tử): như đã giải thích ở trên, transaction là một đơn vị không thể chia nhỏ. `Tất cả hoặc không có gì cả` (All or nothing) . nếu một phần của transaction thất bại toàn bộ sẽ bị hủy

    - `C - Consistency (Tính nahast quán)`  : transaction chỉ có thể chuyển đổi cơ sở dữ liệu từ một trạng thái hợp lệ này sang một trạng thái hợp lệ khác. nó đảm bảo các quy tắc (constraints) của  cơ sở dữ liệu luôn được duy trì. Ví dụ nếu ta có ràng buộc là số dư tài khoản không thể âm, thì một transaction cố gắng làm cho số dư âm sẽ bị từ chối

    - `I - Isolation (Tính cô lập)`: Khi nhiều transaction chạy đồng thời, chúng không được can thiệp vào nhau. Mỗi transaction phải hoạt động độc lập, Và những thay đổi của nó chỉ hiển thị với các transaction khác nhau khi nó đã hoàn tất

    - `D - Durability (tính bền vững)`: sau khi một transaction đã được `commit` (lưu lại), những thay đổi của nó sẽ tồn tại vĩnh viễn trong CSDL, ngay cả khi hệ thống gặp sự cố như mất điện

- Tóm lại: transaction là `Lời cam kết` của hệ thống database rằng một nhóm các thao tác sẽ được thực hiện một cách an toàn, tin cậy , và không làm ảnh hưởng xấu đến dữ liệu. Đó là lý do nó đống vai trò cốt lõi trong các hệ thống tài chính, thương mại điện tử, và mọi ứng dụng đòi hỏi độ chính xác cao về dữ liệu


- Transaction là một khái niệm nền tàng trong Database, giúp đmả bảo dữ liệu luôn chính xác và đáng tin cậy. Việc hiểu các tính chất `ACID` các mức cô lập và cơ chế quản lý như `locking` hay `MVCC` sẽ giúp chúng ta thiết kế và phát triển các ứng dụng của hiệu suất cao và an toàn hơn