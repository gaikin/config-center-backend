package com.configcenter.backend.control.page.dto;

public record PageResourceUpsertRequest(
        Long id,
        String menuCode,
        @jakarta.validation.constraints.NotBlank String pageCode,
        String frameCode,
        @jakarta.validation.constraints.NotBlank String name,
        @jakarta.validation.constraints.NotBlank String status,
        @jakarta.validation.constraints.NotBlank String ownerOrgId,
        @jakarta.validation.constraints.NotBlank String detectRulesSummary
) {
}
