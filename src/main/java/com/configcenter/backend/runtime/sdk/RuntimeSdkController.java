package com.configcenter.backend.runtime.sdk;

import com.configcenter.backend.common.api.ApiResponse;
import com.configcenter.backend.runtime.sdk.dto.RuntimeJobPackageView;
import com.configcenter.backend.runtime.sdk.dto.RuntimePromptBundleView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/runtime")
public class RuntimeSdkController {

    private final RuntimeSdkService runtimeSdkService;

    public RuntimeSdkController(RuntimeSdkService runtimeSdkService) {
        this.runtimeSdkService = runtimeSdkService;
    }

    @GetMapping("/pages/{pageId}/prompt-bundle")
    public ApiResponse<RuntimePromptBundleView> getPromptBundle(@PathVariable Long pageId) {
        return ApiResponse.success(runtimeSdkService.getPromptBundle(pageId));
    }

    @GetMapping("/job-package")
    public ApiResponse<RuntimeJobPackageView> getJobPackage(
            @RequestParam Long pageResourceId,
            @RequestParam(required = false) Long ruleId,
            @RequestParam(required = false) Long sceneId
    ) {
        return ApiResponse.success(runtimeSdkService.getJobPackage(pageResourceId, ruleId, sceneId));
    }
}

