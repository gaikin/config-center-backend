package com.configcenter.backend.runtime.record;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.configcenter.backend.bootstrap.ConfigCenterApiServerApplication;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = ConfigCenterApiServerApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("h2")
class RuntimeRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void promptTriggerLogsShouldReturnConcreteRows() throws Exception {
        mockMvc.perform(get("/api/runtime/prompt-trigger-logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body[0].ruleName").value("开户完整性提醒"))
                .andExpect(jsonPath("$.body[0].pageResourceName").value("Loan Apply Page"));
    }

    @Test
    void jobExecutionRecordsShouldReturnConcreteRows() throws Exception {
        mockMvc.perform(get("/api/runtime/job-execution-records"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body[0].sceneName").value("脚本链路测试"))
                .andExpect(jsonPath("$.body[0].pageResourceName").value("Loan Apply Page"));
    }

    @Test
    void createPromptTriggerLogShouldReturnGeneratedId() throws Exception {
        mockMvc.perform(post("/api/runtime/prompt-trigger-logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "ruleId": 300,
                                  "ruleName": "Large Amount Prompt",
                                  "pageResourceId": 100,
                                  "pageResourceName": "Loan Apply Page",
                                  "orgId": "org.demo",
                                  "orgName": "演示机构",
                                  "promptMode": "FLOATING",
                                  "promptContentSummary": "demo",
                                  "sceneId": 9001,
                                  "sceneName": "贷款申请自动查数预填",
                                  "triggerResult": "HIT",
                                  "reason": "sdk trigger",
                                  "triggerAt": "2026-03-23T12:30:00",
                                  "userId": "80334567",
                                  "traceId": "trace-unit-test"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body").isNumber());
    }

    @Test
    void createJobExecutionRecordShouldReturnGeneratedId() throws Exception {
        mockMvc.perform(post("/api/runtime/job-execution-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "sceneId": 9001,
                                  "sceneName": "贷款申请自动查数预填",
                                  "triggerSource": "PROMPT_TRIGGER",
                                  "result": "SUCCESS",
                                  "fallbackToManual": false,
                                  "detail": "unit test",
                                  "startedAt": "2026-03-23T12:30:00",
                                  "finishedAt": "2026-03-23T12:30:02",
                                  "userId": "80334567"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body").isNumber());
    }
}
