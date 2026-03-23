package com.configcenter.backend.control.jobscene.dto;

public record JobNodeUpsertRequest(
        Long id,
        Long sceneId,
        String nodeType,
        String name,
        Integer orderNo,
        Boolean enabled,
        String configJson
) {
}
