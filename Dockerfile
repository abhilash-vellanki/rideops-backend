FROM eclipse-temurin:21-jdk-noble AS build

WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw

COPY src/ src/
RUN ./mvnw --batch-mode --no-transfer-progress clean package -DskipTests

FROM eclipse-temurin:21-jre-noble AS runtime

RUN useradd --system --user-group --no-create-home appuser

WORKDIR /opt/rideops

COPY --from=build --chown=appuser:appuser /workspace/target/*.jar application.jar

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/opt/rideops/application.jar"]
