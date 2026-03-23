package com.configcenter.backend.permission.dto;

import java.time.LocalDateTime;

public record PermissionResourceView(
        Long id,
        String resourceCode,
        String resourceName,
        String resourceType,
        String resourcePath,
        String pagePath,
        String status,
        Integer orderNo,
        String description,
        LocalDateTime updateTime
) {
}
