package com.configcenter.backend.control.publish.dto;

import java.util.List;

public record PublishAuditLogView(
        Long id,
        String action,
        String resourceType,
        Long resourceId,
        String resourceName,
        String operator,
        String effectiveScopeType,
        List<String> effectiveOrgIds,
        String effectiveScopeSummary,
        String effectiveStartAt,
        String effectiveEndAt,
        String approvalTicketId,
        String approvalSource,
        String approvalStatus,
        String createdAt
) {
}
