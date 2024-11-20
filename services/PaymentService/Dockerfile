# === Stage 1: Build JAR file ===
# Use an official Maven image to build the app
FROM maven:3.8.3-openjdk-17 AS build-stage

# Set working directory
WORKDIR /app

# Copy parent pom.xml
COPY pom.xml /

# Copy payment service pom.xml
COPY PaymentService/pom.xml .

# Install dependencies
RUN mvn dependency:resolve -U

# Copy source files
COPY PaymentService/src ./src

# Build project with Maven
RUN mvn package -DskipTests

# === Stage 2: Run JAR file ===
# Use an official OpenJDK image to run the app
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

## Create the data directory for SQLite
RUN mkdir data

# Copy the JAR file from the build stage
COPY --from=build-stage /app/target/*.jar app.jar

# Expose the port for the server to run on
EXPOSE 8082

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
