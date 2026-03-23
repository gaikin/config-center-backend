package com.configcenter.backend.control.interfaceapi.dto;

public record InterfaceVersionContentView(
        String name,
        String description,
        String method,
        String testPath,
        String prodPath,
        String url,
        String status,
        String ownerOrgId,
        Integer currentVersion,
        Integer timeoutMs,
        Integer retryTimes,
        String bodyTemplateJson,
        String inputConfigJson,
        String outputConfigJson,
        String paramSourceSummary,
        String responsePath,
        Boolean maskSensitive
) {
}
