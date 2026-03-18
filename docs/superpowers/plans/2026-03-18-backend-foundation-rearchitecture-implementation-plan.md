# Backend Foundation Rearchitecture Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build the new `apps + packages` backend foundation with `MyBatis-Plus`, shared common infrastructure, and a new Spring Boot startup path so later module migrations can happen entirely in the new structure.

**Architecture:** Keep one Maven project for now, but make `backend/apps/api-server` the only runtime entrypoint and compile `packages/*` as the authoritative source roots. Move common HTTP, exception, request-context, and database bootstrap code into the new structure first, then prove the new foundation with H2-backed smoke tests before any business-module migration begins.

**Tech Stack:** Java 17 + Spring Boot 3.4 + MyBatis-Plus + H2 + MySQL + Maven + JUnit 5

---

## Scope Split

This plan intentionally covers only phase 1 from [2026-03-18-backend-full-rearchitecture-design.md](/C:/dev/projects/work/config-center/docs/superpowers/specs/2026-03-18-backend-full-rearchitecture-design.md): shared foundation migration.

Follow-on plans must be written separately for:

1. `permission + governance`
2. `page-resource + interface-registry + rule + publish`
3. `runtime + seed rewrite + old-tree deletion`

## File Structure Map

Planned files for this phase:

- Create: `backend/apps/api-server/src/main/java/com/configcenter/backend/bootstrap/ConfigCenterApiServerApplication.java`
  Responsibility: sole Spring Boot entrypoint and package scan root for the new structure.
- Create: `backend/apps/api-server/src/main/java/com/configcenter/backend/common/controller/HealthController.java`
  Responsibility: preserve a minimal HTTP smoke endpoint in the new runtime tree.
- Create: `backend/apps/api-server/src/main/java/com/configcenter/backend/common/api/ApiResponse.java`
  Responsibility: shared success/failure envelope for controllers.
- Create: `backend/apps/api-server/src/main/java/com/configcenter/backend/common/api/PageResponse.java`
  Responsibility: shared paged response DTO.
- Create: `backend/apps/api-server/src/main/java/com/configcenter/backend/common/exception/GlobalExceptionHandler.java`
  Responsibility: map business/validation/unexpected exceptions to `ApiResponse`.
- Create: `backend/apps/api-server/src/main/java/com/configcenter/backend/common/filter/RequestContextFilter.java`
  Responsibility: build request context from headers for every HTTP request.
- Create: `backend/apps/api-server/src/main/resources/application.yml`
  Responsibility: new canonical root Spring Boot config.
- Create: `backend/apps/api-server/src/main/resources/application-h2.yml`
  Responsibility: H2 runtime profile for local boot and tests.
- Create: `backend/apps/api-server/src/main/resources/application-mysql.yml`
  Responsibility: MySQL runtime profile for non-local environments.
- Create: `backend/apps/api-server/src/main/resources/schema-h2.sql`
  Responsibility: phase-1 schema bootstrapping for H2 smoke startup.
- Create: `backend/apps/api-server/src/main/resources/data-h2.sql`
  Responsibility: phase-1 seed data for H2 smoke startup.
- Create: `backend/packages/application/src/main/java/com/configcenter/backend/common/context/RequestContext.java`
  Responsibility: request identity state shared across application use cases.
- Create: `backend/packages/application/src/main/java/com/configcenter/backend/common/context/RequestContextHolder.java`
  Responsibility: thread-local access to current request context.
- Create: `backend/packages/application/src/main/java/com/configcenter/backend/common/exception/BizException.java`
  Responsibility: application/business exception type with HTTP status and structured details.
- Create: `backend/packages/application/src/main/java/com/configcenter/backend/common/exception/ErrorDetail.java`
  Responsibility: structured validation/business error payload item.
- Create: `backend/packages/infrastructure-db/src/main/java/com/configcenter/backend/infrastructure/db/config/MybatisPlusConfig.java`
  Responsibility: pagination interceptor and DB-related framework configuration.
- Create: `backend/packages/infrastructure-db/src/main/java/com/configcenter/backend/infrastructure/db/config/DbAuditMetaObjectHandler.java`
  Responsibility: auto-fill audit columns for insert/update.
- Create: `backend/packages/infrastructure-db/src/main/java/com/configcenter/backend/infrastructure/db/model/BaseAuditDO.java`
  Responsibility: shared `createdAt/createdBy/updatedAt/updatedBy/isDeleted` persistence fields.
- Create: `backend/packages/test-kit/src/test/java/com/configcenter/backend/bootstrap/ApiServerBootstrapTest.java`
  Responsibility: prove the new runtime tree can boot with H2 and expose the health endpoint.
- Modify: `backend/pom.xml`
  Responsibility: add `MyBatis-Plus`, compile the new source roots, and wire new test source roots.
- Delete later in this phase: matching old files under `backend/src/main/java/com/configcenter/backend/common/*`, `backend/src/main/resources/*`, and `backend/src/main/java/com/configcenter/backend/ConfigCenterBackendApplication.java`
  Responsibility: remove duplicate bootstrap/common code once the new structure passes verification.

### Task 1: Rewire Maven to Compile the New Structure

**Files:**
- Modify: `backend/pom.xml`
- Create: `backend/packages/test-kit/src/test/java/com/configcenter/backend/bootstrap/ApiServerBootstrapTest.java`
- Verify: `mvn -f backend/pom.xml -Dtest=ApiServerBootstrapTest test`

- [ ] **Step 1: Create `backend/packages/test-kit/src/test/java/com/configcenter/backend/bootstrap/ApiServerBootstrapTest.java` with a `@SpringBootTest` that references `ConfigCenterApiServerApplication` and calls `/health` through `MockMvc`**
- [ ] **Step 2: Run `mvn -f backend/pom.xml -Dtest=ApiServerBootstrapTest test`**
  Expected: FAIL because the new application class and source roots do not exist yet
- [ ] **Step 3: Add `mybatis-plus-spring-boot3-starter` and any required test/build-helper plugins to `backend/pom.xml`**
- [ ] **Step 4: Configure `backend/pom.xml` to compile Java/resources from `apps/api-server`, `packages/application`, `packages/domain-models`, `packages/infrastructure-db`, and test sources from `packages/test-kit`**
- [ ] **Step 5: Re-run `mvn -f backend/pom.xml -Dtest=ApiServerBootstrapTest test`**
  Expected: FAIL moves forward to missing runtime/bootstrap files instead of missing source roots
- [ ] **Step 6: Commit**

```bash
git add backend/pom.xml backend/packages/test-kit/src/test/java/com/configcenter/backend/bootstrap/ApiServerBootstrapTest.java
git commit -m "build: wire backend new source roots"
```

### Task 2: Create the New API-Server Bootstrap and Runtime Resources

**Files:**
- Create: `backend/apps/api-server/src/main/java/com/configcenter/backend/bootstrap/ConfigCenterApiServerApplication.java`
- Create: `backend/apps/api-server/src/main/resources/application.yml`
- Create: `backend/apps/api-server/src/main/resources/application-h2.yml`
- Create: `backend/apps/api-server/src/main/resources/application-mysql.yml`
- Create: `backend/apps/api-server/src/main/resources/schema-h2.sql`
- Create: `backend/apps/api-server/src/main/resources/data-h2.sql`
- Verify: `mvn -f backend/pom.xml -Dtest=ApiServerBootstrapTest test`

- [ ] **Step 1: Create `ConfigCenterApiServerApplication.java` with `@SpringBootApplication(scanBasePackages = "com.configcenter.backend")` and `@MapperScan` pointed at the new infrastructure-db package**
- [ ] **Step 2: Copy and normalize the current root Spring config into `backend/apps/api-server/src/main/resources/application.yml` so the new entrypoint owns profile selection and logging defaults**
- [ ] **Step 3: Copy the current H2 and MySQL profile config into `backend/apps/api-server/src/main/resources/application-h2.yml` and `backend/apps/api-server/src/main/resources/application-mysql.yml`**
- [ ] **Step 4: Copy `schema-h2.sql` and `data-h2.sql` into the new resources tree so H2 boot no longer depends on `backend/src/main/resources`**
- [ ] **Step 5: Run `mvn -f backend/pom.xml -Dtest=ApiServerBootstrapTest test`**
  Expected: FAIL now points at missing common HTTP or request-context classes rather than missing bootstrap/resources
- [ ] **Step 6: Commit**

```bash
git add backend/apps/api-server/src/main/java/com/configcenter/backend/bootstrap/ConfigCenterApiServerApplication.java backend/apps/api-server/src/main/resources/application.yml backend/apps/api-server/src/main/resources/application-h2.yml backend/apps/api-server/src/main/resources/application-mysql.yml backend/apps/api-server/src/main/resources/schema-h2.sql backend/apps/api-server/src/main/resources/data-h2.sql
git commit -m "feat: bootstrap backend from api-server app"
```

### Task 3: Move Shared HTTP Response and Exception Infrastructure

**Files:**
- Create: `backend/apps/api-server/src/main/java/com/configcenter/backend/common/api/ApiResponse.java`
- Create: `backend/apps/api-server/src/main/java/com/configcenter/backend/common/api/PageResponse.java`
- Create: `backend/apps/api-server/src/main/java/com/configcenter/backend/common/exception/GlobalExceptionHandler.java`
- Create: `backend/packages/application/src/main/java/com/configcenter/backend/common/exception/BizException.java`
- Create: `backend/packages/application/src/main/java/com/configcenter/backend/common/exception/ErrorDetail.java`
- Verify: `mvn -f backend/pom.xml -Dtest=ApiServerBootstrapTest test`

- [ ] **Step 1: Port `BizException` and `ErrorDetail` into `packages/application` so business errors live outside the HTTP adapter layer**
- [ ] **Step 2: Port `ApiResponse` and `PageResponse` into `apps/api-server` so controllers can return a stable envelope from the new tree**
- [ ] **Step 3: Port `GlobalExceptionHandler` into `apps/api-server` and update imports to use the new application-layer exception classes**
- [ ] **Step 4: Run `mvn -f backend/pom.xml -Dtest=ApiServerBootstrapTest test`**
  Expected: FAIL moves on to missing request-context or health-controller pieces
- [ ] **Step 5: Commit**

```bash
git add backend/apps/api-server/src/main/java/com/configcenter/backend/common/api/ApiResponse.java backend/apps/api-server/src/main/java/com/configcenter/backend/common/api/PageResponse.java backend/apps/api-server/src/main/java/com/configcenter/backend/common/exception/GlobalExceptionHandler.java backend/packages/application/src/main/java/com/configcenter/backend/common/exception/BizException.java backend/packages/application/src/main/java/com/configcenter/backend/common/exception/ErrorDetail.java
git commit -m "refactor: move shared response and exception infrastructure"
```

### Task 4: Move Request Context and HTTP Filtering Into the New Structure

**Files:**
- Create: `backend/packages/application/src/main/java/com/configcenter/backend/common/context/RequestContext.java`
- Create: `backend/packages/application/src/main/java/com/configcenter/backend/common/context/RequestContextHolder.java`
- Create: `backend/apps/api-server/src/main/java/com/configcenter/backend/common/filter/RequestContextFilter.java`
- Create: `backend/apps/api-server/src/main/java/com/configcenter/backend/common/controller/HealthController.java`
- Verify: `mvn -f backend/pom.xml -Dtest=ApiServerBootstrapTest test`

- [ ] **Step 1: Port `RequestContext` and `RequestContextHolder` into `packages/application` without changing their header contract**
- [ ] **Step 2: Port `RequestContextFilter` into `apps/api-server` and update imports to the new context package**
- [ ] **Step 3: Port `HealthController` into `apps/api-server` so the smoke test has a stable endpoint to hit**
- [ ] **Step 4: Run `mvn -f backend/pom.xml -Dtest=ApiServerBootstrapTest test`**
  Expected: FAIL only if DB/bootstrap wiring is still incomplete; otherwise PASS
- [ ] **Step 5: Commit**

```bash
git add backend/packages/application/src/main/java/com/configcenter/backend/common/context/RequestContext.java backend/packages/application/src/main/java/com/configcenter/backend/common/context/RequestContextHolder.java backend/apps/api-server/src/main/java/com/configcenter/backend/common/filter/RequestContextFilter.java backend/apps/api-server/src/main/java/com/configcenter/backend/common/controller/HealthController.java
git commit -m "refactor: move request context pipeline to new backend structure"
```

### Task 5: Add MyBatis-Plus Foundation and Audit Support

**Files:**
- Create: `backend/packages/infrastructure-db/src/main/java/com/configcenter/backend/infrastructure/db/config/MybatisPlusConfig.java`
- Create: `backend/packages/infrastructure-db/src/main/java/com/configcenter/backend/infrastructure/db/config/DbAuditMetaObjectHandler.java`
- Create: `backend/packages/infrastructure-db/src/main/java/com/configcenter/backend/infrastructure/db/model/BaseAuditDO.java`
- Modify: `backend/apps/api-server/src/main/resources/application.yml`
- Verify: `mvn -f backend/pom.xml test`

- [ ] **Step 1: Create `MybatisPlusConfig.java` with pagination interceptor and baseline DB framework configuration**
- [ ] **Step 2: Create `DbAuditMetaObjectHandler.java` to auto-fill `createdAt`, `createdBy`, `updatedAt`, and `updatedBy` from `RequestContextHolder`**
- [ ] **Step 3: Create `BaseAuditDO.java` with shared audit and logical-delete columns for later persistence models**
- [ ] **Step 4: Update `application.yml` and profile files only as needed so the runtime uses `mybatis-plus` configuration keys instead of old `mybatis` keys**
- [ ] **Step 5: Run `mvn -f backend/pom.xml test`**
  Expected: PASS with the bootstrap smoke test green
- [ ] **Step 6: Commit**

```bash
git add backend/packages/infrastructure-db/src/main/java/com/configcenter/backend/infrastructure/db/config/MybatisPlusConfig.java backend/packages/infrastructure-db/src/main/java/com/configcenter/backend/infrastructure/db/config/DbAuditMetaObjectHandler.java backend/packages/infrastructure-db/src/main/java/com/configcenter/backend/infrastructure/db/model/BaseAuditDO.java backend/apps/api-server/src/main/resources/application.yml backend/apps/api-server/src/main/resources/application-h2.yml backend/apps/api-server/src/main/resources/application-mysql.yml
git commit -m "feat: add mybatis-plus foundation for backend rearchitecture"
```

### Task 6: Remove Old Bootstrap/Common Duplicates and Re-Verify the New Entry Path

**Files:**
- Delete: `backend/src/main/java/com/configcenter/backend/ConfigCenterBackendApplication.java`
- Delete: `backend/src/main/java/com/configcenter/backend/common/api/ApiResponse.java`
- Delete: `backend/src/main/java/com/configcenter/backend/common/api/PageResponse.java`
- Delete: `backend/src/main/java/com/configcenter/backend/common/context/RequestContext.java`
- Delete: `backend/src/main/java/com/configcenter/backend/common/context/RequestContextHolder.java`
- Delete: `backend/src/main/java/com/configcenter/backend/common/controller/HealthController.java`
- Delete: `backend/src/main/java/com/configcenter/backend/common/exception/BizException.java`
- Delete: `backend/src/main/java/com/configcenter/backend/common/exception/ErrorDetail.java`
- Delete: `backend/src/main/java/com/configcenter/backend/common/exception/GlobalExceptionHandler.java`
- Delete: `backend/src/main/java/com/configcenter/backend/common/filter/RequestContextFilter.java`
- Delete: `backend/src/main/resources/application.yml`
- Delete: `backend/src/main/resources/application-h2.yml`
- Delete: `backend/src/main/resources/application-mysql.yml`
- Delete: `backend/src/main/resources/schema-h2.sql`
- Delete: `backend/src/main/resources/data-h2.sql`
- Verify: `mvn -f backend/pom.xml test`
- Verify: `mvn -f backend/pom.xml spring-boot:run "-Dspring-boot.run.profiles=h2"`

- [ ] **Step 1: Delete the old bootstrap/common files that now have authoritative replacements in `apps/api-server` and `packages/*`**
- [ ] **Step 2: Run `mvn -f backend/pom.xml test`**
  Expected: PASS using only the new source/resource roots
- [ ] **Step 3: Run `mvn -f backend/pom.xml spring-boot:run "-Dspring-boot.run.profiles=h2"`**
  Expected: application starts successfully and `/health` responds from the new entrypoint
- [ ] **Step 4: Commit**

```bash
git add backend
git commit -m "refactor: switch backend foundation to new app and packages structure"
```

### Task 7: Document Migration Outcome and Queue the Next Plans

**Files:**
- Modify: `backend/README.md`
- Modify: `docs/superpowers/specs/2026-03-18-backend-full-rearchitecture-design.md` (only if implementation changed the approved structure)
- Create: `docs/superpowers/plans/2026-03-18-backend-permission-governance-rearchitecture-implementation-plan.md` (placeholder stub only if the team wants immediate continuation)
- Verify: `git status --short`

- [ ] **Step 1: Update `backend/README.md` so the documented startup path, source layout, and build commands point to the new structure**
- [ ] **Step 2: Reconcile the design spec only if the implemented foundation differs from the approved architecture**
- [ ] **Step 3: Run `git status --short` and confirm only intended foundation files changed**
- [ ] **Step 4: Record the next plan sequence: `permission + governance`, then `control`, then `runtime cleanup`**
- [ ] **Step 5: Commit**

```bash
git add backend/README.md docs/superpowers/specs/2026-03-18-backend-full-rearchitecture-design.md docs/superpowers/plans
git commit -m "docs: align backend foundation migration notes"
```

## Final Verification Checklist

- [ ] Run `mvn -f backend/pom.xml test`
- [ ] Run `mvn -f backend/pom.xml -DskipTests package`
- [ ] Run `mvn -f backend/pom.xml spring-boot:run "-Dspring-boot.run.profiles=h2"` and verify `/health`
- [ ] Confirm no common/bootstrap resources are still loaded from `backend/src/main/*`
- [ ] Confirm the next implementation plan is ready before starting phase 2
