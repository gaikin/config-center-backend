package com.configcenter.backend.control.jobscene.dto;

public record JobExecutionView(
        Long id,
        Long sceneId,
        String sceneName,
        String triggerSource,
        String result,
        boolean fallbackToManual,
        String detail,
        String startedAt,
        String finishedAt
) {
}
