# Config Center Backend

Spring Boot + MyBatis-Plus backend for the config center project.

## Stack

- Java 17
- Spring Boot
- MyBatis-Plus
- MySQL (default production mode)
- H2 (local simulation)
- Maven

## Scripts

- `mvn spring-boot:run`
- `mvn "-Dspring-boot.run.profiles=h2" spring-boot:start`
- `mvn spring-boot:stop`
- `mvn -DskipTests package`
- `mvn test`

## Profiles

- Default profile: `mysql`
- Switch to H2: `SPRING_PROFILES_ACTIVE=h2`

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
- Local startup uses MySQL by default; switch to H2 with `SPRING_PROFILES_ACTIVE=h2` for in-memory local boot and `/healthz`.
