package com.configcenter.backend.control;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
class ControlModuleMigrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void controlEndpointsShouldStayAvailableAfterMigration() throws Exception {
        mockMvc.perform(get("/api/control/page-sites"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/control/interfaces"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/control/rules"))
                .andExpect(status().isOk());
    }

    @Test
    void legacyControlClassesShouldBeRemovedFromModulePackage() {
        assertThrows(ClassNotFoundException.class,
                () -> Class.forName("com.configcenter.backend.module.control.page.PageResourceController"));
        assertThrows(ClassNotFoundException.class,
                () -> Class.forName("com.configcenter.backend.module.control.interfaceapi.InterfaceRegistryController"));
        assertThrows(ClassNotFoundException.class,
                () -> Class.forName("com.configcenter.backend.module.control.rule.RuleController"));
        assertThrows(ClassNotFoundException.class,
                () -> Class.forName("com.configcenter.backend.module.control.publish.PublishController"));
    }
}
