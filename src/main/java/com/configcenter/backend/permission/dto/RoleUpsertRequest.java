package com.configcenter.backend.permission.dto;

public record RoleUpsertRequest(
        Long id,
        String name,
        String roleType,
        String status,
        String orgScopeId,
        Integer memberCount
) {
}
