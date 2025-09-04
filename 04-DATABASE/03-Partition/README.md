## Introduction

## 1. Partition là gì ?

-   `Parition` (phân vùng) là cách `chia một bảng lớn thành nhiều phần nhỏ hơn`. Nhưng khi chúng ta truy vấn nó vẫn nhìn như `một bảng duy nhất`

    -   Cũng là một cách để tối ưu truy vấn

    -   Giảm phạm vi tìm kiếm

-   `Khác với index`

    -   Index chỉ giúp tìm nhanh các dòng trong một bảng lớn
    -   Partition là `chia nhỏ vật lý dữ liệu trên ổ dĩa`. Mỗi phần gọi là partition. Nhưng dụng vẫn thấy đây là một bảng
    -   Tăng tốc tìm kiếm trong phạm vi

-   VD: Khi chúng ta phân vùng. Thì khi tìm kiếm dữ liệu ta chỉ cần tìm các vùng xác định mà ko cần loop qua toàn bộ DB

## 1.2 Cơ chế

-   Mỗi Partition là một "bảng con" vật lý

    -   Partition chỉ "chia nhỏ tập dữ liệu gốc thành nhiều phần"
    -   Trong mỗi Partition, các bản ghi `ko nhất thiết nầm sát nhau` về giá trị sắp xếp (trừ khi chúng ta đánh index)

    -   Tách file riêng ("Bảng con vật lý")
    -

## 2. Lợi ích của Partition

-   Tăng tốc độ truy vấn (**Partition Pruning**)

    -   Khi chúng ta lọc theo điều kiện partition (VD ngày tháng, Khu vực, loại sản phẩm database) chỉ quét đúng `partition cần thiết`, thay vì quyets toàn bảng

-   Dễ dàng bảo trì, xóa dữ liệu cũ

    -   Không cần phải `DELETE` từng hàng tốn thời gian
    -   Ta có thể `DROP PARTITION` các tháng ko cần thiết nữa (cực nhah, như xóa hẳn một file)

-   Quản lý dữ liệu lớn (qunar lý file vật lý)

    -   Mỗi Partition có thể đặt trên ổ đĩa khác nhau (với một số hệ quản trị)
    -   Giúp phan tán IO cân bằng taỉ

-   Tăng tốc thao tác ghi (INSERT / LOAD)

    -   Nếu INSERT đúng phân vùng, DB chỉ cập nhật file nhỏ, không kiểm tra toàn bộ

-   Dễ archive/backup từng phần

    -   Có thể backup từng Partition, phục hồi từng phần thuận tiện hơn

-   _Tóm tắt_
    -   Chia nhỏ bảng lớn, xử lý từng phần
    -   Lợi ích: Truy vấn nhanh hơn, xóa nhanh,backup dễ, giảm áp lực bảo trì

## So sánh với Page

-   `Page` (hay còn gọi là `BLock`) `đơn vị lưu trữ nhỏ nhất` trong database. Thường là 8KB `PostgreSQL`
-   tất cả dữ liệu của bảng/ index đều được lưu thành các page trên ổ đĩa

-   Khi truy vấn database `đọc/ghi từng page một` không phải từng dòng riêng lẻ

-   Partition

    -   Là `cách phân chia bảng thành nhiều phần`. Mỗi phần chứa rất nhiều Page
    -   Mỗi Parittiion bản chất cũng là một bảng con. Bên trong nó vẫn lưu được các Page

-   page Đơn vị nhỏ nhất DB (DB tự quản lý)

-   Partition: Đơn vị logic lớn (người dùng tạo/ thiết kế chia nhỏ bảng)

## Các kiểu Partition cơ bản

-   Có 3 kiểu Partition phổ biến nhất

    -   `Range Partitioning` (phân theo khoảng giá trị)
    -   `List Partitioning` (phân theo danh sách giá trị)
    -   `Hash Partitioning` Phân theo vùng giá trị băm / ngẫu nhiên

-   Ngoài ra còn có `Composite Partitioning` (kết hợp nhiều kiểu)

-   `range` :

    -   VD chia theo ngày tháng
    -   P1: Từ 2020-01-01 -> 2020-12-31
    -   P2: Từ 2021-01-01 -> 2021-12-31
    -   `1` Cột duy nhất

-   `List`

    -   Chia theo giá trị cụ thể
        -   Partition 1: tất cả có dòng `region = 'Hanoi' `
        -   Partition : tất cả có dòng `region = 'Saigon'`
        -   (1 cột hoặc n cột)

-   `Hash`

    -   Chia ngẫu nhiên để cân bằng, dưa vào giá trị hash
    -   p0: hash(user_id) % 4 = 0
    -   p1: hash(user_id) % 4 = 1
    -   p2: hash(user_id) % 4 = 2
    -   p3: hash(user_id) % 4 = 3
    -   p4: hash(user_id) % 4 = 4
    -   (1 cột hoặc n cột . Tổng hợp bằng cách Hash lại)

-   `composite partition`
    -   Nhiều lớp PARTITION với nhau (range xong bên trong lại chia LIST)

## Điều kiện để Paritition Pruning hoạt động

-   Truy vấn phải `có điều kiện WHERE khớp với cột PARTITION` (VD `WHERE created at =` ) nếu parition theo created_at
-   Nếu điều kiện ko khớp (VD chỉ WHERE theo `customer_id`) mà PARITITON theo year `database sẽ phải kiểm tra tất cả các PARTITION`

## Khi nào nên dùng

-   Bảng rất lớn
    -   Truy vấn hiện tại chậm
-   Truy vấn thường xuyên theo vùng giá trị
    -   Lọc theo trường nhất định
    -   truy vấn theo từng vùng nhiều ít khi cần truy vấn toàn bảng
-   Xóa hoặc Archive dữ liệu cũ theo vùng dễ dàng
    -   DROP PARTITIOn
-   QUản lý bảo trì tốt hơn
    -   Phân dữ liệu
    -   Backup/restore linh hoạt

## Khi nào không nên dùng

-   Bảng quá nhỏ

    -   Phức tạp hơn, lợi ích gần như không có

-   truy vấn ko thường xuyên lọc theo cột PARTITION

    -   cột khác coojjt PARTITION thì phải `quyets tất cả PARITION` (mở nhiều file nhỏ merge KQ)

-   Số lượng PARTITION quá nhiều (chia từng giờ..., `quá tải metadata`)
-   Khi có quá nhiều loại INDEX phức tạp, quản lý index trên nhiều PARTITION trở nên khó kiểm soát
-   Thao tác GHi đồng thời vào nhiều PARTITION khác nhau có thể gây DEAD LOCK

## Câu hỏi thêm

-   **Việc chia vùng này có sắp xếp lại bộ nhớ không**

    -   Đơn thuần là chia nhỏ dữ liệu thành từng bảng con `Không tự động sắp xếp dữ liệu vật lý` trong mỗi Parition
    -   `Clustered Index` mới là thứ quyết định `trật tự vật lý`

-   Tóm lại:

    -   Có phân chia sắp xếp vào các rỏ hàng. Nhưng các sản phẩm bên trong giữ nguyên
    -   Nếu chúng ta muốn sắp xếp trong từng Partition được `sắp xếp vật lý` ta cần thêm CLUSTERED INDEX

-   **INdex trong từng Partition**

    -   Có thể tạo trên từng. hoặc toàn bộ
    -   Chỉ có tác dụng trong từng partition
    -

-   **Vậy tôi có quyết định cách chia vùng ngoài kiểu chia vùng không**

    -

-   **Khi tạo một Partition có phá vỡ quy luật index cũ không**

-   1. Khi ta tạo Partition cho một bảng đã có index (hoặc clustereed index)

    -   các index cũ sẽ không còn tác dụng trực tiếp lên bảng cha nữa
    -   Khi partition , bảng cha chỉ con là `một khung logic`. Còn dữ liệu thật chia ra các `bảng con vật lý` (PARTITION)
    -   Index sẽ `cần tạo lại trên từng partition` (hoặc tạo mới)

-   Clustered Index:

    -   Khi chia partition các `dòng vật lý sẽ bị chia tách thành nhiều file nhỏ`
    -   Muốn sắp xếp vật lý trong từng phần Partition `phải tạo clusterred index riêng cho từng partition`

-   3. Tổng kết

    -   Tạo partition = phá vỡ (reset) toàn bộ index cũ
    -   Clustered index cũng sẽ bị “chia tách”
    -   Việc truy vấn sẽ do database tối ưu, gom kết quả từ các partition (giống microservice trả về dữ liệu rồi merge lại)

## Thực hành

## Tạo bảng với việc chia PARTITION

-   Khi chúng ta sử dụng RANGE.
-   trong `mySQL` thì việc chia phân vùng bắt buộc cột đó phải nằm trong các danh sách các PRIMARY KEY , hoặc nó phải là PRIMARY KEY

-   `MySQL`

    -   MySQL sẽ dùng chia nhỏ vật lý dữ liệu thành các vùng. Để dảm bảo một hàng luôn xác định được nằm ở partition nào, và ko trùng lặp
        -   nó bắt buộc phải `PRIMARY KEY phải đủ theo thông tin phân vùng`
        -   Luôn xác định tính duy nhất PARTITION chứa một hàng theo key
        -   Dễ kiểm soát và kiểm tra tính toàn vẹn dữ liệu

- `Các CSDL khác`
    - Chỉ yêu cầu phải chứa đủ UNIQUE Key mà thôi

- Vậy MySQL sẽ thay đổi logic của hệ thống. Nhưng nó ổn định và đoán trước


-   VD nếu ko có quản lý đó:

    -   1. Thường chia vật lý dữ liệu

        -   Nếu PRIMARY KEY chỉ có `id`, mà partition lại chia theo `created_at` lúc kiểm tra một dòng với id cụ thể. (MySQL) ko biết chắc chắn dòng đó nằm ở PARTITION nào (vì id không chứa thông tin YEAR)
        -   MySQL muốn mọi thao tác theo PRIMARY KEY đều xác định `chính xác một PARTITION` -> nên cần tát cả các cột liên quan PARTITION phải nằm trong PRIMARY KEY

    -   2. TRánh lỗi khi insert hoặc tìm kiếm
        -   Nếu chúng ta có 2 dòng cùng `id` (VD khôi phục, import hoặc thao tác đặc biệt) nhưng khác `created_at`. không thể đảm bảo tính toàn vẹn phân vùng chính xác
        -   Vì mỗi PARITION quản lý như một DATABASE độc lập. ID của mỗi pARTITION có thể trùng nhau nếu ko hợp lý
