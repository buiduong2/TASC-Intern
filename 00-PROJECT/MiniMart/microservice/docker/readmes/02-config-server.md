## Config Server

- Gần giống như y hệt Erureka .
- Đầu tiên cần docker file

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

COPY configuration-server/src configuration-server/src

RUN mvn clean package -pl configuration-server -am -DskipTests

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

COPY --from=build /app/configuration-server/target/*.jar app.jar

EXPOSE 8071
ENTRYPOINT ["java","-jar","app.jar"]
```

### Cài đặt

- sau này khi run Container nhớ truyền `ENCRYPT_KEY`

### Thử Run

```bash
docker build -f configuration-server/Dockerfile -t minimart-configuration:1.0 .
docker run --name configuration -p 8071:8071 -e ENCRYPT_KEY=fjE83Ki8403lod87dne7Yj3ltHueh48jfu09j4U2hf64Lo minimart-configuration:1.0
```

### Cài đặt Docker Compose

- `1️⃣ Vấn đề Healcheck - depends`
- `2️⃣ build trực tiếp trong docker compose`

- docker compose

```yml
version: '3.8'

services:
    postgres:
        image: postgres:15
        container_name: postgres
        environment:
            POSTGRES_DB: ${POSTGRES_DB}
            POSTGRES_USER: ${POSTGRES_USER}
            POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
        volumes:
            - postgres_data:/var/lib/postgresql/data
        networks:
            - minimart-net

    config-server:
        build:
            context: ../../../
            dockerfile: configuration-server/Dockerfile
            container_name: config-server
            ports:
                - '8071:8071'
            environtment:
                ENCRYPT_KEY: ${ENCRYPT_KEY}
                test:
                    [
                        'CMD',
                        'wget',
                        '--spider',
                        '-q',
                        'http://localhost:8071/actuator/health'
                    ]
                interval: 10s
                timeout: 5s
                retries: 5
                start_period: 20s
            networks:
                - minimart-net

    eureka-server:
        build:
            context: ../../
            dockerfile: eureka-server/Dockerfile
        container_name: eureka-server
        ports:
            - '8070:8070'
        environment:
            CONFIG_SERVER_URI: http://config-server:8071
        depends_on:
            config-server:
                condition: service_healthy
        healthcheck:
            test:
                [
                    'CMD',
                    'wget',
                    '--spider',
                    '-q',
                    'http://localhost:8070/actuator/health'
                ]
            interval: 10s
            timeout: 5s
            retries: 5
            start_period: 20s
        networks:
            - minimart-net

networks:
    minimart-net:
        driver: bridge

volumes:
    postgres_data:
```

- **`Giải thích`**
- Tổng quan kiến trúc

```
Postgres  (Data Layer)
    ↓
Config Server  (Configuration Layer)
    ↓
Eureka Server  (Service Discovery Layer)
```

```yml
version: '3.8'
```

- Xác định Docker Compose schema version
- 3.8 hỗ trợ healthcheck, depends_on condition, network tốt
- Không ảnh hưởng kiến trúc, chỉ là compatibility

```yml
networks:
    - minimart-net
```

- Tất cả service nằm cùng network
- Resolve bằng service name (VD: postgres, config-server)
- Không dùng localhost
- `Config Server`

```yml
config-server:
    build:
        context: ../../../
        dockerfile: configuration-server/Dockerfile
```

- Không dùng image hardcoded
- Build từ source hiện tại
- Build từ source hiện tại
- Trong VPS production có thể chuyển sang image:tag
- `Heal Check`

```yml
healthcheck:
    test:
        [
            'CMD',
            'wget',
            '--spider',
            '-q',
            'http://localhost:8071/actuator/health'
        ]
```

- Container running ≠ Service ready
- Healthcheck đảm bảo Config Server thực sự hoạt động
- Quan trọng vì Eureka phụ thuộc nó
- `Depend on`

```yml
depends_on:
    config-server:
        condition: service_healthy
```

- Không chỉ start trước
- Phải healthy trước
- Tránh race condition

```yml
Config healthy
↓
Eureka healthy
↓
Auth start
```

- `networks`

```yml
networks:
    minimart-net:
        driver: bridge
```

- Tạo isolated network
- Không đụng host network
- Tất cả service nói chuyện nội bộ
- 