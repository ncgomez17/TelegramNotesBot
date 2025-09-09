# Etapa de construcción
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean install

# Etapa de ejecución
FROM openjdk:17-jdk-slim
COPY --from=build /app/target/TelegramNotesBot-0.0.1-SNAPSHOT.jar /app/TelegramNotesBot.jar
WORKDIR /app
CMD ["java", "-jar", "/app/TelegramNotesBot.jar"]
