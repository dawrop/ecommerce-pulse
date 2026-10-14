FROM sbtscala/scala-sbt:eclipse-temurin-17.0.13_11_1.10.7_2.13.16 AS builder

WORKDIR /app

COPY . .

RUN sbt "generator/assembly"

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/out/jvm/scala-2.13.18/ecommerce-pulse-generator/ecommerce-pulse-generator-assembly-0.1.0.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]