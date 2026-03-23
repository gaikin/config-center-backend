package com.configcenter.backend.permission.dto;

import java.util.List;

public record SessionMeView(
        String userId,
        String orgId,
        List<RoleView> roles,
        List<String> resourceCodes,
        List<String> resourcePaths
) {
}
