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