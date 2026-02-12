FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S springgroup && adduser -S springuser -G springgroup
WORKDIR /app
COPY target/*.jar app.jar
RUN chown springuser:springgroup app.jar
USER springuser
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
