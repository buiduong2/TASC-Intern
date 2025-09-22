## Spring Framework

## 1. Introduction

## 1. Giới thiệu Spring Framework: SpringMVC, SpringBoot

-   **Spring framework**
-   là một `mã nguồn mở`, dùng để phát triển ứng dụng Java
-   Cung cấp nền tảng toàn diện để xây dựng ứng dụng java Enterprise (ứng dụng web, dịch vụ REST, microservice)
-   Điểm mạnh của spring
    -   `IoC (inversion of Control) / DI (dependency injection)`: quản lý
    -   `AOP  (Aspect Oriented Programming)`: hỗ trợ lập trình hướng khía cạnh (logging. bảo mật, transaction)
-   **SpringMVC**

-   là một module trong Spring. chuyên dùng để phát triển `ứng dụng web theo mô hình MVC`
- Cấu trúc: 
    - `Model`: dữ liệu và logic nghiệp vụ
    - `View` giao diện người dùng (JSP, Thymeleaf , FreeMaker)
    - `Controller`: nhận request từ client, gọi service/DAO trả về response

- Cơ chế hoạt động
    - Người dùng gửi request -> Dispatcher Servlet (Trung tâm của Spring MVC)
    - DispatcherSerlet chuyển đến `Controller` thích hợp
    - Controller xử lý gọi `Service/DAO` trả về kết quả `model`
    - Trả dữ liệu đến `View Resolver`  để render ra trang View
    - Gửi response về cho Client

- **Spring Boot**
- Là một dự án mở rộng của Spring, ra đời để `đơn giản hóa việc phát triển ứng dụng Spring`
- Đặc điểm nổi bật: 
    - `Auto-Configuration`: tự động cấu hình dựa trên thư viện có trong classpath
    - `Embedded Server`: tính hợp sẵn server Tomcat Jetty , không cần deploy file War thủ công
    - `Starter Dependencies`:  cung cấp các gói phụ thuộc (starter) cho từng mục đích (Springboot starter - web, spring-boot-starter-jpa-data, spring-boot-starter-security)
    - `Actuator`: cung cấp endpoint quản lý, giám sát hệ thống (metrics, health check)
    - Tích hợp sẵn với `Microservice` (Sprign Cloud)

- Ưu điểm
    - Giảm cấu hình XML phức tạp
    - Dễ chạy chỉ cần (javar -jar app.jar)
    - Phù hợp phát triển nhanh (Rapid APplication Development) và Microservices

- **Tóm lại**
    - Spring Framework: nền tảng gốc, cung cấp IoC, AOP, Data Access, MVC
    - Spring MVC: module Spring để phát triển ứng dụng web theo mô hình MVC
    - Spring Boot: giúp xây dựng ứng dụng SPring nhanh chóng, ít cấu hình, hỗ trợ microservice

## AOP

- Cho phép chia cắt các mối quan tâm bằngcách thêm các hành vi , khía cạnh để code của ứng dụng chỉ quan tâm đến bản thân các môi lo lắng của nó (VD bussiness thì ko quan tâm, Validate, hay Log v..v..)