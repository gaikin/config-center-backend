package com.configcenter.backend.governance.dto;

public record GovernanceAuditLogRecordView(
        Long id,
        String action,
        String resourceType,
        String operator
) {
}
