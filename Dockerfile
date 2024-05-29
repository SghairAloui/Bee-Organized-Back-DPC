# Use an official OpenJDK 17 runtime as a parent image
FROM maven:3.8.4-openjdk-17-slim AS build

# Set the working directory to /app
WORKDIR /app

# Copy the source code
COPY . .

# Build the application
RUN mvn clean install -DskipTests

# Create a new image with the JAR file
FROM adoptopenjdk/openjdk17:alpine-jre

# Set the working directory to /app
WORKDIR /app

# Copy the JAR file from the build image
COPY --from=build /app/target/BeeOranized-0.0.1-SNAPSHOT.jar /app/BeeOranized-0.0.1-SNAPSHOT.jar

# Make port 9999 available to the world outside this container
EXPOSE 9999

# Define environment variable
ENV JAVA_OPTS=""

# Run the Java application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar BeeOranized-0.0.1-SNAPSHOT.jar"]
