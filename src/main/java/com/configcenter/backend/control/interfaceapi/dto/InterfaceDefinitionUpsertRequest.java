package com.configcenter.backend.control.interfaceapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InterfaceDefinitionUpsertRequest(
        Long id,
        @NotBlank String name,
        @NotBlank String description,
        @NotBlank String method,
        String testPath,
        @NotBlank String prodPath,
        String url,
        @NotBlank String ownerOrgId,
        @NotNull Integer currentVersion,
        Integer timeoutMs,
        Integer retryTimes,
        String bodyTemplateJson,
        @NotBlank String inputConfigJson,
        @NotBlank String outputConfigJson,
        @NotBlank String paramSourceSummary,
        @NotBlank String responsePath,
        Boolean maskSensitive
) {
}
