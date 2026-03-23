package com.configcenter.backend.runtime.sdk;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.configcenter.backend.bootstrap.ConfigCenterApiServerApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = ConfigCenterApiServerApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("h2")
class RuntimeSdkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void promptBundleShouldReturnPromptItems() throws Exception {
        mockMvc.perform(get("/api/runtime/pages/100/prompt-bundle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.pageResourceId").value("100"))
                .andExpect(jsonPath("$.body.prompts[0].ruleName").value("Large Amount Prompt"))
                .andExpect(jsonPath("$.body.prompts[0].fieldRules[0].fieldKey").value("risk_level"))
                .andExpect(jsonPath("$.body.prompts[0].fieldRules[0].operator").value("EQ"))
                .andExpect(jsonPath("$.body.prompts[0].fieldRules[0].expectedValue").value("HIGH"));
    }

    @Test
    void jobPackageShouldReturnSceneAndNodes() throws Exception {
        mockMvc.perform(get("/api/runtime/job-package?pageResourceId=100&ruleId=300"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.pageResourceId").value("100"))
                .andExpect(jsonPath("$.body.jobs[0].sceneId").value("9001"))
                .andExpect(jsonPath("$.body.jobs[0].nodes[0].nodeType").value("page_get"));
    }
}
