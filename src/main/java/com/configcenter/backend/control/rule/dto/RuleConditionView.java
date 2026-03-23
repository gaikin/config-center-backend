package com.configcenter.backend.control.rule.dto;

import com.fasterxml.jackson.databind.JsonNode;

public record RuleConditionView(
        Long id,
        Long ruleId,
        Long groupId,
        JsonNode left,
        String operator,
        JsonNode right,
        String updatedAt
) {
}
