FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn -B -DskipTests dependency:go-offline

COPY src ./src
COPY config-repo ./config-repo
RUN mvn -B -DskipTests clean package

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

ENV JAVA_OPTS=""

COPY --from=build /app/target/*.jar app.jar
COPY --from=build /app/config-repo ./config-repo

EXPOSE 8888

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
