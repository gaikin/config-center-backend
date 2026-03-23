package com.configcenter.backend.runtime.record.dto;

public record JobExecutionRecordQuery(
        String keyword,
        String result,
        Long pageResourceId,
        String orgId,
        String startAt,
        String endAt,
        Long sceneId,
        String sceneName
) {
}
