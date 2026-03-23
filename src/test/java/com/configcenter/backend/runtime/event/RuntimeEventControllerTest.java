package com.configcenter.backend.runtime.event;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.configcenter.backend.bootstrap.ConfigCenterApiServerApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = ConfigCenterApiServerApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("h2")
class RuntimeEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReportRuntimeEvents() throws Exception {
        mockMvc.perform(post("/api/runtime/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "events": [
                                    {
                                      "type": "PROMPT_TRIGGERED",
                                      "createdAt": "2026-03-23T12:30:00Z",
                                      "traceId": "trace-unit-test",
                                      "sdkVersion": "0.3.0",
                                      "bundleVersion": "prompt-100-1",
                                      "pageResourceId": "100",
                                      "ruleId": "300",
                                      "reason": "unit test trigger",
                                      "latencyMs": 12
                                    },
                                    {
                                      "type": "JOB_EXECUTION_FINISHED",
                                      "createdAt": "2026-03-23T12:30:02Z",
                                      "reason": "unit test finished"
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.returnCode").value("OK"))
                .andExpect(jsonPath("$.body").value(2));
    }
}
