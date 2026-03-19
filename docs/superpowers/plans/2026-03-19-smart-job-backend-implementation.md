# Smart Job Backend Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build the backend APIs, persistence model, publish/version flow, and execution-log support required for the Smart Job module to launch with stable scene versions, unique output variables, and troubleshooting data.

**Architecture:** Extend the current Spring Boot + MyBatis control layer with a dedicated `jobscene` module that mirrors existing page/rule/publish patterns. Persist scene definitions, version snapshots, node graphs, execution instances, and node logs in H2/MySQL-compatible tables, then expose typed control APIs for save, validate, publish, execution history, and node log querying.

**Tech Stack:** Java 17, Spring Boot 3, Spring MVC, Spring Validation, MyBatis Plus, H2/MySQL, JUnit 5, MockMvc

---

## Scope Check

This plan covers the backend half of the Smart Job launch only. Frontend builder interaction and page composition remain in:

- `C:/dev/projects/work/config-center-frontend/docs/superpowers/plans/2026-03-19-smart-job-launch-implementation.md`

Backend responsibilities in this plan:

- job scene persistence
- scene/node versioning and publish snapshots
- graph and variable validation
- execution instance and node log APIs
- recent run summaries for the frontend list page

## Frontend Contract Sync (2026-03-19)

Latest frontend behavior that backend contract must align with:

1. Node property panel is hidden when no node is selected.
2. `page_get.field` is selected from page-bound fields (no free-text field input in UI).
3. `list_lookup.inputSource` now has explicit type control:
   - `STRING`: manual input
   - `REFERENCE`: select from upstream output references
4. In `REFERENCE` mode, frontend does not expose editable raw template text. Backend should persist and validate canonical reference values (for example `{{node_12_api_result}}`).
5. Builder no longer has standalone "save node property" action; "save layout" must persist current node details and layout together.

## File Structure

### Existing files to extend

- Modify: `src/main/java/com/configcenter/backend/control/publish/PublishService.java`
- Modify: `src/main/java/com/configcenter/backend/control/publish/PublishController.java`
- Modify: `src/main/resources/schema-h2.sql`
- Modify: `src/main/resources/data-h2.sql`
- Modify: `src/test/java/com/configcenter/backend/control/ControlModuleMigrationTest.java`

### New control-layer files

- Create: `src/main/java/com/configcenter/backend/control/jobscene/JobSceneController.java`
- Create: `src/main/java/com/configcenter/backend/control/jobscene/JobSceneService.java`
- Create: `src/main/java/com/configcenter/backend/control/jobscene/JobSceneValidationService.java`
- Create: `src/main/java/com/configcenter/backend/control/jobscene/JobSceneExecutionService.java`

### New DTO / domain files

- Create: `src/main/java/com/configcenter/backend/control/jobscene/dto/JobSceneSaveRequest.java`
- Create: `src/main/java/com/configcenter/backend/control/jobscene/dto/JobSceneNodeRequest.java`
- Create: `src/main/java/com/configcenter/backend/control/jobscene/dto/JobSceneListItemResponse.java`
- Create: `src/main/java/com/configcenter/backend/control/jobscene/dto/JobExecutionDetailResponse.java`
- Create: `src/main/java/com/configcenter/backend/control/jobscene/dto/JobSceneValidationResponse.java`

### New persistence files

- Create: `src/main/java/com/configcenter/backend/infrastructure/db/control/jobscene/JobSceneMapper.java`
- Create: `src/main/java/com/configcenter/backend/infrastructure/db/control/jobscene/JobSceneVersionMapper.java`
- Create: `src/main/java/com/configcenter/backend/infrastructure/db/control/jobscene/JobSceneNodeMapper.java`
- Create: `src/main/java/com/configcenter/backend/infrastructure/db/control/jobscene/JobExecutionMapper.java`
- Create: `src/main/java/com/configcenter/backend/infrastructure/db/control/jobscene/JobExecutionLogMapper.java`
- Create: `src/main/java/com/configcenter/backend/infrastructure/db/control/jobscene/model/JobSceneDO.java`
- Create: `src/main/java/com/configcenter/backend/infrastructure/db/control/jobscene/model/JobSceneVersionDO.java`
- Create: `src/main/java/com/configcenter/backend/infrastructure/db/control/jobscene/model/JobSceneNodeDO.java`
- Create: `src/main/java/com/configcenter/backend/infrastructure/db/control/jobscene/model/JobExecutionDO.java`
- Create: `src/main/java/com/configcenter/backend/infrastructure/db/control/jobscene/model/JobExecutionLogDO.java`

### New tests

- Create: `src/test/java/com/configcenter/backend/control/jobscene/JobSceneControllerTest.java`
- Create: `src/test/java/com/configcenter/backend/control/jobscene/JobSceneValidationServiceTest.java`
- Create: `src/test/java/com/configcenter/backend/control/jobscene/JobSceneExecutionServiceTest.java`

## Task 1: Add typed Smart Job API skeleton

**Files:**
- Create: `src/main/java/com/configcenter/backend/control/jobscene/JobSceneController.java`
- Create: `src/main/java/com/configcenter/backend/control/jobscene/JobSceneService.java`
- Create: `src/main/java/com/configcenter/backend/control/jobscene/dto/JobSceneSaveRequest.java`
- Create: `src/main/java/com/configcenter/backend/control/jobscene/dto/JobSceneNodeRequest.java`
- Create: `src/test/java/com/configcenter/backend/control/jobscene/JobSceneControllerTest.java`

- [ ] **Step 1: Write the failing controller test**

```java
@SpringBootTest(classes = ConfigCenterApiServerApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("h2")
class JobSceneControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldExposeJobSceneListEndpoint() throws Exception {
        mockMvc.perform(get("/api/control/job-scenes"))
                .andExpect(status().isOk());
    }
}
```

- [ ] **Step 2: Run the controller test to verify it fails**

Run: `mvn -q -Dtest=JobSceneControllerTest test`
Expected: FAIL with 404 or missing class

- [ ] **Step 3: Create typed request DTOs**

Use typed request objects instead of `Map<String, Object>`, for example:

```java
public record JobSceneSaveRequest(
        Long id,
        String name,
        Long pageResourceId,
        String executionMode,
        Boolean previewBeforeExecute,
        Integer manualDurationSec,
        List<JobSceneNodeRequest> nodes
) {}
```

Also include node-level fields needed by the new frontend contract in `JobSceneNodeRequest`, especially:

- `field` (for `page_get`)
- `inputSourceType` (`STRING` / `REFERENCE`, for `list_lookup`)
- `inputSource`
- `resultKey`

- [ ] **Step 4: Create controller and service skeleton**

Add endpoints for:

- `GET /api/control/job-scenes`
- `GET /api/control/job-scenes/{sceneId}`
- `POST /api/control/job-scenes/drafts`
- `POST /api/control/job-scenes/{sceneId}/validate`
- `GET /api/control/job-scenes/{sceneId}/executions`
- `GET /api/control/job-scenes/executions/{executionId}`

- [ ] **Step 5: Run the controller test again**

Run: `mvn -q -Dtest=JobSceneControllerTest test`
Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add src/main/java/com/configcenter/backend/control/jobscene src/test/java/com/configcenter/backend/control/jobscene/JobSceneControllerTest.java
git commit -m "feat: add smart job control api skeleton"
```

## Task 2: Add persistence tables and MyBatis mappers

**Files:**
- Modify: `src/main/resources/schema-h2.sql`
- Modify: `src/main/resources/data-h2.sql`
- Create: `src/main/java/com/configcenter/backend/infrastructure/db/control/jobscene/JobSceneMapper.java`
- Create: `src/main/java/com/configcenter/backend/infrastructure/db/control/jobscene/JobSceneVersionMapper.java`
- Create: `src/main/java/com/configcenter/backend/infrastructure/db/control/jobscene/JobSceneNodeMapper.java`
- Create: `src/main/java/com/configcenter/backend/infrastructure/db/control/jobscene/JobExecutionMapper.java`
- Create: `src/main/java/com/configcenter/backend/infrastructure/db/control/jobscene/JobExecutionLogMapper.java`
- Create: `src/main/java/com/configcenter/backend/infrastructure/db/control/jobscene/model/JobSceneDO.java`
- Create: `src/main/java/com/configcenter/backend/infrastructure/db/control/jobscene/model/JobSceneVersionDO.java`
- Create: `src/main/java/com/configcenter/backend/infrastructure/db/control/jobscene/model/JobSceneNodeDO.java`
- Create: `src/main/java/com/configcenter/backend/infrastructure/db/control/jobscene/model/JobExecutionDO.java`
- Create: `src/main/java/com/configcenter/backend/infrastructure/db/control/jobscene/model/JobExecutionLogDO.java`

- [ ] **Step 1: Add the failing integration test for schema-backed listing**

Extend `JobSceneControllerTest.java`:

```java
@Test
void shouldReturnSeededJobScenes() throws Exception {
    mockMvc.perform(get("/api/control/job-scenes"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.records[0].name").exists());
}
```

- [ ] **Step 2: Run the controller test to verify it fails**

Run: `mvn -q -Dtest=JobSceneControllerTest test`
Expected: FAIL because no tables or seed data exist

- [ ] **Step 3: Add H2 schema**

Create tables with the same audit pattern as existing resources:

- `job_scene_definition`
- `job_scene_version`
- `job_scene_node`
- `job_execution`
- `job_execution_log`

Key columns to include:

- scene and version ids
- `status`
- `current_version_id`
- `graph_json`
- `output_variables_json`
- `result`
- `trigger_source`
- `detail`

- [ ] **Step 4: Add seed data and mappers**

Seed one active scene with:

- one published version
- three nodes
- one execution
- matching node logs

Add mapper methods for list/detail/save/query execution logs.

- [ ] **Step 5: Run the controller test again**

Run: `mvn -q -Dtest=JobSceneControllerTest test`
Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add src/main/resources/schema-h2.sql src/main/resources/data-h2.sql src/main/java/com/configcenter/backend/infrastructure/db/control/jobscene
git commit -m "feat: persist smart job scenes and executions"
```

## Task 3: Implement graph and variable validation

**Files:**
- Create: `src/main/java/com/configcenter/backend/control/jobscene/JobSceneValidationService.java`
- Create: `src/test/java/com/configcenter/backend/control/jobscene/JobSceneValidationServiceTest.java`
- Modify: `src/main/java/com/configcenter/backend/control/jobscene/JobSceneService.java`
- Modify: `src/main/java/com/configcenter/backend/control/jobscene/dto/JobSceneValidationResponse.java`

- [ ] **Step 1: Write the failing validation tests**

```java
class JobSceneValidationServiceTest {

    private final JobSceneValidationService service = new JobSceneValidationService();

    @Test
    void shouldBlockBrokenVariableReferences() {
        JobSceneValidationResponse response = service.validateDraft(sampleDraftWithBrokenReference());
        assertFalse(response.canPublish());
        assertTrue(response.blockingIssues().contains("broken-variable-reference"));
    }
}
```

- [ ] **Step 2: Run the validation test to verify it fails**

Run: `mvn -q -Dtest=JobSceneValidationServiceTest test`
Expected: FAIL because validation service does not exist yet

- [ ] **Step 3: Implement validation rules**

Validation must cover:

- start path exists
- terminal path exists
- no isolated nodes
- required node inputs present
- output variable names unique per scene version
- downstream references remain unresolved when source node/output is removed
- `page_get.field` must exist in bound page fields of scene `pageResourceId`
- `list_lookup.inputSourceType = REFERENCE` requires canonical reference format and valid upstream variable existence
- `list_lookup.inputSourceType = STRING` treats `inputSource` as literal value (no reference parsing)

Do not auto-remap downstream references server-side.

- [ ] **Step 4: Return structured validation response**

Expose:

- `canSaveDraft`
- `canPublish`
- `blockingIssues`
- `warningIssues`
- `affectedNodes`
- `brokenVariables`

- [ ] **Step 5: Run validation tests**

Run: `mvn -q -Dtest=JobSceneValidationServiceTest test`
Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add src/main/java/com/configcenter/backend/control/jobscene/JobSceneValidationService.java src/test/java/com/configcenter/backend/control/jobscene/JobSceneValidationServiceTest.java src/main/java/com/configcenter/backend/control/jobscene/JobSceneService.java src/main/java/com/configcenter/backend/control/jobscene/dto/JobSceneValidationResponse.java
git commit -m "feat: validate smart job graphs and variables"
```

## Task 4: Add draft/version/publish flow for Smart Job scenes

**Files:**
- Modify: `src/main/java/com/configcenter/backend/control/jobscene/JobSceneService.java`
- Modify: `src/main/java/com/configcenter/backend/control/publish/PublishService.java`
- Modify: `src/main/java/com/configcenter/backend/control/publish/PublishController.java`
- Modify: `src/main/java/com/configcenter/backend/infrastructure/db/control/publish/PublishTaskMapper.java`
- Modify: `src/test/java/com/configcenter/backend/control/jobscene/JobSceneControllerTest.java`

- [ ] **Step 1: Extend the failing controller test**

```java
@Test
void shouldCreateDraftAndPublishJobScene() throws Exception {
    mockMvc.perform(post("/api/control/job-scenes/drafts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(validDraftBody()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.status").value("DRAFT"));
}
```

- [ ] **Step 2: Run the controller test to verify it fails**

Run: `mvn -q -Dtest=JobSceneControllerTest test`
Expected: FAIL because save/publish flow is incomplete

- [ ] **Step 3: Implement version-safe draft behavior**

Rules:

- editing an active scene creates a new draft version
- publish freezes the current graph and output-variable snapshot
- executions always point back to the published version id
- drafts never mutate the active version in place
- save layout should atomically persist both current node detail payload and graph position changes

- [ ] **Step 4: Extend publish support**

Allow publish validation and publish task creation for `JOB_SCENE` resources, reusing the current publish module rather than creating a parallel publish flow.

- [ ] **Step 5: Run targeted tests**

Run: `mvn -q -Dtest=JobSceneControllerTest test`
Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add src/main/java/com/configcenter/backend/control/jobscene/JobSceneService.java src/main/java/com/configcenter/backend/control/publish/PublishService.java src/main/java/com/configcenter/backend/control/publish/PublishController.java src/main/java/com/configcenter/backend/infrastructure/db/control/publish/PublishTaskMapper.java src/test/java/com/configcenter/backend/control/jobscene/JobSceneControllerTest.java
git commit -m "feat: add smart job draft and publish flow"
```

## Task 5: Add execution history, node logs, and recent run summaries

**Files:**
- Create: `src/main/java/com/configcenter/backend/control/jobscene/JobSceneExecutionService.java`
- Create: `src/main/java/com/configcenter/backend/control/jobscene/dto/JobExecutionDetailResponse.java`
- Create: `src/test/java/com/configcenter/backend/control/jobscene/JobSceneExecutionServiceTest.java`
- Modify: `src/main/java/com/configcenter/backend/control/jobscene/JobSceneController.java`
- Modify: `src/main/java/com/configcenter/backend/control/jobscene/JobSceneService.java`
- Modify: `src/main/java/com/configcenter/backend/infrastructure/db/control/jobscene/JobExecutionMapper.java`
- Modify: `src/main/java/com/configcenter/backend/infrastructure/db/control/jobscene/JobExecutionLogMapper.java`

- [ ] **Step 1: Write the failing execution-service test**

```java
class JobSceneExecutionServiceTest {

    @Test
    void shouldReturnExecutionDetailWithNodeLogsAndManualFallbackHint() {
        JobExecutionDetailResponse detail = service.getExecutionDetail(8001L);
        assertEquals("FAILED", detail.result());
        assertFalse(detail.nodeLogs().isEmpty());
        assertTrue(detail.manualHandlingAdvice().contains("人工处理"));
    }
}
```

- [ ] **Step 2: Run the execution-service test to verify it fails**

Run: `mvn -q -Dtest=JobSceneExecutionServiceTest test`
Expected: FAIL because execution-detail aggregation does not exist

- [ ] **Step 3: Implement execution queries**

Add endpoints and service methods for:

- scene execution list
- execution detail
- node log list
- recent run summary per scene

Support explicit log states:

- `SUCCESS`
- `FAILED`
- `SKIPPED`
- `NOT_RUN`

- [ ] **Step 4: Run targeted tests**

Run: `mvn -q -Dtest=JobSceneExecutionServiceTest test`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/configcenter/backend/control/jobscene/JobSceneExecutionService.java src/main/java/com/configcenter/backend/control/jobscene/dto/JobExecutionDetailResponse.java src/main/java/com/configcenter/backend/control/jobscene/JobSceneController.java src/main/java/com/configcenter/backend/control/jobscene/JobSceneService.java src/main/java/com/configcenter/backend/infrastructure/db/control/jobscene/JobExecutionMapper.java src/main/java/com/configcenter/backend/infrastructure/db/control/jobscene/JobExecutionLogMapper.java src/test/java/com/configcenter/backend/control/jobscene/JobSceneExecutionServiceTest.java
git commit -m "feat: add smart job execution observability apis"
```

## Task 6: Final verification and migration safety

**Files:**
- Modify: `src/test/java/com/configcenter/backend/control/ControlModuleMigrationTest.java`
- Modify: `docs/superpowers/plans/2026-03-19-smart-job-backend-implementation.md`
- Verify: `mvn test`

- [ ] **Step 1: Extend the module migration test**

Add assertions that:

- `/api/control/job-scenes` returns 200
- `/api/control/job-scenes/{sceneId}/executions` returns 200

- [ ] **Step 2: Run focused tests**

Run: `mvn -q -Dtest=JobSceneControllerTest,JobSceneValidationServiceTest,JobSceneExecutionServiceTest,ControlModuleMigrationTest test`
Expected: PASS

- [ ] **Step 3: Run the full backend test suite**

Run: `mvn test`
Expected: PASS

- [ ] **Step 4: Record residual backend integration risks**

Document any remaining gaps, especially:

- asynchronous execution engine not yet wired
- cross-resource snapshot consistency if rule/page versions change after publish
- production log retention policy

- [ ] **Step 5: Commit**

```bash
git add src/test/java/com/configcenter/backend/control/ControlModuleMigrationTest.java docs/superpowers/plans/2026-03-19-smart-job-backend-implementation.md
git commit -m "test: verify smart job backend launch plan"
```

## Verification Checklist

- `mvn -q -Dtest=JobSceneControllerTest test`
- `mvn -q -Dtest=JobSceneValidationServiceTest test`
- `mvn -q -Dtest=JobSceneExecutionServiceTest test`
- `mvn -q -Dtest=ControlModuleMigrationTest test`
- `mvn test`

## Review Notes

No dedicated `plan-document-reviewer` subagent is available in this environment, so do a manual review before execution:

- confirm typed DTOs replace loose `Map<String, Object>` usage for Smart Job APIs
- confirm publish flow reuses existing publish infrastructure instead of duplicating it
- confirm variable uniqueness is enforced but downstream references are never auto-remapped
- confirm every API needed by the frontend list/builder/execution views exists in this plan
