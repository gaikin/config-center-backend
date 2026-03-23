package com.configcenter.backend.control.rule.dto;

import com.fasterxml.jackson.databind.JsonNode;

public record RuleConditionUpsertRequest(
        Long id,
        Long ruleId,
        Long groupId,
        JsonNode left,
        String operator,
        JsonNode right
) {
}
