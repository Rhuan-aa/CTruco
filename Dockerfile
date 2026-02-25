FROM maven:3.8.4-openjdk-17-slim AS build

WORKDIR /app

COPY pom.xml .

COPY domain ./domain
COPY persistence ./persistence
COPY bot-spi ./bot-spi
COPY bot-impl ./bot-impl
COPY web ./web

RUN mvn clean package -DskipTests -pl web -am

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=build /app/web/target/web-1.2.0-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Xmx512m", "-Dserver.port=${PORT}", "-jar", "app.jar"]