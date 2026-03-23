package com.configcenter.backend.control.rule.dto;

import java.util.List;

public record RuleShareRequest(
        String shareMode,
        List<String> sharedOrgIds,
        String operator
) {
}
