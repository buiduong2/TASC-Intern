### Câu lệnh

### khó khăn

- Không có gì đặc biệt so với eureka và configuratio n
- Khi sử dụng DOcker thì ta bị redirect bởi Oauth nó sẽ đưa chúng ta đến với host của internal Container dẫn tới bị lỗi

## Cách sửa Internal COntainer host redirect

thêm
```yml
server:
  forward-headers-strategy: framework
```

### 

```bash
cd D:\04-Company\01-TASC\TASC-Intern\00-PROJECT\MiniMart\microservice
docker build -f gateway-server/Dockerfile -t minimart-gateway-server:1.0 .
docker run --name auth -p 8071:8071  minimart-gateway-server:1.0
```

### THêm vào docker compose

