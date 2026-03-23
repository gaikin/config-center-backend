package com.configcenter.backend.runtime.sdk.dto;

import java.util.List;

public record RuntimeJobSceneView(
        String sceneId,
        String sceneName,
        String pageResourceId,
        String pageResourceName,
        String executionMode,
        String status,
        List<RuntimeJobNodeView> nodes
) {
}

