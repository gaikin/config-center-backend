package com.configcenter.backend.control.rule.dto;

public record RuleConditionGroupUpsertRequest(
        String logicType,
        Long parentGroupId
) {
}
