package com.configcenter.backend.governance.generalconfig;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
class GeneralConfigControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listShouldReturnPlatformRuntimeRows() throws Exception {
        mockMvc.perform(get("/api/governance/general-config-items?groupKey=platform-runtime"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body[0].groupKey").value("platform-runtime"))
                .andExpect(jsonPath("$.body[0].itemKey").value("promptStableVersion"));
    }

    @Test
    void upsertShouldPersistByNaturalKey() throws Exception {
        mockMvc.perform(post("/api/governance/general-config-items")
                        .contentType("application/json")
                        .content("""
                                {
                                  "groupKey": "platform-runtime",
                                  "itemKey": "promptStableVersion",
                                  "itemValue": "9.9.9",
                                  "description": "测试保存",
                                  "status": "ACTIVE",
                                  "orderNo": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.groupKey").value("platform-runtime"))
                .andExpect(jsonPath("$.body.itemValue").value("9.9.9"));
    }
}
