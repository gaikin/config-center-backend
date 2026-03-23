package com.configcenter.backend.control.rule.dto;

import java.util.List;

public record RulePreviewView(
        Long ruleId,
        boolean matched,
        String summary,
        List<RulePreviewTraceView> traces
) {
}
