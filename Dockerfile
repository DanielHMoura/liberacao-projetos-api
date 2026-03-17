FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN apk add --no-cache maven

# Baixa dependencias antes de copiar o codigo para aproveitar cache de camada no Render.
RUN mvn -B -ntp \
    -Dmaven.wagon.http.retryHandler.count=5 \
    -Dmaven.wagon.http.connectionTimeout=30000 \
    -Dmaven.wagon.http.readTimeout=60000 \
    dependency:go-offline

COPY src src
RUN mvn -B -ntp clean package -DskipTests \
    -Dmaven.wagon.http.retryHandler.count=5 \
    -Dmaven.wagon.http.connectionTimeout=30000 \
    -Dmaven.wagon.http.readTimeout=60000

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
