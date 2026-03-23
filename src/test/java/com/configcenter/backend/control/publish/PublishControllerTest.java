package com.configcenter.backend.control.publish;

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
class PublishControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void logsShouldReturnConcreteRows() throws Exception {
        mockMvc.perform(get("/api/control/publish/logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body[0].action").value("PUBLISH"))
                .andExpect(jsonPath("$.body[0].resourceType").value("PAGE_RESOURCE"));
    }
}
