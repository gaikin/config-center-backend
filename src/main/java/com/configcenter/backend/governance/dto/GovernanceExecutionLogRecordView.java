package com.configcenter.backend.governance.dto;

public record GovernanceExecutionLogRecordView(
        Long executionId,
        Long sceneId,
        String status
) {
}
