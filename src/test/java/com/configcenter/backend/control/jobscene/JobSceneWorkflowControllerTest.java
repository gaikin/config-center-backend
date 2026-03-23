package com.configcenter.backend.control.jobscene;

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
class JobSceneWorkflowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listJobNodesShouldReturnConcreteRows() throws Exception {
        mockMvc.perform(get("/api/control/job-scenes/9001/nodes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body[0].sceneId").value(9001))
                .andExpect(jsonPath("$.body[0].name").value("Page Get 1"));
    }

    @Test
    void upsertJobNodeShouldPersistConcreteRow() throws Exception {
        mockMvc.perform(post("/api/control/job-scenes/9001/nodes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "sceneId": 9001,
                                  "nodeType": "page_click",
                                  "name": "Page Click 1",
                                  "orderNo": 5,
                                  "enabled": true,
                                  "configJson": "{}"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.sceneId").value(9001))
                .andExpect(jsonPath("$.body.name").value("Page Click 1"));
    }
}
