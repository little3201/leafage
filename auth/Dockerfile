FROM eclipse-temurin:25-jre

WORKDIR /app

COPY target/auth-0.0.1.jar app.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
