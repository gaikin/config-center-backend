package com.configcenter.backend.runtime.sdk.dto;

import java.util.List;

public record RuntimePromptBundleView(
        String bundleVersion,
        String pageResourceId,
        List<RuntimePromptItemView> prompts
) {
}

