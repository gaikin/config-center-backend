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

- `mvn spring-boot:run`
- `mvn "-Dspring-boot.run.profiles=h2" spring-boot:start`
- `mvn spring-boot:stop`
- `mvn -DskipTests package`
- `mvn test`

## Profiles

- Default profile: `h2`
- Switch to MySQL: `SPRING_PROFILES_ACTIVE=mysql`

## H2 Console

- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:config_center`
- User: `sa`
- Password: empty

## Structure

- `src/main/java`: application source code (bootstrap, controller, application service, infrastructure)
- `src/main/resources`: Spring profiles and runtime resources
- `src/test/java`: integration and smoke tests
- `db/migrations`: draft MySQL schema scripts

## Notes

- Runtime entrypoint is `com.configcenter.backend.bootstrap.ConfigCenterApiServerApplication`.
- Project follows Maven standard source layout.
- Local startup works out of the box with in-memory H2 and `/healthz`.
