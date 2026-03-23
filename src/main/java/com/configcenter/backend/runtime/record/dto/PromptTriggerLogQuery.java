package com.configcenter.backend.runtime.record.dto;

public record PromptTriggerLogQuery(
        String keyword,
        Long pageResourceId,
        String orgId,
        String startAt,
        String endAt,
        Long ruleId
) {
}
