package com.configcenter.backend.permission.dto;

import java.time.LocalDateTime;

public record UserRoleBindingView(
        Long id,
        String userId,
        Long roleId,
        String status,
        LocalDateTime createTime
) {
}
