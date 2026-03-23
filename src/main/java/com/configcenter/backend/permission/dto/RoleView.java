package com.configcenter.backend.permission.dto;

import java.time.LocalDateTime;

public record RoleView(
        Long id,
        String name,
        String roleType,
        String status,
        String orgScopeId,
        Integer memberCount,
        LocalDateTime updateTime
) {
}
