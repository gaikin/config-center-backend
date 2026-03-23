package com.configcenter.backend.runtime.record.dto;

public record JobExecutionRecordView(
        Long id,
        Long sceneId,
        String sceneName,
        Long pageResourceId,
        String pageResourceName,
        String orgId,
        String orgName,
        String triggerSource,
        String result,
        String failureReasonSummary,
        String startedAt,
        String finishedAt
) {
}
