package com.configcenter.backend.control.page;

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
class PageMappingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listPageElementsShouldReturnRows() throws Exception {
        mockMvc.perform(get("/api/control/page-resources/100/elements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body[0].pageResourceId").value(100))
                .andExpect(jsonPath("$.body[0].selectorType").exists());
    }

    @Test
    void upsertPageElementShouldPersistRow() throws Exception {
        mockMvc.perform(post("/api/control/page-elements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "pageResourceId": 100,
                                  "logicName": "customer_id",
                                  "selector": "//*[@id='customer_id']",
                                  "selectorType": "XPATH",
                                  "frameLocation": "main-frame"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.pageResourceId").value(100))
                .andExpect(jsonPath("$.body.logicName").value("customer_id"));
    }

    @Test
    void listBusinessFieldsShouldReturnGlobalAndPageScopeRows() throws Exception {
        mockMvc.perform(get("/api/control/business-fields?pageResourceId=100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body[0].code").exists())
                .andExpect(jsonPath("$.body[0].scope").exists());
    }

    @Test
    void upsertPageFieldBindingAndDeleteShouldSucceed() throws Exception {
        mockMvc.perform(post("/api/control/page-field-bindings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": 6299,
                                  "pageResourceId": 100,
                                  "businessFieldCode": "field_risk_level",
                                  "pageElementId": 6002,
                                  "required": false
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.id").value(6299))
                .andExpect(jsonPath("$.body.pageResourceId").value(100));

        mockMvc.perform(post("/api/control/page-field-bindings/6299"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body").value(true));
    }
}
