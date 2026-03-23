package com.configcenter.backend.runtime.sdk.dto;

import java.util.List;

public record RuntimeJobPackageView(
        String bundleVersion,
        String pageResourceId,
        List<RuntimeJobSceneView> jobs
) {
}

