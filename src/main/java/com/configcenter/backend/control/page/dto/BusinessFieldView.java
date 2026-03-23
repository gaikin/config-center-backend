package com.configcenter.backend.control.page.dto;

import java.util.List;

public record BusinessFieldView(
        Long id,
        String code,
        String name,
        String scope,
        Long pageResourceId,
        String valueType,
        boolean required,
        String description,
        String ownerOrgId,
        String status,
        Integer currentVersion,
        List<String> aliases,
        String updatedAt
) {
}

