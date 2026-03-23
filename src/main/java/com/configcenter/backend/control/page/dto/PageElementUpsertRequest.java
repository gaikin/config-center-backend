package com.configcenter.backend.control.page.dto;

public record PageElementUpsertRequest(
        Long id,
        Long pageResourceId,
        String logicName,
        String selector,
        String selectorType,
        String frameLocation
) {
}

