package com.configcenter.backend.control.page.dto;

public record PageFieldBindingUpsertRequest(
        Long id,
        Long pageResourceId,
        String businessFieldCode,
        Long pageElementId,
        Boolean required
) {
}

