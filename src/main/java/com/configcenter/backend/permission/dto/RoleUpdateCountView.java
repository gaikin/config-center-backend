package com.configcenter.backend.permission.dto;

public record RoleUpdateCountView(
        Long roleId,
        int updatedCount
) {
}
