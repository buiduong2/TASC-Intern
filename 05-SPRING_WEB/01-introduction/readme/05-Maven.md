## Maven

Apache maven là một chương trình quản lý dự án cho phép các developers có thể quản lý về version, các dependencies (các component, thư viện sử dụng trong dự án) , quản lý build, tự động download javadoc & source, ….

-   Dependency management: quản lý thư viện, version, và các dependency truyền qua (transitive dependencies -Nếu A cần thư viện B. Khi kéo A kéo cả B) .
-   Build automation: tự động hóa compile, test, package, install, deploy.
-   Convention over configuration: chuẩn hóa cấu trúc dự án và quy trình build, giảm nhu cầu cấu hình thủ công.


- Khi build
    - tất cả .jar dependencies.
    - Tự viết lệnh compile dài dòng với -cp để include thư viện
    - Tự viết script để chạy test, tạo jar.

-

## cách sử dụng



-   **Quản lý thư viện**
    -   Khai báo `dependencies` trong file `pom.xml` với groupId, artifactId, version.
    -   Maven sẽ tải thư viện từ remote repository (mặc định là Maven Central) về local repository (~/.m2/repository).
    -   Maven xử lý luôn transitive dependencies (tự động thêm thư viện mà dependency của bạn cần).

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
  <version>3.0.0</version>
</dependency>
```

-   **Build project (biên dịch và đóng gói)**

-   Có thể gọi các lệnh để chạy các thứ trong dự án

    -   mvn validate → kiểm tra tính hợp lệ của dự án (biên dịch ko gặp lỗi)
    -   mvn compile → biên dịch mã nguồn.
    -   mvn test → chạy unit test.
    -   mvn package → đóng gói ứng dụng (.jar hoặc .war).
    -   mvn install → cài package vào local repository.
    -   mvn deploy → đẩy package lên remote repository (chia sẻ cho team).

-   **Plugin**
-   Maven không tự thực hiện công việc; nó ủy quyền cho plugin.
    - maven-compiler-plugin để compile code.
    - maven-surefire-plugin để chạy test.
    - maven-jar-plugin để tạo file .jar.

- **Hỗ trợ đa dự án**
    - Một dự án lớn có thể gồm nhiều module.
    - Maven hỗ trợ parent POM và child module POMs.
    - Cách dùng: định nghĩa pom.xml gốc với <modules>, các module con sẽ kế thừa cấu hình từ parent.

- **Sử dụng build theo profile**
- Khi ta build có thể chọn các profile tương ứng với cái ta định nghĩa trong `pom.xml`

