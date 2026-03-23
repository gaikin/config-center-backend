package com.configcenter.backend.control.rule.dto;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

public record RuleDefinitionView(
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
    public RuleDefinitionView withId(Long value) {
        return new RuleDefinitionView(value, name, ruleScope, ruleSetCode, pageResourceId, pageResourceName, sourceRuleId, sourceRuleName, shareMode, sharedOrgIds, sharedBy, sharedAt, priority, promptMode, closeMode, promptContentConfigJson, closeTimeoutSec, hasConfirmButton, sceneId, sceneName, effectiveStartAt, effectiveEndAt, status, currentVersion, ownerOrgId, listLookupConditions, updatedAt);
    }

    public RuleDefinitionView withName(String value) {
        return new RuleDefinitionView(id, value, ruleScope, ruleSetCode, pageResourceId, pageResourceName, sourceRuleId, sourceRuleName, shareMode, sharedOrgIds, sharedBy, sharedAt, priority, promptMode, closeMode, promptContentConfigJson, closeTimeoutSec, hasConfirmButton, sceneId, sceneName, effectiveStartAt, effectiveEndAt, status, currentVersion, ownerOrgId, listLookupConditions, updatedAt);
    }

    public RuleDefinitionView withRuleScope(String value) {
        return new RuleDefinitionView(id, name, value, ruleSetCode, pageResourceId, pageResourceName, sourceRuleId, sourceRuleName, shareMode, sharedOrgIds, sharedBy, sharedAt, priority, promptMode, closeMode, promptContentConfigJson, closeTimeoutSec, hasConfirmButton, sceneId, sceneName, effectiveStartAt, effectiveEndAt, status, currentVersion, ownerOrgId, listLookupConditions, updatedAt);
    }

    public RuleDefinitionView withRuleSetCode(String value) {
        return new RuleDefinitionView(id, name, ruleScope, value, pageResourceId, pageResourceName, sourceRuleId, sourceRuleName, shareMode, sharedOrgIds, sharedBy, sharedAt, priority, promptMode, closeMode, promptContentConfigJson, closeTimeoutSec, hasConfirmButton, sceneId, sceneName, effectiveStartAt, effectiveEndAt, status, currentVersion, ownerOrgId, listLookupConditions, updatedAt);
    }

    public RuleDefinitionView withPageResourceId(Long value) {
        return new RuleDefinitionView(id, name, ruleScope, ruleSetCode, value, pageResourceName, sourceRuleId, sourceRuleName, shareMode, sharedOrgIds, sharedBy, sharedAt, priority, promptMode, closeMode, promptContentConfigJson, closeTimeoutSec, hasConfirmButton, sceneId, sceneName, effectiveStartAt, effectiveEndAt, status, currentVersion, ownerOrgId, listLookupConditions, updatedAt);
    }

    public RuleDefinitionView withPageResourceName(String value) {
        return new RuleDefinitionView(id, name, ruleScope, ruleSetCode, pageResourceId, value, sourceRuleId, sourceRuleName, shareMode, sharedOrgIds, sharedBy, sharedAt, priority, promptMode, closeMode, promptContentConfigJson, closeTimeoutSec, hasConfirmButton, sceneId, sceneName, effectiveStartAt, effectiveEndAt, status, currentVersion, ownerOrgId, listLookupConditions, updatedAt);
    }

    public RuleDefinitionView withSourceRuleId(Long value) {
        return new RuleDefinitionView(id, name, ruleScope, ruleSetCode, pageResourceId, pageResourceName, value, sourceRuleName, shareMode, sharedOrgIds, sharedBy, sharedAt, priority, promptMode, closeMode, promptContentConfigJson, closeTimeoutSec, hasConfirmButton, sceneId, sceneName, effectiveStartAt, effectiveEndAt, status, currentVersion, ownerOrgId, listLookupConditions, updatedAt);
    }

    public RuleDefinitionView withSourceRuleName(String value) {
        return new RuleDefinitionView(id, name, ruleScope, ruleSetCode, pageResourceId, pageResourceName, sourceRuleId, value, shareMode, sharedOrgIds, sharedBy, sharedAt, priority, promptMode, closeMode, promptContentConfigJson, closeTimeoutSec, hasConfirmButton, sceneId, sceneName, effectiveStartAt, effectiveEndAt, status, currentVersion, ownerOrgId, listLookupConditions, updatedAt);
    }

    public RuleDefinitionView withShareMode(String value) {
        return new RuleDefinitionView(id, name, ruleScope, ruleSetCode, pageResourceId, pageResourceName, sourceRuleId, sourceRuleName, value, sharedOrgIds, sharedBy, sharedAt, priority, promptMode, closeMode, promptContentConfigJson, closeTimeoutSec, hasConfirmButton, sceneId, sceneName, effectiveStartAt, effectiveEndAt, status, currentVersion, ownerOrgId, listLookupConditions, updatedAt);
    }

    public RuleDefinitionView withSharedOrgIds(List<String> value) {
        return new RuleDefinitionView(id, name, ruleScope, ruleSetCode, pageResourceId, pageResourceName, sourceRuleId, sourceRuleName, shareMode, value, sharedBy, sharedAt, priority, promptMode, closeMode, promptContentConfigJson, closeTimeoutSec, hasConfirmButton, sceneId, sceneName, effectiveStartAt, effectiveEndAt, status, currentVersion, ownerOrgId, listLookupConditions, updatedAt);
    }

    public RuleDefinitionView withSharedBy(String value) {
        return new RuleDefinitionView(id, name, ruleScope, ruleSetCode, pageResourceId, pageResourceName, sourceRuleId, sourceRuleName, shareMode, sharedOrgIds, value, sharedAt, priority, promptMode, closeMode, promptContentConfigJson, closeTimeoutSec, hasConfirmButton, sceneId, sceneName, effectiveStartAt, effectiveEndAt, status, currentVersion, ownerOrgId, listLookupConditions, updatedAt);
    }

    public RuleDefinitionView withSharedAt(String value) {
        return new RuleDefinitionView(id, name, ruleScope, ruleSetCode, pageResourceId, pageResourceName, sourceRuleId, sourceRuleName, shareMode, sharedOrgIds, sharedBy, value, priority, promptMode, closeMode, promptContentConfigJson, closeTimeoutSec, hasConfirmButton, sceneId, sceneName, effectiveStartAt, effectiveEndAt, status, currentVersion, ownerOrgId, listLookupConditions, updatedAt);
    }

    public RuleDefinitionView withPriority(Integer value) {
        return new RuleDefinitionView(id, name, ruleScope, ruleSetCode, pageResourceId, pageResourceName, sourceRuleId, sourceRuleName, shareMode, sharedOrgIds, sharedBy, sharedAt, value, promptMode, closeMode, promptContentConfigJson, closeTimeoutSec, hasConfirmButton, sceneId, sceneName, effectiveStartAt, effectiveEndAt, status, currentVersion, ownerOrgId, listLookupConditions, updatedAt);
    }

    public RuleDefinitionView withPromptMode(String value) {
        return new RuleDefinitionView(id, name, ruleScope, ruleSetCode, pageResourceId, pageResourceName, sourceRuleId, sourceRuleName, shareMode, sharedOrgIds, sharedBy, sharedAt, priority, value, closeMode, promptContentConfigJson, closeTimeoutSec, hasConfirmButton, sceneId, sceneName, effectiveStartAt, effectiveEndAt, status, currentVersion, ownerOrgId, listLookupConditions, updatedAt);
    }

    public RuleDefinitionView withCloseMode(String value) {
        return new RuleDefinitionView(id, name, ruleScope, ruleSetCode, pageResourceId, pageResourceName, sourceRuleId, sourceRuleName, shareMode, sharedOrgIds, sharedBy, sharedAt, priority, promptMode, value, promptContentConfigJson, closeTimeoutSec, hasConfirmButton, sceneId, sceneName, effectiveStartAt, effectiveEndAt, status, currentVersion, ownerOrgId, listLookupConditions, updatedAt);
    }

    public RuleDefinitionView withPromptContentConfigJson(String value) {
        return new RuleDefinitionView(id, name, ruleScope, ruleSetCode, pageResourceId, pageResourceName, sourceRuleId, sourceRuleName, shareMode, sharedOrgIds, sharedBy, sharedAt, priority, promptMode, closeMode, value, closeTimeoutSec, hasConfirmButton, sceneId, sceneName, effectiveStartAt, effectiveEndAt, status, currentVersion, ownerOrgId, listLookupConditions, updatedAt);
    }

    public RuleDefinitionView withCloseTimeoutSec(Integer value) {
        return new RuleDefinitionView(id, name, ruleScope, ruleSetCode, pageResourceId, pageResourceName, sourceRuleId, sourceRuleName, shareMode, sharedOrgIds, sharedBy, sharedAt, priority, promptMode, closeMode, promptContentConfigJson, value, hasConfirmButton, sceneId, sceneName, effectiveStartAt, effectiveEndAt, status, currentVersion, ownerOrgId, listLookupConditions, updatedAt);
    }

    public RuleDefinitionView withHasConfirmButton(Boolean value) {
        return new RuleDefinitionView(id, name, ruleScope, ruleSetCode, pageResourceId, pageResourceName, sourceRuleId, sourceRuleName, shareMode, sharedOrgIds, sharedBy, sharedAt, priority, promptMode, closeMode, promptContentConfigJson, closeTimeoutSec, value, sceneId, sceneName, effectiveStartAt, effectiveEndAt, status, currentVersion, ownerOrgId, listLookupConditions, updatedAt);
    }

    public RuleDefinitionView withSceneId(Long value) {
        return new RuleDefinitionView(id, name, ruleScope, ruleSetCode, pageResourceId, pageResourceName, sourceRuleId, sourceRuleName, shareMode, sharedOrgIds, sharedBy, sharedAt, priority, promptMode, closeMode, promptContentConfigJson, closeTimeoutSec, hasConfirmButton, value, sceneName, effectiveStartAt, effectiveEndAt, status, currentVersion, ownerOrgId, listLookupConditions, updatedAt);
    }

    public RuleDefinitionView withSceneName(String value) {
        return new RuleDefinitionView(id, name, ruleScope, ruleSetCode, pageResourceId, pageResourceName, sourceRuleId, sourceRuleName, shareMode, sharedOrgIds, sharedBy, sharedAt, priority, promptMode, closeMode, promptContentConfigJson, closeTimeoutSec, hasConfirmButton, sceneId, value, effectiveStartAt, effectiveEndAt, status, currentVersion, ownerOrgId, listLookupConditions, updatedAt);
    }

    public RuleDefinitionView withEffectiveStartAt(String value) {
        return new RuleDefinitionView(id, name, ruleScope, ruleSetCode, pageResourceId, pageResourceName, sourceRuleId, sourceRuleName, shareMode, sharedOrgIds, sharedBy, sharedAt, priority, promptMode, closeMode, promptContentConfigJson, closeTimeoutSec, hasConfirmButton, sceneId, sceneName, value, effectiveEndAt, status, currentVersion, ownerOrgId, listLookupConditions, updatedAt);
    }

    public RuleDefinitionView withEffectiveEndAt(String value) {
        return new RuleDefinitionView(id, name, ruleScope, ruleSetCode, pageResourceId, pageResourceName, sourceRuleId, sourceRuleName, shareMode, sharedOrgIds, sharedBy, sharedAt, priority, promptMode, closeMode, promptContentConfigJson, closeTimeoutSec, hasConfirmButton, sceneId, sceneName, effectiveStartAt, value, status, currentVersion, ownerOrgId, listLookupConditions, updatedAt);
    }

    public RuleDefinitionView withStatus(String value) {
        return new RuleDefinitionView(id, name, ruleScope, ruleSetCode, pageResourceId, pageResourceName, sourceRuleId, sourceRuleName, shareMode, sharedOrgIds, sharedBy, sharedAt, priority, promptMode, closeMode, promptContentConfigJson, closeTimeoutSec, hasConfirmButton, sceneId, sceneName, effectiveStartAt, effectiveEndAt, value, currentVersion, ownerOrgId, listLookupConditions, updatedAt);
    }

    public RuleDefinitionView withCurrentVersion(Integer value) {
        return new RuleDefinitionView(id, name, ruleScope, ruleSetCode, pageResourceId, pageResourceName, sourceRuleId, sourceRuleName, shareMode, sharedOrgIds, sharedBy, sharedAt, priority, promptMode, closeMode, promptContentConfigJson, closeTimeoutSec, hasConfirmButton, sceneId, sceneName, effectiveStartAt, effectiveEndAt, status, value, ownerOrgId, listLookupConditions, updatedAt);
    }

    public RuleDefinitionView withOwnerOrgId(String value) {
        return new RuleDefinitionView(id, name, ruleScope, ruleSetCode, pageResourceId, pageResourceName, sourceRuleId, sourceRuleName, shareMode, sharedOrgIds, sharedBy, sharedAt, priority, promptMode, closeMode, promptContentConfigJson, closeTimeoutSec, hasConfirmButton, sceneId, sceneName, effectiveStartAt, effectiveEndAt, status, currentVersion, value, listLookupConditions, updatedAt);
    }

    public RuleDefinitionView withListLookupConditions(JsonNode value) {
        return new RuleDefinitionView(id, name, ruleScope, ruleSetCode, pageResourceId, pageResourceName, sourceRuleId, sourceRuleName, shareMode, sharedOrgIds, sharedBy, sharedAt, priority, promptMode, closeMode, promptContentConfigJson, closeTimeoutSec, hasConfirmButton, sceneId, sceneName, effectiveStartAt, effectiveEndAt, status, currentVersion, ownerOrgId, value, updatedAt);
    }

    public RuleDefinitionView withUpdatedAt(String value) {
        return new RuleDefinitionView(id, name, ruleScope, ruleSetCode, pageResourceId, pageResourceName, sourceRuleId, sourceRuleName, shareMode, sharedOrgIds, sharedBy, sharedAt, priority, promptMode, closeMode, promptContentConfigJson, closeTimeoutSec, hasConfirmButton, sceneId, sceneName, effectiveStartAt, effectiveEndAt, status, currentVersion, ownerOrgId, listLookupConditions, value);
    }
}
