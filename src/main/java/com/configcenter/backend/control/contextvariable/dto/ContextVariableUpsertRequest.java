package com.configcenter.backend.control.contextvariable.dto;

public record ContextVariableUpsertRequest(
        Long id,
        String key,
        String label,
        String valueSource,
        String staticValue,
        String scriptContent,
        String status,
        String ownerOrgId
) {
}
