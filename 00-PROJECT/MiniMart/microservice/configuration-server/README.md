## Build Docker

```sh
mvn clean package -DskipTests
docker build -t duongbd1997/configuration-server:0.0.1-SNAPSHOT .
docker run -d -p 8071:8071  -e ENCRYPT_KEY=$ENCRYPT_KEY --name configuration-server duongbd1997/configuration-server:0.0.1-SNAPSHOT


```
