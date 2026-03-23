package com.configcenter.backend.governance.dto;

public record GovernanceTriggerLogRecordView(
        Long id,
        String traceId,
        String eventType
) {
}
