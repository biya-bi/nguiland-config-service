# syntax=docker/dockerfile:1

FROM eclipse-temurin:21-jre-alpine

WORKDIR /opt/ostock

COPY target/*.jar ./config-service.jar

ENTRYPOINT java -jar ./config-service.jar
