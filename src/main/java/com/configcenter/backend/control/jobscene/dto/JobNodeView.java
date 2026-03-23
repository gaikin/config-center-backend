package com.configcenter.backend.control.jobscene.dto;

public record JobNodeView(
        Long id,
        Long sceneId,
        String nodeType,
        String name,
        Integer orderNo,
        boolean enabled,
        String configJson,
        String updatedAt
) {
}
