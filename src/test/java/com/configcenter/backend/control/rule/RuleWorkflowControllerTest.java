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
class RuleWorkflowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listRuleConditionGroupsShouldReturnConcreteRows() throws Exception {
        mockMvc.perform(get("/api/control/rules/300/condition-groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body[0].ruleId").value(300))
                .andExpect(jsonPath("$.body[0].logicType").value("AND"));
    }

    @Test
    void createRuleConditionGroupShouldPersistConcreteRow() throws Exception {
        mockMvc.perform(post("/api/control/rules/300/condition-groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "logicType": "OR"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.ruleId").value(300))
                .andExpect(jsonPath("$.body.logicType").value("OR"));
    }
}
