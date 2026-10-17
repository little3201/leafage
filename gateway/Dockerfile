FROM eclipse-temurin:25-jre

WORKDIR /app

COPY target/gateway-0.0.1.jar app.jar

EXPOSE 8760

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
