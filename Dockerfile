# ── Stage 1: Build ──────────────────────────────────────────────────────────
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy pom.xml first so dependency layer is cached
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copy source and package
COPY src ./src
RUN mvn package -DskipTests -q

# ── Stage 2: Run ────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Create non-root user for security
RUN addgroup --system zion && adduser --system --ingroup zion zion

COPY --from=build /app/target/zion_center-0.0.1-SNAPSHOT.jar app.jar

RUN chown zion:zion app.jar
USER zion

EXPOSE 8080

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-XX:+UseContainerSupport", "-jar", "app.jar"]
