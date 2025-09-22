## ORM là gì ?

-   `ORM (Object-Relational Mapping)` là một kỹ thuật lập trình giúp ánh xạ (map) các đối tượng (objects) trong ngôn ngữ lập trình hướng đối tượng (OOP) với các bảng (tables) trong cơ sở dữ liệu quan hệ (relational database). Về bản chất ORM đóng vai trò là một "cầu nối" giúp lập trình viên thao tác với dữ liệu trong cơ sở dữ liệu như thể đang làm việc với các đối tượng Java, Python, C#... thông thường mà không cần viết các câu lệnh SQL phức tạp.

-   Ví dụ, thay vì viết một câu lệnh SQL như SELECT \* FROM users WHERE id = 123;, bạn có thể sử dụng ORM để thực hiện một lệnh đơn giản như userRepository.findById(123);.

## Sử dụng ORM mang lại lợi ích như thế nào cho ứng dụng.

-   `Tăng tốc độ phát triển`: Lập trình viên không cần phải dành thời gian viết và tối ưu hóa các câu lệnh SQL. Việc thao tác với cơ sở dữ liệu thông qua các đối tượng giúp code trở nên trực quan và dễ đọc hơn, từ đó tăng hiệu suất làm việc.
-   `Giảm thiểu lỗi`: ORM giảm đáng kể lỗi cú pháp SQL và các vấn đề liên quan đến việc khớp dữ liệu giữa ứng dụng và cơ sở dữ liệu. NÓ cũng giúp tránh được các lỗi bảo mật như `SQL injection`

-   `Tính độc lập với CSDL`: Hầu hết các framework ORM đều hỗ trợ nhiều loại CSDL khác nhau (Như mySQL, PostgresQL, Oracle). Ứng dụng của chúng ta có thể chuyenr đổi giữa các hệ CSDL mà không cần thay đổi code tầng bussiness logic, chỉ cần thay đổi cấu hình

-   `4. Hỗ trợ lập trình hướng đối tượng OOP`: ORM cho phép áp dụng nhiều nguyên tắc OOP như kế thừa (inheritance), đa hình `(polymorphism), và đsong gói (encapsulation) vào việc quản lý dữ liệu, điều này rất khó thực hiện với câu SQL thuần

## Cơ chế hoạt động của ORM như thế nào

-   `Metadata`: ORM sử dụng các file cấu hình như XML, hoặc Annotation để xác định cách ánh xạ giữa các lớp (classes) và các trường (fields) với các bảng và cột trong CSDL
-   `Mapping (ánh xạ)`: ORM engine đọc metadata và xây dựng một mô hình ánh xạ, khi một đối tượng được tạo hoặc thay đổi, ORM sẽ dựa vào mô hình này để biết các thuộc tính của đối tượng tương ứng với những cột nào trong bảng

-   `Runtime`: khi ứng dụng cần truy cập vào CSDL (vD `save()`, `find()`) ORM sẽ:
    -   `Tự đọng sinh SQL`: dựa vào yêu cầu của chúng ta. ORM sẽ tự động tạo câu lệnh SQL tương ứng `INSERT` , `SELECT`, `UPDAte`, `DELETE`
    -   `Thực thi SQL`: ORM sử dụng `JDBC`(java). `DB-API` (python) đẻ kết nói và thực thi câu lệnh SQL trên CSDL
    -   `Chuyển đổi dữ liệu`: sau khi nhận kết quả từ CSDL (dạng bảng). ORM sẽ chuyển đổi dữ liệu đó thành các đối tượng tương ứng và trả về cho ứng dụng. QUá tình này được gọi là `hydration` (lấp đày dữ liêu jvafo đối tượng)

## So sánh performance của việc sử dụng ORM vs JDBC

| Tiêu chí         | ORM                                                                                                                                                                            | JDBC                                                                                                                                                                        |
| ---------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Performance      | Thường chậm hơn                                                                                                                                                                | thường nhanh hơn                                                                                                                                                            |
| Lý do            | ORM thêm một tànga trừu tượng để thực hiện ánh xạ và tự động sinh SQL. Quá trình này tiêu tốn CPU và RAM. Câu lệnh SQL được sinh tự động có thể không phải lúc nào cũng tối ưu | JDBC yêu cầu lập trình viên viết SQL thuần cho phép kiểm soát hoàn toàn các câu lệnh và tối ưu hóa chúng cho từng trường hợp cụ thể. Không có chi phí xử lý metadata ánh xạ |
| Độ linh hoạt     | Kém linh hoạt hơn. Khó khăn khi cần viết các câu lệnh SQL phức tạp. tối ưu hóa sâu hoặc truy vấn các procedure                                                                 | Rất linh hoạt. Cho phép chúng ta viết và thực thi bất kì câu lệnh SQL nào                                                                                                   |
| Ứng dụng thực tế | Phù hợp với các ứng dụng  có nhiều nghiệp vụ phức tạp, đòi hỏi tốc độ phát triển nhanh và dễ bảo trì                                                                           | Phù hợp với các ứng dụng cần hiệu năng cao nhất, xử lý dữ liệu lớn (big data) hoặc các tác vụ batch processing |

- `Kết luận`: 
    - `ORM` rất mạnh mẽ và tiện dụng cho các ứng dụng nhiều tác vụ CRUD  và yêu cầu tốc độ phát triển nhanh
    - `JDBC` lựa chọn hàng đầu khi hiệu năng là yếu tố quan trọng nhất và ta cần kiểm soát hoàn toàn các truy vấn SQL

- Thực tế. nhiều dự án lớn sử dụng cả 2: `ORM` cho phần lớn các tác vụ CRUD thông thường và `JDBC` hoặc truy vấn SQL thuần phục vụ cho các tác vụ cần hiệu năng cực cao

## Tầng ứng dụng

- Làm việc thông qua các Entity. Nó mapping 1-1 với bảng của CSDL. Một class , @Entity @Table

- @Id: giống bảng có một primary key . Đặt là kiểu số kiểu UUID hay ji cung đều được cả

- @Column: tên trường trong DB mà kết hợp nhiều chữ khác nhau. TỨc là kiểu Snake 

- Thay vì làm việc với DB thì làm việc với Object. Ko quan tâm là DB là có cái gì nó như thế nào 

- Ưu điểm: 
- `Tăng tốc độ phát triển`: ORM cho phép lập trình viên thao tác với cơ sở dữ liệu bằng các đối tượng và phương thức của ngôn ngữ lập trình (ví dụ: Python, Java, C#), thay vì viết các câu lệnh SQL. Điều này giúp giảm đáng kể thời gian viết code và xử lý các thao tác cơ bản như thêm, sửa, xóa, truy vấn dữ liệu. ⏱️

-` Hạn chế lỗi SQL Injection`: ORM tự động xử lý việc escape các giá trị đầu vào, giúp ngăn chặn các cuộc tấn công SQL Injection một cách hiệu quả. Đây là một trong những ưu điểm bảo mật nổi bật nhất của ORM.

- `Đơn giản hóa việc truy vấn`: Với ORM, bạn có thể thực hiện các truy vấn phức tạp bằng cách sử dụng cú pháp dễ đọc, gần gũi với ngôn ngữ lập trình. Thay vì phải viết các câu lệnh JOIN phức tạp trong SQL, bạn chỉ cần sử dụng các thuộc tính của đối tượng.

- `Tăng tính độc lập` của ứng dụng với cơ sở dữ liệu: ORM tạo ra một lớp trừu tượng giữa ứng dụng và hệ quản trị cơ sở dữ liệu (DBMS). Điều này có nghĩa là bạn có thể dễ dàng chuyển đổi từ một loại cơ sở dữ liệu này sang loại khác (ví dụ: từ MySQL sang PostgreSQL hoặc SQLite) mà không cần phải viết lại toàn bộ các câu lệnh SQL.

- `Dễ dàng bảo trì code`: Code sử dụng ORM thường dễ đọc và dễ bảo trì hơn so với code chứa nhiều câu lệnh SQL trực tiếp. Khi có thay đổi trong cấu trúc cơ sở dữ liệu, việc cập nhật code ORM thường đơn giản hơn.
- Nhược điểm: 
- `Hiệu năng`: ORM có thể tạo ra các câu lệnh SQL không tối ưu, đặc biệt đối với các truy vấn phức tạp hoặc khi thao tác với lượng dữ liệu lớn. Việc này có thể dẫn đến hiệu suất thấp hơn so với việc viết các câu lệnh SQL "thuần" được tối ưu hóa. 🐢

- `Khó kiểm soát truy vấn`: Lập trình viên ít có quyền kiểm soát trực tiếp đối với các câu lệnh SQL được tạo ra bởi ORM. Điều này có thể gây khó khăn trong việc debug hoặc tối ưu hóa hiệu năng khi cần thiết.

- `Độ phức tạp ban đầu`: Để sử dụng ORM hiệu quả, lập trình viên cần phải học cú pháp và các quy tắc của framework ORM cụ thể (ví dụ: SQLAlchemy trong Python, Hibernate trong Java). Điều này có thể tốn thời gian học hỏi ban đầu.

- `Overhead`: Việc chuyển đổi giữa đối tượng trong ngôn ngữ lập trình và các hàng trong cơ sở dữ liệu có thể tạo ra một "gánh nặng" (overhead), ảnh hưởng nhỏ đến hiệu suất, đặc biệt trong các ứng dụng cần tốc độ xử lý cực cao.