FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/elderly-health-backend-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-Dstdout.encoding=UTF-8", "-jar", "app.jar"]
