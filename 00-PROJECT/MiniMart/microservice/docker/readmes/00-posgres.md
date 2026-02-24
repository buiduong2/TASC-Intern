## Bắt đầu tạo khung các thứ cần thiết

- `Vấn đề ta cần nhiều database cần khởi tạo . không thể chỉ sử dụng POSTGRES_DB. Bởi vì có thể có nhiều hơn một DB`

```yml
version: '3.8'

services:
networks: 
  minimart-net:
    driver: bridge

volumes:
  postgres_data:
```

- Có network để các service giao tiếp với nhau
- Có volumes để khi restart ko mất giữ liệu

## Bắt đầu tạo Docker COntainer cho Posgres SQL

```yml
version: '3.8'

services:
  postgres: # Tên service
    image: postgres:15 # Chọn Iamge hợp lý
    container_name: postgres
    environment:
      POSTGRES_DB: ${POSTGRES_DB} # Sử dụng env
      POSTGRES_USER: ${POSTGRES_USER}
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
    volumes: 
      - postgres_data:/var/lib/postgresql/data 
      - ../../init-db:/docker-entrypoint-initdb.d
    networks: 
      - minimart-net

networks: 
  minimart-net:
    driver: bridge

volumes:
  postgres_data:
```

### Chạy thử

```sh
docker compose up postgres

```

### Check

- Kiểm tra 
```bash
docker logs postgres
```
- Xuất hiện dòng 
```bash
database system is ready to accept connections
```

```bash
docker exec -it  postgres  sh
psql -U admin -d postgres
postgres=# \l
                                               List of databases
     Name      | Owner | Encoding |  Collate   |   Ctype    | ICU Locale | Locale Provider | Access privileges
---------------+-------+----------+------------+------------+------------+-----------------+-------------------
 minimart-auth | admin | UTF8     | en_US.utf8 | en_US.utf8 |            | libc            |
```

### Tránh các lỗi

-  password authentication failed
-  container không đọc .env
-  Docker config ko đcọ được env
-  

```
name: vps
services:
  postgres:
    container_name: postgres
    environment:
      POSTGRES_DB: minimart-auth
      POSTGRES_PASSWORD: super-secret-password
      POSTGRES_USER: admin
    image: postgres:15
    networks:
      minimart-net: null
    volumes:
      - type: volume
        source: postgres_data
        target: /var/lib/postgresql/data
        volume: {}
networks:
  minimart-net:
    name: vps_minimart-net
    driver: bridge
volumes:
  postgres_data:
    name: vps_postgres_data
```

- 