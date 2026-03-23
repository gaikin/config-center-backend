package com.configcenter.backend.control.interfaceapi;

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
class InterfaceRegistryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listShouldReturnConcreteInterfaceRows() throws Exception {
        mockMvc.perform(get("/api/control/interfaces").param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.records[0].name").value("Customer Profile API"))
                .andExpect(jsonPath("$.body.records[0].prodPath").value("/internal/customer/profile"));
    }

    @Test
    void saveDraftShouldCloneFromActiveRow() throws Exception {
        mockMvc.perform(post("/api/control/interfaces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": 200,
                                  "name": "Customer Profile API - Draft",
                                  "description": "查询客户资料草稿",
                                  "method": "POST",
                                  "prodPath": "/internal/customer/profile",
                                  "ownerOrgId": "org.demo",
                                  "currentVersion": 2,
                                  "bodyTemplateJson": "",
                                  "inputConfigJson": "{}",
                                  "outputConfigJson": "[]",
                                  "paramSourceSummary": "Header 0 / Query 0 / Path 0 / Body 0",
                                  "responsePath": "$.data.customerId"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.status").value("DRAFT"))
                .andExpect(jsonPath("$.body.currentVersion").value(2))
                .andExpect(jsonPath("$.body.name").value("Customer Profile API - Draft"));
    }

    @Test
    void statusUpdateShouldReturnConcreteObject() throws Exception {
        mockMvc.perform(post("/api/control/interfaces/200/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "DISABLED"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.status").value("DISABLED"))
                .andExpect(jsonPath("$.body.name").value("Customer Profile API"));

        mockMvc.perform(post("/api/control/interfaces/200/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "ACTIVE"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.status").value("ACTIVE"));
    }
}
