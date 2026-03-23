package com.configcenter.backend.control.interfaceapi.dto;

public record InterfaceDefinitionView(
        Long id,
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
        Boolean maskSensitive,
        String updatedAt
) {
}
