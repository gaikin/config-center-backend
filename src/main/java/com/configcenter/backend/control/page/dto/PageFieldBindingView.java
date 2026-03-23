package com.configcenter.backend.control.page.dto;

public record PageFieldBindingView(
        Long id,
        Long pageResourceId,
        String businessFieldCode,
        Long pageElementId,
        boolean required,
        String updatedAt
) {
}

