package com.configcenter.backend.governance;

import com.configcenter.backend.common.api.ApiResponse;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/governance")
public class GovernanceConfigController {

    private final GovernanceApplicationService governanceApplicationService;

    public GovernanceConfigController(GovernanceApplicationService governanceApplicationService) {
        this.governanceApplicationService = governanceApplicationService;
    }

    @GetMapping("/platform-runtime-config")
    public ApiResponse<Map<String, Object>> platformRuntimeConfig() {
        return ApiResponse.success(governanceApplicationService.getPlatformRuntimeConfig());
    }

    @PutMapping("/platform-runtime-config")
    public ApiResponse<Map<String, Object>> updatePlatformRuntimeConfig(@RequestBody Map<String, Object> payload) {
        return ApiResponse.success(governanceApplicationService.upsertPlatformRuntimeConfig(payload));
    }

    @GetMapping("/menu-sdk-policies")
    public ApiResponse<List<Map<String, Object>>> menuSdkPolicies() {
        return ApiResponse.success(governanceApplicationService.listMenuSdkPolicies());
    }

    @PostMapping("/menu-sdk-policies")
    public ApiResponse<Map<String, Object>> createMenuSdkPolicy(@RequestBody Map<String, Object> payload) {
        return ApiResponse.success(governanceApplicationService.createMenuSdkPolicy(payload));
    }

    @PutMapping("/menu-sdk-policies/{id}")
    public ApiResponse<Map<String, Object>> updateMenuSdkPolicy(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload
    ) {
        return ApiResponse.success(governanceApplicationService.updateMenuSdkPolicy(id, payload));
    }
}
