package com.configcenter.backend.control.jobscene.dto;

import java.util.List;

public record JobSceneUpsertRequest(
        Long id,
        String name,
        String ownerOrgId,
        String shareMode,
        List<String> sharedOrgIds,
        String sharedBy,
        String sharedAt,
        Long sourceSceneId,
        String sourceSceneName,
        Long pageResourceId,
        String pageResourceName,
        String executionMode,
        Boolean previewBeforeExecute,
        Boolean floatingButtonEnabled,
        String floatingButtonLabel,
        Integer floatingButtonX,
        Integer floatingButtonY,
        String status,
        Integer manualDurationSec,
        Boolean riskConfirmed
) {
}
