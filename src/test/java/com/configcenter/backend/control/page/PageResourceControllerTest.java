package com.configcenter.backend.control.page;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.configcenter.backend.bootstrap.ConfigCenterApiServerApplication;
import static org.hamcrest.Matchers.nullValue;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = ConfigCenterApiServerApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("h2")
class PageResourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void pageMenusShouldReturnRegionFirstObjects() throws Exception {
        mockMvc.perform(get("/api/control/page-menus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body[0].regionId").value("trade"))
                .andExpect(jsonPath("$.body[0].menuCode").value("loan-apply"));
    }

    @Test
    void createPageMenuShouldPersistRow() throws Exception {
        mockMvc.perform(post("/api/control/page-menus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "regionId": "trade",
                                  "menuCode": "manual-check",
                                  "menuName": "Manual Check",
                                  "urlPattern": "/manual/check",
                                  "status": "ACTIVE"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.menuCode").value("manual-check"))
                .andExpect(jsonPath("$.body.menuName").value("Manual Check"))
                .andExpect(jsonPath("$.body.regionId").value("trade"));
    }

    @Test
    void pageResourcesShouldReturnConcreteRows() throws Exception {
        mockMvc.perform(get("/api/control/page-resources"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.records[0].name").value("Loan Apply Page"))
                .andExpect(jsonPath("$.body.records[0].menuCode").value("loan-process"))
                .andExpect(jsonPath("$.body.records[0].menuId").doesNotExist())
                .andExpect(jsonPath("$.body.records[0].currentVersion").value(1))
                .andExpect(jsonPath("$.body.records[0].detectRulesSummary").value("URL兜底"));
    }

    @Test
    void createPageResourceShouldPersistCodeBasedOwnership() throws Exception {
        mockMvc.perform(post("/api/control/page-resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "menuCode": "loan-apply",
                                  "pageCode": "loan.apply.audit",
                                  "frameCode": "loan-audit-frame",
                                  "name": "Loan Audit Page",
                                  "status": "ACTIVE",
                                  "ownerOrgId": "org.demo",
                                  "detectRulesSummary": "URL兜底"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.menuCode").value("loan-apply"))
                .andExpect(jsonPath("$.body.pageCode").value("loan.apply.audit"));
    }

    @Test
    void createPageResourceShouldRejectMissingMenuCode() throws Exception {
        mockMvc.perform(post("/api/control/page-resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "pageCode": "loan.apply.preview",
                                  "frameCode": "loan-preview-frame",
                                  "name": "Loan Preview Page",
                                  "status": "ACTIVE",
                                  "ownerOrgId": "org.demo",
                                  "detectRulesSummary": "URL兜底"
                                }
                                """))
                .andExpect(status().is5xxServerError());
    }
}
