package com.configcenter.backend.control.dataprocessor;

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
class DataProcessorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listShouldReturnSeedRows() throws Exception {
        mockMvc.perform(get("/api/control/data-processors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body[0].id").value(3511))
                .andExpect(jsonPath("$.body[0].usedByCount").value(22));
    }

    @Test
    void upsertShouldPersistConcreteRow() throws Exception {
        mockMvc.perform(post("/api/control/data-processors")
                        .contentType(MediaType.APPLICATION_JSON)
                .content("""
                                {
                                  "name": "trim-text",
                                  "paramCount": 1,
                                  "functionCode": "function transform(input) { return String(input ?? '').slice(0, 8); }",
                                  "status": "ACTIVE"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.name").value("trim-text"))
                .andExpect(jsonPath("$.body.usedByCount").value(0));
    }

    @Test
    void statusUpdateShouldReturnConcreteRow() throws Exception {
        mockMvc.perform(post("/api/control/data-processors/3511/status")
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
