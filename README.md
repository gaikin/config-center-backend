# Config Center Backend

Spring Boot + MyBatis-Plus backend for the config center project.

## Stack

- Java 17
- Spring Boot
- MyBatis-Plus
- H2 (default local simulation)
- MySQL
- Maven

## Scripts

- `mvn -f backend/pom.xml spring-boot:run`
- `mvn -f backend/pom.xml "-Dspring-boot.run.profiles=h2" spring-boot:start`
- `mvn -f backend/pom.xml spring-boot:stop`
- `mvn -f backend/pom.xml -DskipTests package`
- `mvn -f backend/pom.xml test`

## Profiles

- Default profile: `h2`
- Switch to MySQL: `SPRING_PROFILES_ACTIVE=mysql`

## H2 Console

- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:config_center`
- User: `sa`
- Password: empty

## Structure

- `apps/api-server/src/main/java`: application bootstrap and HTTP adapter layer
- `apps/api-server/src/main/resources`: Spring profiles and runtime resources
- `packages/application/src/main/java`: application-layer shared context and exceptions
- `packages/domain-models/src/main/java`: domain models and contracts
- `packages/infrastructure-db/src/main/java`: DB infrastructure and MyBatis-Plus config
- `packages/test-kit/src/test/java`: foundation-level integration and smoke tests
- `db/migrations`: draft MySQL schema scripts

## Notes

- Runtime entrypoint is `com.configcenter.backend.bootstrap.ConfigCenterApiServerApplication`.
- Shared common code has moved to the new `apps + packages` structure.
- Local startup works out of the box with in-memory H2 and `/healthz`.
