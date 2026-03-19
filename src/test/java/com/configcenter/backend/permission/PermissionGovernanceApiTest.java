package com.configcenter.backend.permission;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
class PermissionGovernanceApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void sessionShouldAggregateResourcePathsFromRoleBindings() throws Exception {
        mockMvc.perform(get("/api/permissions/session/me")
                        .header("X-User-Id", "person-head-admin-a")
                        .header("X-Org-Id", "head-office"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.returnCode").value("OK"))
                .andExpect(jsonPath("$.body.userId").value("person-head-admin-a"))
                .andExpect(jsonPath("$.body.roles[0].name").exists())
                .andExpect(jsonPath("$.body.resourcePaths[0]").exists());
    }

    @Test
    void shouldReplaceRoleResourceGrants() throws Exception {
        mockMvc.perform(put("/api/permissions/roles/7003/resource-grants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"resourceCodes\":[\"menu_dashboard\",\"page_dashboard_list\"]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.updatedCount").value(2));

        mockMvc.perform(get("/api/permissions/roles/7003/resource-grants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body[0].resourceCode").exists());
    }

    @Test
    void shouldCreateAndUpdateRole() throws Exception {
        mockMvc.perform(post("/api/permissions/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"测试角色\",\"roleType\":\"CONFIG_OPERATOR\",\"status\":\"ACTIVE\",\"orgScopeId\":\"org.demo\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.id").value(7004));

        mockMvc.perform(put("/api/permissions/roles/7004")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"测试角色-更新\",\"roleType\":\"CONFIG_OPERATOR\",\"status\":\"ACTIVE\",\"orgScopeId\":\"org.demo\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.name").value("测试角色-更新"));
    }

    @Test
    void shouldUpdatePlatformRuntimeConfig() throws Exception {
        mockMvc.perform(put("/api/governance/platform-runtime-config")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"promptStableVersion\":\"1.5.0\",\"promptGrayDefaultVersion\":\"1.5.1-rc1\",\"jobStableVersion\":\"2.3.0\",\"jobGrayDefaultVersion\":\"2.3.1-rc1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.promptStableVersion").value("1.5.0"));

        mockMvc.perform(get("/api/governance/platform-runtime-config"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.jobStableVersion").value("2.3.0"));
    }

    @Test
    void shouldCreateAndListMenuSdkPolicy() throws Exception {
        mockMvc.perform(post("/api/governance/menu-sdk-policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"menuCode\":\"prompts\",\"menuName\":\"提示词管理\",\"promptGrayEnabled\":true,\"promptGrayVersion\":\"1.5.1-rc1\",\"promptGrayOrgIds\":[\"org.demo\"],\"jobGrayEnabled\":false,\"jobGrayOrgIds\":[],\"effectiveStart\":\"2026-03-18T00:00:00\",\"effectiveEnd\":\"2026-03-31T23:59:59\",\"status\":\"ACTIVE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.id").exists());

        mockMvc.perform(get("/api/governance/menu-sdk-policies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body[0].menuCode").exists());
    }
}
