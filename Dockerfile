# ESTÁGIO 1: Compilação (Build)
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app

# Copia o pom.xml raiz
COPY pom.xml .

# Copia todos os módulos do projeto para evitar o erro de "Child module does not exist"
# O Maven precisa enxergar a estrutura completa definida no pom.xml pai
COPY domain ./domain
COPY persistence ./persistence
COPY bot-spi ./bot-spi
COPY bot-impl ./bot-impl
COPY web ./web
COPY console ./console
COPY desktop ./desktop

# Compila o módulo web e suas dependências necessárias (-am)
# Agora os módulos console e desktop existem na estrutura, satisfazendo o Maven
RUN mvn clean package -DskipTests -pl web -am

# ESTÁGIO 2: Execução (Runtime)
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copia o jar do módulo web (o estágio 1 garante que ele foi gerado)
COPY --from=build /app/web/target/web-1.2.0-SNAPSHOT.jar app.jar

# Configuração de porta dinâmica para o Render
ENTRYPOINT ["java", "-Xmx512m", "-Dserver.port=${PORT}", "-jar", "app.jar"]