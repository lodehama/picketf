FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app
COPY . .

WORKDIR /app/picketf
RUN mvn clean package -DskipTests

FROM eclipse-temurin:25-jdk
WORKDIR /app
COPY --from=build /app/picketf/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]