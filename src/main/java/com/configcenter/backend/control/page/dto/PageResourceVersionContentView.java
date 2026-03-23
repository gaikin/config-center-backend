package com.configcenter.backend.control.page.dto;

public record PageResourceVersionContentView(
        String pageName,
        String pageCode,
        String frameCode,
        String detectRulesSummary,
        String ownerOrgId
) {
}
