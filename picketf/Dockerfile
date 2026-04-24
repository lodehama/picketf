FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY picketf/pom.xml .
RUN mvn dependency:go-offline

COPY picketf/src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]