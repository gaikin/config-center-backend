package com.configcenter.backend.control.dataprocessor.dto;

public record DataProcessorUpsertRequest(
        Long id,
        String name,
        Integer paramCount,
        String functionCode,
        String status
) {
}
