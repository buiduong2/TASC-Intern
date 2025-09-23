## Đóng gói  1 Image cho dự án Springboot

- Có 2 phương pháp : Tạo một Dockerfile thủ công hoặc sử dụng `Jib` tự động


## Phương pháp 1: Sử dụng Dockerfile (truyền thống)

- Đây là cách phổ biến nhất và cho phếp chúng ta tùy chỉnh chi tiết quá trình đóng gói. Chúng ta cần tạo một file có tên là `Dockerfile` trong thư mục gốc của dự án

### B1. Build file JAR

- Trước tiên, chúng ta cần build dự án SPringBoot để tạo ra file JAR có thể chạy được. Mở terminal và chạy lệnh
```sh
mvn clean package
```

### B2: Tạo Dockerfile

- Tạo 1 file có tên là `Dockerfile` trong thư mục gốc của dự án và có thêm nội dung sau: 

```Dockerfile
# Sử dụng Iamge chính thức của OpenJDK làm nền tảng
FROM openjdk:17-jdk-slim

# Thiết lập biến môi trường để định vị file JAR
ARG JAR_FILE=target/your-app-0.0.1-SNAPSHOT.jar

# Sao chép file JAR từ máy chủ vào Iamge
COPY ${JAR_FILE} app.jar

# Lệnh để chạy ứng dụng khi container khởi động
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

- Giải thích
    - `FROM openjdk:17-dk-slim`: sử dụng một Image Java nhẹ và an toàn làm nền tảng
    - `ARG JAR_FILE`: Khai báo một biến để tham chiếu đến file JAR đã build
    - `COPY ${JAR_FILE} app.jar`: sao chép JAR đã build vào image và đổi tên thành `app.jar` cho tiện lợi
    - `ENTRYPOINT ["java", "-jar" , "/app.jar"]` Lệnh sẽ được thực thi để khởi động ứng dụng Java

### B3: buil và chạy Docker Image

- 1. Build Image

```sh
docker build -t your-spring-boot-app .
```
- `-t your-spring-boot-app`:  Đặt tên (tag) cho Image
- `.` : chỉ định docker file nằm trong thư mục hiện tại

- 2. Chạy container

```sh
docker run -p 8080:8080 your-spring-boot-app
```
- `-p 8080:8080` ánh xạ cổng 8080 trên máy của chúng ta với cổng 8080 trong container

## Phương pháp 2: Sử dụng Plugin Jib (cách hiện đại)

- `Jib` là một plugin của Google giúp chuisng ta build docker Image mà `không cần phải có Docker đã cài đặt trên máy` và `không cần viết Dockerfile` jib tự động đóng gói ứng dụng Java của chúng ta một cách hiệu quả, chia nhiều lớp (layers) để tối ưu hóa quá trình build và tải image

### Bước 1: thêm plugin Jib vào file `pom.xml`

- Mở file `pom.xml` của dự án và thêm plugin jib vào `<plguin>`

```xml
<plugin>
    <groupId>com.google.cloud.tools</groupId>
    <artifactId>jib-maven-plugin</artifactId>
    <version>3.4.0</version>
    <configuration>
        <from>
            <image>eclipse-temurin:17-jre-focal</image>
        </from>
        <to>
            <image>docker.io/your-dockerhub-username/your-spring-boot-app</image>
        </to>
    </configuration>
</plugin> 
```

- `<from>` chỉ định nền tảng image mà ta muốn sử dụng `eclipse-temurin:17-jre-focal` là một lựa chọn tốt

- `to` là nơi ta muốn đẩy image lên (DOcker hub). 

### Bước 2: Build và đẩy Image

- Một trong 2 lệnh

- `1. Chỉ build image vào local Docker daemon`

```sh
mvn compile jib:dockerBuild
```

- `Build và đẩy Image trực tiếp trên Docker hub (hoặc registry khác)`

```sh
mvn compile jib:build
```

- `Ưu điểm của jib`
    - `Nhanh và hiệu quả` : tự động tối ưu hóa các lớp (layers) của Image, chỉ build lại những gì đã thay đổi

    - `không cần dokcer` : rất tiện lợi cho môi trường CICD (Continuous Itergration / Continous Delivery)

    - `An toàn`: không cần sử dụng `root` để chạy Docker deamon, giảm rủi do bảo mật
