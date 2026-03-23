package com.configcenter.backend.control.page.dto;

public record PageElementView(
        Long id,
        Long pageResourceId,
        String logicName,
        String selector,
        String selectorType,
        String frameLocation,
        String updatedAt
) {
}

