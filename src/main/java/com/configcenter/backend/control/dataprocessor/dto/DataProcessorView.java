package com.configcenter.backend.control.dataprocessor.dto;

public record DataProcessorView(
        Long id,
        String name,
        Integer paramCount,
        String functionCode,
        String status,
        Integer usedByCount
) {
}
