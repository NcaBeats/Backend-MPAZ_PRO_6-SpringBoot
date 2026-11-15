# syntax=docker/dockerfile:1

# ---------- Build ----------
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /workspace

# Cache de dependencias: solo se invalida si cambia el pom.
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q clean package -DskipTests

# ---------- Runtime ----------
FROM eclipse-temurin:25-jre
WORKDIR /app

RUN groupadd --system spring && useradd --system --gid spring spring
COPY --from=build /workspace/target/*.jar app.jar
USER spring

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
