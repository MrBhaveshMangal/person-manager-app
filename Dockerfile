# Use OpenJDK 17 base image
FROM eclipse-temurin:17

# Create app directory
WORKDIR /app

# Copy the JAR file into the image
COPY target/emaildemo-0.0.1-SNAPSHOT.jar app.jar

# Expose port (default for Spring Boot)
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]
