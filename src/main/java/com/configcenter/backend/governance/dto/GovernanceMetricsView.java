package com.configcenter.backend.governance.dto;

import java.util.List;

public record GovernanceMetricsView(
        double executionSuccessRate,
        int avgSavedSeconds,
        List<String> failureReasonTopN,
        int expiredResourceCount,
        int expiringSoonResourceCount
) {
}
