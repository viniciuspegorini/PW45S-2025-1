# BUILD
FROM eclipse-temurin:17-jdk-alpine as build
WORKDIR /workspace/server

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

# clean up the file
RUN sed -i 's/\r$//' mvnw
# create package
RUN /bin/sh mvnw package -DskipTests

# DELIVERY
FROM openjdk:17
COPY --from=build /workspace/server/target/server-0.1.jar server.jar
ENTRYPOINT ["java", "-jar", "server.jar"]