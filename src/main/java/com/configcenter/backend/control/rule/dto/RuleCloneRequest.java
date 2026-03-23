package com.configcenter.backend.control.rule.dto;

public record RuleCloneRequest(
        String targetOrgId,
        String operator
) {
}
