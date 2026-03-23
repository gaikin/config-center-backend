package com.configcenter.backend.permission.dto;

import java.time.LocalDateTime;

public record RoleResourceGrantView(
        Long id,
        Long roleId,
        String resourceCode,
        LocalDateTime createTime
) {
}
