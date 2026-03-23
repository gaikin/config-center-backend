package com.configcenter.backend.runtime.sdk.dto;

import java.util.List;

public record RuntimePromptItemView(
        String ruleId,
        String ruleName,
        String traceId,
        String promptMode,
        String title,
        String content,
        String confirmText,
        String closeText,
        String sceneId,
        String sceneName,
        String pageResourceId,
        String pageResourceName,
        Integer priority,
        List<RuntimePromptFieldRuleView> fieldRules
) {
}
