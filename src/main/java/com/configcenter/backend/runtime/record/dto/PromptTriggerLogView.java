package com.configcenter.backend.runtime.record.dto;

public record PromptTriggerLogView(
        Long id,
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
        String triggerAt,
        String triggerResult,
        String reason
) {
}
