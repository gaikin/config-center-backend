package com.configcenter.backend.control.contextvariable;

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
class ContextVariableControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listShouldReturnSeedRows() throws Exception {
        mockMvc.perform(get("/api/control/context-variables"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body[0].key").value("org_id"))
                .andExpect(jsonPath("$.body[0].ownerOrgId").value("head-office"));
    }

    @Test
    void upsertShouldPersistConcreteRow() throws Exception {
        mockMvc.perform(post("/api/control/context-variables")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "key": "customer_type",
                                  "label": "customer type",
                                  "valueSource": "STATIC",
                                  "staticValue": "VIP",
                                  "status": "ACTIVE",
                                  "ownerOrgId": "head-office"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.key").value("customer_type"))
                .andExpect(jsonPath("$.body.staticValue").value("VIP"));
    }

    @Test
    void statusUpdateShouldReturnConcreteRow() throws Exception {
        mockMvc.perform(post("/api/control/context-variables/3601/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "DISABLED"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.status").value("DISABLED"));
    }
}
