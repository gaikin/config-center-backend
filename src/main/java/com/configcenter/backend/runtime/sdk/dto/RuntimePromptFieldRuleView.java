package com.configcenter.backend.runtime.sdk.dto;

public record RuntimePromptFieldRuleView(
        String conditionId,
        String fieldKey,
        String selector,
        String operator,
        String expectedValue
) {
}
