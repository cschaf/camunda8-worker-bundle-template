# pre-fetch dependencies
FROM maven:3.9.9-eclipse-temurin-21 AS DEPENDENCIES

WORKDIR /app
COPY common/pom.xml common/pom.xml
COPY workers/pom.xml workers/pom.xml

COPY pom.xml .

RUN mvn -B -e org.apache.maven.plugins:maven-dependency-plugin:3.1.2:go-offline

FROM maven:3.9.9-eclipse-temurin-21 AS BUILDER

WORKDIR /app
COPY --from=DEPENDENCIES /root/.m2 /root/.m2
COPY --from=DEPENDENCIES /app/ /app
COPY common/src /app/common/src
COPY workers/src /app/workers/src

RUN mvn -B -e clean install -DskipTests

FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY workers/src/main/resources/application-onpremise.yaml /app/application.yaml
COPY --from=BUILDER /app/workers/target/*.jar /app/workers.jar

COPY start.sh /app/start.sh
RUN chmod +x /app/start.sh

ENTRYPOINT ["/app/start.sh", "/app"]