package com.configcenter.backend.governance;

import com.configcenter.backend.common.api.ApiResponse;
import com.configcenter.backend.governance.dto.MenuSdkPolicyUpsertRequest;
import com.configcenter.backend.governance.dto.MenuSdkPolicyView;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/governance")
public class GovernanceConfigController {

    private final GovernanceApplicationService governanceApplicationService;

    public GovernanceConfigController(GovernanceApplicationService governanceApplicationService) {
        this.governanceApplicationService = governanceApplicationService;
    }

    @GetMapping("/menu-sdk-policies")
    public ApiResponse<List<MenuSdkPolicyView>> menuSdkPolicies() {
        return ApiResponse.success(governanceApplicationService.listMenuSdkPolicies());
    }

    @PostMapping("/menu-sdk-policies")
    public ApiResponse<MenuSdkPolicyView> createMenuSdkPolicy(@RequestBody MenuSdkPolicyUpsertRequest payload) {
        return ApiResponse.success(governanceApplicationService.createMenuSdkPolicy(payload));
    }

    @PostMapping("/menu-sdk-policies/{id}")
    public ApiResponse<MenuSdkPolicyView> updateMenuSdkPolicy(
            @PathVariable Long id,
            @RequestBody MenuSdkPolicyUpsertRequest payload
    ) {
        return ApiResponse.success(governanceApplicationService.updateMenuSdkPolicy(id, payload));
    }
}

