# ESTÁGIO 1: Compilação (Build)
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml .
COPY domain ./domain
COPY persistence ./persistence
COPY bot-spi ./bot-spi
COPY bot-impl ./bot-impl
COPY web ./web

# Compila o módulo web e suas dependências
RUN mvn clean package -DskipTests -pl web -am

# ESTÁGIO 2: Execução (Runtime) - Alterado para uma imagem disponível
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copia o jar do módulo web
COPY --from=build /app/web/target/web-1.2.0-SNAPSHOT.jar app.jar

# Configuração de porta dinâmica para o Render
ENTRYPOINT ["java", "-Xmx512m", "-Dserver.port=${PORT}", "-jar", "app.jar"]