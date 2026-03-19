package com.configcenter.backend.runtime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
class RuntimeGovernanceMigrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void runtimeAndGovernanceEndpointsShouldStayAvailableAfterMigration() throws Exception {
        mockMvc.perform(post("/api/runtime/page-context/resolve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"menuCode\":\"loan-apply\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/runtime/pages/100/bundle"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/governance/pending-summary"))
                .andExpect(status().isOk());
    }

    @Test
    void legacyRuntimeAndGovernanceClassesShouldBeRemovedFromModulePackage() {
        assertThrows(ClassNotFoundException.class,
                () -> Class.forName("com.configcenter.backend.module.runtime.context.PageContextController"));
        assertThrows(ClassNotFoundException.class,
                () -> Class.forName("com.configcenter.backend.module.runtime.context.PageContextService"));
        assertThrows(ClassNotFoundException.class,
                () -> Class.forName("com.configcenter.backend.module.runtime.context.PageContextMapper"));
        assertThrows(ClassNotFoundException.class,
                () -> Class.forName("com.configcenter.backend.module.runtime.snapshot.SnapshotController"));
        assertThrows(ClassNotFoundException.class,
                () -> Class.forName("com.configcenter.backend.module.runtime.snapshot.SnapshotService"));
        assertThrows(ClassNotFoundException.class,
                () -> Class.forName("com.configcenter.backend.module.runtime.snapshot.RuntimeSnapshotMapper"));
        assertThrows(ClassNotFoundException.class,
                () -> Class.forName("com.configcenter.backend.module.governance.GovernanceController"));
        assertThrows(ClassNotFoundException.class,
                () -> Class.forName("com.configcenter.backend.module.governance.GovernanceService"));
    }
}
