package com.configcenter.backend.control.page.dto;

import java.util.List;

public record BusinessFieldUpsertRequest(
        Long id,
        String code,
        String name,
        String scope,
        Long pageResourceId,
        String valueType,
        Boolean required,
        String description,
        String ownerOrgId,
        String status,
        Integer currentVersion,
        List<String> aliases
) {
}

