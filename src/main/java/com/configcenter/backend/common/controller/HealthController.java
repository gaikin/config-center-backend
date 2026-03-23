package com.configcenter.backend.common.controller;

import com.configcenter.backend.common.api.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/healthz")
    public ApiResponse<HealthView> health() {
        return ApiResponse.success(new HealthView("config-center-backend", "UP"));
    }
}
