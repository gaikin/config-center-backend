package com.configcenter.backend.control.jobscene.dto;

import java.util.List;

public record JobSceneShareRequest(
        String shareMode,
        List<String> sharedOrgIds,
        String operator
) {
}
