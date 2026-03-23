package com.configcenter.backend.runtime.record.dto;

public record PromptTriggerLogUpsertRequest(
        Long ruleId,
        String ruleName,
        Long pageResourceId,
        String pageResourceName,
        String orgId,
        String orgName,
        String promptMode,
        String promptContentSummary,
        Long sceneId,
        String sceneName,
        String triggerResult,
        String reason,
        String triggerAt,
        String userId,
        String traceId
) {
}

