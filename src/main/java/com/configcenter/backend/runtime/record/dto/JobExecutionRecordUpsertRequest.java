package com.configcenter.backend.runtime.record.dto;

public record JobExecutionRecordUpsertRequest(
        Long sceneId,
        String sceneName,
        String triggerSource,
        String result,
        Boolean fallbackToManual,
        String detail,
        String startedAt,
        String finishedAt,
        String userId
) {
}

