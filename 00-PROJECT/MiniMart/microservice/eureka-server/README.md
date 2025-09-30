```sh
mvn clean package -DskipTests
docker build -t duongbd1997/eureka-server:0.0.1-SNAPSHOT .
docker run -d -p 8070:8070 --name eureka-server duongbd1997/eureka-server:0.0.1-SNAPSHOT

```
