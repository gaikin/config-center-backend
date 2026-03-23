package com.configcenter.backend.control.rule.dto;

import java.util.Map;

public record RulePreviewRequest(
        Map<String, String> pageFields,
        Map<String, String> interfaceFields,
        Map<String, String> context
) {
}
