## Deploy Auth Service 

- `Vấn đề phụ thuộc khá nhiều thứ: Eureka + Configuration + database`
- `Vấn đề database ko tự khởi tạo`
- `Vấn đề schema rời rạc do sử dụng file .sql riêng`
- `Vấn đề với Profile khác nhau`
- Tạo thêm các file config riêng
- `Vấn đề cần chạy sql scription sau khi app chạy và seeding`
- Sử dụng defer ở property
- Và sử dụng data + init schema ở property


```dockerfile
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .

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

RUN mvn -B -ntp dependency:go-offline

# Phụ thuộc Common
COPY common/src common/src 
COPY authentication-service/src authentication-service/src

RUN mvn clean package -pl authentication-service -am -DskipTests

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

COPY --from=build /app/authentication-service/target/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java","-jar","app.jar"]
```

- Chạy thử
```bash
docker build -f authentication-service/Dockerfile -t minimart-authentication-service:1.0 .
docker run --name auth -p 8071:8071  minimart-authentication-service:1.0
```

### Để xử lý vấn đề với Profile

- ta sẽ tạo thêm profile docker
```yml
# config/authentication-service-docker.yml (docker)
eureka:
    client:
        service-url:
            defaultZone: http://eureka-server:8070/eureka/

spring:
    datasource:
        url: jdbc:postgresql://${DATASOURCE_NAME}:5433/minimart-auth?createDatabaseIfNotExist=true
        username: ${DATASOURCE_USERNAME}
        password: ${DATASOURCE_PASSWORD}

    jpa:
        show-sql: true
        properties:
            hibernate:
                format_sql: true
                highlight_sql: true
        hibernate:
            ddl-auto: update

    security:
        oauth2:
            client:
                registration:
                    github:
                        client-id: ${GITHUB_CLIENT_ID}
                        client-secret: ${GITHUB_CLIENT_SECRET}
                    google:
                        client-id: ${GOOGLE_CLIENT_ID}
                        client-secret: ${GOOGLE_CLIENT_SECRET}

custom:
    client:
        origins: ${CUSTOM_CORS_CLIENT_ORIGINS}

management:
  endpoints:
    web:
      exposure:
        include: health,info
```

