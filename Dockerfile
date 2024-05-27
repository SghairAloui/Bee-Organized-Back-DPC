# Use openjdk 17 as the base image
FROM openjdk:17-jdk-alpine

MAINTAINER iheb.kchiche@dipower.fr

# Install necessary tools
RUN apk update && \
    apk add --no-cache \
       maven \
       curl

# Create a directory for the application
WORKDIR /opt/myapp

# Copy the Maven project
COPY . .

# Build the application
RUN mvn clean package

# Download and extract Apache Tomcat
RUN curl -O https://dlcdn.apache.org/tomcat/tomcat-9/v9.0.85/bin/apache-tomcat-9.0.85.tar.gz && \
    tar xfz apache-tomcat-9.0.85.tar.gz && \
    mv apache-tomcat-9.0.85 /opt/tomcat

# Remove unnecessary files
RUN rm -f apache-tomcat-9.0.85.tar.gz && \
    rm -rf /opt/tomcat/webapps/*

# Copy the built .war file to the webapps directory
RUN cp /opt/myapp/target/BeeOranized-0.0.1-SNAPSHOT.war /opt/tomcat/webapps/

# Expose Tomcat ports
EXPOSE 9999

# Start Tomcat
CMD ["/opt/tomcat/bin/catalina.sh", "run"]
