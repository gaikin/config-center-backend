package com.configcenter.backend.control.rule;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
class RuleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listShouldReturnConcreteRuleRows() throws Exception {
        mockMvc.perform(get("/api/control/rules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.records[0].name").value("Large Amount Prompt"))
                .andExpect(jsonPath("$.body.records[0].status").value("ACTIVE"));
    }

    @Test
    void versionEndpointShouldBeRemoved() throws Exception {
        mockMvc.perform(post("/api/control/rules/300/versions"))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.errorMsg").value(org.hamcrest.Matchers.containsString("No static resource")));
    }

    @Test
    void previewShouldUseCurrentRuleOnly() throws Exception {
        mockMvc.perform(post("/api/control/rules/300/preview")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "pageFields": {
                                    "loanAmount": 600000
                                  }
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.ruleId").value(300))
                .andExpect(jsonPath("$.body.matched").value(true))
                .andExpect(jsonPath("$.body.versionId").doesNotExist());
    }
}
