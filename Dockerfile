FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml ./
COPY domain/pom.xml domain/pom.xml
COPY console/pom.xml console/pom.xml
COPY desktop/pom.xml desktop/pom.xml
COPY persistence/pom.xml persistence/pom.xml
COPY bot-spi/pom.xml bot-spi/pom.xml
COPY bot-impl/pom.xml bot-impl/pom.xml
COPY web/pom.xml web/pom.xml

RUN mvn -B -q -DskipTests dependency:go-offline

COPY . .
RUN mvn -B -DskipTests clean package -pl web -am

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/web/target/web-*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]