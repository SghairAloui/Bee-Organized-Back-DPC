# Use official Maven image with JDK 17 on Alpine Linux to build the application
FROM maven:3.8.4-openjdk-17-slim AS build

# Set the working directory to /app
WORKDIR /app

# Copy the source code
COPY . .

# Build the application
RUN mvn clean install -DskipTests

# Create a new image with OpenJDK 17 on Alpine Linux
FROM openjdk:17-jdk-alpine

# Set the working directory to /app
WORKDIR /app

# Copy the JAR file from the build image
COPY --from=build /app/target/BeeOranized-0.0.1-SNAPSHOT.jar /app/BeeOranized-0.0.1-SNAPSHOT.jar

# Expose port 9999
EXPOSE 9999

# Set the JAVA_OPTS environment variable (optional)
ENV JAVA_OPTS=""

# Run the Java application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/BeeOranized-0.0.1-SNAPSHOT.jar"]
