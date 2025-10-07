## Caching

-   REDIS : inmeory Cache

-   KEy value- Gần giống Map trong Java

-   Mạnh mẽ hơn rất nhiều

-   Hiệu năng lấy xuống rất nhanh. Tính bằng milisecond s. Cực kì giây

-   Sinh ra Redis tối ưu hiệu năng rất là cao

-   2 loại redis

    -   non-clusted Redis
    -   Cluster Redis

## Stand Alone Classesr

-   các API dại diện cho nghiệp vụ

-   Thay vì làm vc với CSDL thì làm việc với Reid

-   Khi dùng Cache Redis thì người ta sẽ dùng cho GET

-   Các method khác như PUT POST , DELETE vẫn phải alfm việc với CSDL

-   Cache để load dữ liệu ra cho nó nhanh chứ ko lưu DAta vào đây

-   Một vài API ko phải toàn bộ hệ thống

-   DB vẫn là cái gốc của ứng dụng

-   Redis chỉ là hỗ trợ tăng hiệu năng

-   Mục đích là tăng performance

-   Khi update thì update vào Redis

-   Tránh việc dữ liệu của CSDL leehcj so với REdis

-   Dữ liệu trên redis tương như dữ liêu jtrong CSDL

-   Thawgnf Stand Alone . Nếu nhiều service đều chọc vào mà nó bị chết thì ko lấy được dữ liệu ra

-   Làm tăng hiệu nawgn quản lý ứng dụng ngon .n hưng có 1 thằng thì quá

-   Người ta tạo ra Master Slave. Master thì ghi vào

-   Slave :

    -   Một thằng backup cho Master

    -   Nhiệm vụ Master bị oẳng thì Slave sẽ thay thế.

    -   Đảm bảo tính HA (HIght avaiable)

-   Load Balancing - Master Slave

    -   Load Balancing : chia tải cho 3 con khác nhau chạy

    -   master Slave: chạy 1 chỗ ko vào Slave.

-   2 con này đặt ở 2 IP khác nhau

-   Mục đích của Redis

    -   Tăng hiệu năng của mình khi mình lấy thông tin:

        -   Tất cả thông tin để cập nhật , xóa thêm , phải fetch ra từ CSDL

        -   Đôi khi bị invalidate cache. Do thông tin cũ

    -   Kiến trúc:

        -   Key-value - cache . Key luôn luôn là một String.
        -   Value: rất nhiều loại: SET, LIST, String, OBject, isntance

    -   List: dùng như Queue để giao tiếp giữa các service khác nhau cũng được
        -   Muốn lấy FIFO. FILO cũng được

-   Khi làm việc với Redis thì có RedisTemplate. Là một Client để giao tiếp giữa ác Service với nhau

## Redis Cluster

-   Khi phân vùng Cluster thì nó lưu giữ liệu như nào

    -   hơn 1000 slot. (khác với key) (1 slot chứa n key). Chia đều giữa các node trong cụm .

    -   Nó có cơ chế tính key nằm ở slot nào (như là một load balance). Chứ ko đây replicate cả 3 thằng

-   Lúc đi tìm thì đi tìm slot nào:
-   Senitive

    -   Giữ liệu vừa phải tương đối

    -   trong một hệ thống lớn yêu cầu Caching cao thì chia theo dạng Cluster

    -   Mục đích: tăng hiệu năng tìm kiếm

    -   Chia tải cho hệ thống redis

-   Khi nào dữ liệu lớn phân tải cao thì làm đến Cluster

-   Cluster: càng chia nhỏ thì ứng dụng càng rối

-   Scale lên ko vấn đề gì cả .

-   Backend failover -> master bj down thì có th

-   VỀ dọc thêm

    -   NOde Slave ko nằm trên primary. mà nằm ở thằng khác.

    -   Secondary chéo nhau thì slave bên kia trở thành master

## cách tăng hiệu năng

-   Multi Thread

-   index, Partition,

-   Cache

###

-   Khi sử dụng ko bó buộc bất cứ hoàn cảnh nào. Kết hợp toàn bộ. dùng hết tất cả cái có thể nhanh. Xem tối ưu bao nhiêu lần
-
## VỀ nhà implement caching với hệ thống ProductDetail


### Hôm sau học về kafka

- 