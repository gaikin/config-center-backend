package com.configcenter.backend.governance;

import com.configcenter.backend.common.api.ApiResponse;
import com.configcenter.backend.governance.dto.GovernanceAuditLogsView;
import com.configcenter.backend.governance.dto.GovernanceExecutionLogsView;
import com.configcenter.backend.governance.dto.GovernanceMetricsView;
import com.configcenter.backend.governance.dto.GovernanceTriggerLogsView;
import com.configcenter.backend.governance.dto.PendingSummaryView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/governance")
public class GovernanceController {

    private final GovernanceService governanceService;

    public GovernanceController(GovernanceService governanceService) {
        this.governanceService = governanceService;
    }

    @GetMapping("/pending-summary")
    public ApiResponse<PendingSummaryView> pendingSummary() {
        return ApiResponse.success(governanceService.pendingSummary());
    }

    @GetMapping("/audit-logs")
    public ApiResponse<GovernanceAuditLogsView> auditLogs() {
        return ApiResponse.success(governanceService.auditLogs());
    }

    @GetMapping("/trigger-logs")
    public ApiResponse<GovernanceTriggerLogsView> triggerLogs() {
        return ApiResponse.success(governanceService.triggerLogs());
    }

    @GetMapping("/execution-logs")
    public ApiResponse<GovernanceExecutionLogsView> executionLogs() {
        return ApiResponse.success(governanceService.executionLogs());
    }

    @GetMapping("/metrics/overview")
    public ApiResponse<GovernanceMetricsView> metricsOverview() {
        return ApiResponse.success(governanceService.metricsOverview());
    }
}

