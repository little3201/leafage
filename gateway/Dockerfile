# Start with a base image containing Java runtime
FROM eclipse-temurin:17-jre-alpine

# Add Maintainer Info
LABEL maintainer="little3201@gmail.com"

# Add param to use in anywhere
ARG JAR_NAME=leafage-gateway
ARG VERSION=0.1.0

# Add a volume pointing to /tmp
VOLUME /tmp

# Make port 8760 available to the world outside this container
EXPOSE 8760

# The application's jar file
ARG JAR_FILE=target/${JAR_NAME}-${VERSION}.jar

# Add the application's jar to the container
COPY ${JAR_FILE} app.jar

# Run the jar file
ENTRYPOINT ["java","-jar","/app.jar","--spring.profiles.active=prod"]
