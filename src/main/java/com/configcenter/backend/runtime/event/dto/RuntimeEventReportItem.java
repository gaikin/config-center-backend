package com.configcenter.backend.runtime.event.dto;

public record RuntimeEventReportItem(
        String type,
        String createdAt,
        String traceId,
        String sdkVersion,
        String bundleVersion,
        String pageResourceId,
        String ruleId,
        String reason,
        Long latencyMs
) {
}

