package com.configcenter.backend.control.page.dto;

public record PageMenuView(
        Long id,
        String regionId,
        String menuCode,
        String menuName,
        String urlPattern,
        String status,
        String ownerOrgId
) {
}
