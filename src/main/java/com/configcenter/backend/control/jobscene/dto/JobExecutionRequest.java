package com.configcenter.backend.control.jobscene.dto;

public record JobExecutionRequest(
        String sceneName,
        String triggerSource
) {
}
