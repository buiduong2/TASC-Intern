### Build Eureka Server

#### B1: Chuẩn bị Dockerfile cho Eureka

- Tạo `dockerfile` trong folder `eureka-server`

- Nội dung Dockerfile

```Dockerfile
# ===== BUILD STAGE =====
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# ===== RUNTIME STAGE =====
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8761
ENTRYPOINT ["java","-jar","app.jar"]
```

- **`Giải thích`**
- Docker này có 2 phần:

```
BUILD STAGE -> build ra file .jar
```

- Gọi là `multi-stage build`
- Mục tiêu:
    - Image cuối cùng nhỏ hơn
    - Không chưa Maven
    - Không chứa Source Code
- `Phần 1: Build Stage`
-   1.

```Dockerfile
FROM maven:3.9.6-eclipse-temurin-17 AS build
```

- Nghĩa là:
    - Dùng image có sẵn:
        - Maven 3.9.6
        - JDK 17
        - Đặt tên stage này là `build`
- Vì sao:
    - Ta cần Maven để compile Project
    - Image này khá nặng ~700MB
    - Nhưng chỉ dùng tạm thời

- `2.`

```Dockerfile
WORKDIR /app
```

- Tạo và chuyển vào thư mục `/app` bên trong Container
- Giống như

```sh
mkdir /app
cd /app
```

- Mọi lệnh sau đó chạy trong thư mục này
- `3. `

```DockerFile
COPY pom.xml .
```

- Chỉ copy file `pom.xml` vào Container
- Chưa copy source code
- `4. `

```dockerFile
RUN mvn dependency:go-offline
```

- Maven sẽ:
    - Download toàn bộ dependency
    - Lưu vào LocalMaven cache trong Container

- Vì sao tách riêng vước này ?
    - Để Docker Cache Layer này.
    - Nếu sau này ta sửa code mà không sửa pom.xml
    - -> bước này ko chạy lại
-   5.

```dockerfile
COPY src ./src
```

- Bây giờ mới copy source code.
- `6. `

```dockerfile
RUN mvn clean package -DskipTests
```

- Compile Project
- Tạo file `.jar` trong `target/`
- Bỏ qua test (để build nhanh)
- Sau bước này ta có:

```
/app/target/eureka-server-xxx.jar
```

- `PHẦN 2: RUNTIME STAGE`
- `7. `

```Dockerfile
FROM eclipse-temurin:17-jdk-alpine
```

- Đây là Image nhẹ hơn nhiều
- Không có Maven
- Chỉ có JDK để chạy ứng dụng
- Alpine = Linux nhỏ gọn
- -> Image nhẹ hơn ~70%
- `8. `

```dockerfile
WORKDIR /app
```

- Lại tạo thư mục `/app` trong image runtime
- `9. `

```dockerfile
COPY --from=build /app/target/*.jar app.jar
```

- Copy file jar từ stage build sang stage runtime
- Stage build sẽ bị bỏ qua sau khi image hoàn tất
- Image cuối cùng chỉ chứa:
    - JDK
    - app.jar
- Không chưa maven
- Không chứa sourcecode

- `10. `

```dockerfile
EXPOSE 8761
```

- `11. `

```dockerfile
COPY --from=build /app/target/*.jar app.jar
```

- `Entrypoint khác gì CMD`

```dockerfile
ENTRYPOINT ["java","-jar","app.jar"]
```

- Image nên trung tính ko có profile trong này ta sẽ thêm enviroent ment trong cmopose

### Test chạy thử

```bash
cd .....
docker build .
```

- lỗi to

```
2.129 Downloading from central: https://repo.maven.apache.org/maven2/com/minimart-project/1.0.0/minimart-project-1.0.0.pom
4.208 [ERROR] [ERROR] Some problems were encountered while processing the POMs:
4.208 [FATAL] Non-resolvable parent POM for com:eureka-server:0.0.1-SNAPSHOT: The following artifacts could not be resolved: com:minimart-project:pom:1.0.0 (absent): Could not find artifact com:minimart-project:pom:1.0.0 in central (https://repo.maven.apache.org/maven2) and 'parent.relativePath' points at wrong local POM @ line 6, column 13
```

### Vấn đề thực sự

- Chúng ta đang xây dựng `microservice multiple module`

### Chuyển sang phiên bản copy tối ưu

```dockerfile
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# 1️⃣ Copy root pom
COPY pom.xml .

# 2️⃣ Copy pom của tất cả module
COPY common/pom.xml common/
COPY eureka-server/pom.xml eureka-server/
COPY configuration-server/pom.xml configuration-server/
COPY gateway-server/pom.xml gateway-server/
COPY authentication-service/pom.xml authentication-service/
COPY product-service/pom.xml product-service/
COPY profile-service/pom.xml profile-service/
COPY inventory-service/pom.xml inventory-service/
COPY order-service/pom.xml order-service/
COPY admin-bff/pom.xml admin-bff/
COPY common-kafka/pom.xml common-kafka/

# 3️⃣ Download dependency
RUN mvn -B -ntp dependency:go-offline

# 4️⃣ Copy source chỉ cho module cần build
COPY eureka-server/src eureka-server/src

# 5️⃣ Build eureka + dependency
RUN mvn clean package -pl eureka-server -am -DskipTests

# ===== RUNTIME =====
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

COPY --from=build /app/eureka-server/target/*.jar app.jar

EXPOSE 8070
ENTRYPOINT ["java","-jar","app.jar"]
```

- `-pl` : Build module cụ thể
- `-am`: build cả module dependency (VD common)

- Chạy bằng câu lệnh. Để lấy Context
```bash
cd D:\04-Company\01-TASC\TASC-Intern\00-PROJECT\MiniMart\microservice
docker build -f eureka-server/Dockerfile .
```

- Thằng Đần Maven nó luôn validate cấu rtucs của tất cả các module con trong multi module
  - Giải pháp ta copy đủ cấu trúc thư mục và các file pom cần thiết
  - `Cái giá phải trả`
      - Dockerfile dài.
      - Phải maintain danh sách module.
      - Khi thêm module mới → phải thêm COPY pom vào Dockerfile.

### Vấn đề Container lại phụ thuộc vào Config Server

- Cấu hình config server, optional để có thể khi không có config server vẫn ko fastfail

```yml
config:
    import: optional:configserver:${CONFIG_SERVER_URI:http://localhost:8071}
```


### Vấn đề Port chỉ được override sau khi kết nối với config Server

### Và EXPOSE nên phản ánh kiến trúc thật sau khi override ở Config Server. Vậy sử dụng 8070


### Test

- Tạo container chạy thử

```bash
docker build -f eureka-server/Dockerfile -t minimart-eureka:1.0 .
docker run --name eureka -p 8070:8070 minimart-eureka:1.0
```

## B2: Sử dụng Docker compose 