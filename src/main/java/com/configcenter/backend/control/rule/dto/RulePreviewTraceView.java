package com.configcenter.backend.control.rule.dto;

public record RulePreviewTraceView(
        Long conditionId,
        String expression,
        String leftValue,
        String rightValue,
        boolean passed,
        String reason
) {
}
