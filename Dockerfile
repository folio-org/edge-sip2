FROM docker.io/folioci/eclipse-temurin:25-alpine
WORKDIR /app
COPY target/*-fat.jar app.jar
EXPOSE 6443
CMD ["java", "-jar", "app.jar"]
