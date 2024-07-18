# syntax=docker/dockerfile:1

FROM eclipse-temurin:17-jre-jammy

WORKDIR /opt/app

COPY target/*.jar ./config-service.jar

ENTRYPOINT java -jar /opt/app/config-service.jar
