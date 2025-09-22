## Cơ chế xử lý transaction

- các hệ quản trị cơ sử dữ liệu (DBMS) sử dụng nhiều kĩ thuật khác nhau để đảm bảo tính chất ACID. hai cơ chế phổ biến nhất là 

- `1. Locking (khóa)`

- Cơ chế này sử dụng các khóa `locks` để kiểm soát quyền truy cập của các transaction đồng thời vào cùng một dữ liệu
    - `Khóa chia sẻ (Shared Lock)`: cho phép nhiều transaction đọc cùng một dữ liệu
    - `Khóa độc quyền (exclusive Lock)`: chỉ cho phép một transaction duy nhất được phép ghi (thay đổi) dữ liệu. Trong khi đó, không transaction nào khác có thể đọc haowjc ghi dữ liệu đó

- Khi một transaction cần truy cập dữ liệu, nó sẽ yêu cầu một loại khóa phù hợp. nếu dữ liệu đã bị khóa bởi một transaction khác, nó phải đợi đến khi khóa được giải phóng. Có chế khóa này giúp `duy trì tính cô lập`. nhưng cũng dẫn đến một vấn đề nghiêm trọng gọi là `DeadLock`
    - `DeadLock (tắc nghẽn)`: xảy ra khi 2 hoặc nhiều transaction cugnf chờ nhau để giải phóng tài nguyên. VD transaction A đang giữ khóa dữ liệu X và cần khóa Y. cùng lúc đó transaction B đang giữ khóa Y và cần khóa X. Cả 2 sẽ chờ nhau mãi mãi. Các DBMS hiện đai có cơ chế phát hiện và giải quyết DEADLOCK bằng cách hủy bỏ một trong các transaction

## 2. Multiversion COncurrency control (MVCC)

- MVCC là một cơ chế quản lý đồng thời tiên tiến hơn, được sử dụng  trong các hệ thống database hiện đại như PostgreSQL và Oracle thay vì dùng khóa để ngăn chặn truy cập, MVCC tạo ra nhiều `phiên bản (verions)` của cùng một hagnf dữ liệu
    - khi một transaction đọc dữ liệu, nó sẽ đọc một phiên bản snapshot (bản chụp) tại thời điểm transaction đó bắt đầu
    - Khi một transaction ghi dữ liệu, nó sẽ tạo ra một phiên bả nmoiws của hàng đó thay vì sửa trực tiếp vào hagnf cũ
    - Các transaction khác vẫn có thể tiếp tục đọc phiên phiên bản cũ mà không bị chặn

- MVCC giúp cải thiện hiệu suất vì nó giảm thiểu sự không cần thiết của khóa, cho phép các thao tác đọc và ghi diễn ra đồng thời không bị tắc nghẽn. Nó đặc biệt hữu ích trong các hệ thống có tần xuất đọc cao

- 