FROM eclipse-temurin:17-jdk
WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

COPY web/pom.xml web/
COPY persistence/pom.xml persistence/
COPY domain/pom.xml domain/
COPY bot-spi/pom.xml bot-spi/
COPY bot-impl/pom.xml bot-impl/
COPY bot-impl/libs bot-impl/libs
COPY console/pom.xml console/
COPY desktop/pom.xml desktop/

RUN chmod +x mvnw && sed -i 's/\r$//' mvnw

RUN ./mvnw install:install-file -Dfile=bot-impl/libs/mineiro-by-bueno-1.0-SNAPSHOT.jar -DgroupId=com.bueno -DartifactId=mineiro-by-bueno -Dversion=1.0-SNAPSHOT -Dpackaging=jar

RUN ./mvnw dependency:go-offline

COPY . .

RUN chmod +x mvnw && sed -i 's/\r$//' mvnw

RUN ./mvnw clean install -Dmaven.test.skip=true

CMD ["java", "-jar", "web/target/web-1.2.0-SNAPSHOT.jar"]