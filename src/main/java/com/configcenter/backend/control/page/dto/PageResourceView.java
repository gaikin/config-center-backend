package com.configcenter.backend.control.page.dto;

public record PageResourceView(
        Long id,
        String menuCode,
        String pageCode,
        String frameCode,
        String name,
        String status,
        String ownerOrgId,
        Integer currentVersion,
        Integer elementCount,
        String detectRulesSummary,
        String updatedAt
) {
}
