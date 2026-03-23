package com.configcenter.backend.control.jobscene.dto;

public record JobSceneCloneRequest(
        String targetOrgId,
        String operator
) {
}
