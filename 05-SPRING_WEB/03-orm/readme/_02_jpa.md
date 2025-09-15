## Spring JPA có phải là 1 triển khai của ORM hay không ?

- Spring JPA không phải là một triển khai của `ORM`. Thay vào đó nó là một `API` (Application programing interface) cung cấp một bộ các tiêu chuẩn và quy ước để làm việc với ORM framework. Spring DataJpa là một module của SPring. giúp chung ta dễ dàng sử dụng JPA 

- Phân biệt JPA và ORM

- **JPA**

- `JPA` là một `đặc tả (specification)` một tập hợp các quy tắc và tiêu chuẩn do cộng đồng java đưa ra. NÓ định nghĩa các đối tượng Java được ánh xạ đến CSDL quan hệ. Cung cấp một API Chuẩn để tương tác với CSDL
- `JPA không có code để thực thi`: nó chỉ là một bản thiết kế cho các ORM framework tuân theo

- **ORM**

- `ORM` là `cơ chế thực thi của ` JPA. Các framework OMR như là `Hibernate` `EclipseLink` và `OpenJPA` là những công cụ cụ thể để thực thể hóa đặc tả JPA
- Chúng ta ko thể sử dụng JPA một mình. chúng ta luôn cần một ORM provider để triển khai nó

- **Spring Data Jpa**

- `Spring Data JPa` là một `module` trong hệ sinh thái SPring. Nó được xây dựng dựa trên JPA và giúp việc sử dụng JPA trở lên dễ dàng hơn rất nhiều
- Nó không phải là một ORM provider, mà một `abstraction layer`( tầng trừu tượng). Spring Data JPA tự động sinh ra các câu lệnh truy vấn từ trên phuhương thức của chúng ta, giúp giảm đáng kể lượng code lặp lại đi lặp lại
- Để trong Spring DataJPA hoạt động. ta vẫn cần phải khai báo ORM provider (VD hibernate) trong `pom.xml`

- Tóm lại mối quan hệ là: 
    - `JPA` (specification - đặc tả)
    - `Hibernate` (implementation - triển khai)
    - `Spring Data JPA` (lớp đơn giản hóa)

- Spring data JPA giúp chúng ta sử dụng JPA một cách iheeuj quả , nhưng bản thân nó không phải là ORM. nó cần một ORM provider như hibernate để thực hiện các ánh xạ và tương tác với CSDL (JPA ko phải là thứ thực thi. Nó phân tích mọi thứ AOP rồi ủy quyền cho ORM thực thi các câu lệnh - Nó trừu tượng hóa che giấu sự phức tạp của việc sử dụng JPA và ORM provider)