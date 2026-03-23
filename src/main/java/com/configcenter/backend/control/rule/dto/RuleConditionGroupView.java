package com.configcenter.backend.control.rule.dto;

public record RuleConditionGroupView(
        Long id,
        Long ruleId,
        String logicType,
        Long parentGroupId,
        String updatedAt
) {
}
