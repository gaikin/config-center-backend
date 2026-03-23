package com.configcenter.backend.permission.dto;

public record PermissionResourceUpsertRequest(
        Long id,
        String resourceCode,
        String resourceName,
        String resourceType,
        String resourcePath,
        String pagePath,
        String status,
        Integer orderNo,
        String description
) {
}
