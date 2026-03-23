package com.configcenter.backend.control.rule.dto;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

public record RuleDefinitionUpsertRequest(
        Long id,
        String name,
        String ruleScope,
        String ruleSetCode,
        Long pageResourceId,
        String pageResourceName,
        Long sourceRuleId,
        String sourceRuleName,
        String shareMode,
        List<String> sharedOrgIds,
        String sharedBy,
        String sharedAt,
        Integer priority,
        String promptMode,
        String closeMode,
        String promptContentConfigJson,
        Integer closeTimeoutSec,
        Boolean hasConfirmButton,
        Long sceneId,
        String sceneName,
        String effectiveStartAt,
        String effectiveEndAt,
        String status,
        Integer currentVersion,
        String ownerOrgId,
        JsonNode listLookupConditions,
        String updatedAt
) {
}
