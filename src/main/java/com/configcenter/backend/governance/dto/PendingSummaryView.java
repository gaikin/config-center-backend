package com.configcenter.backend.governance.dto;

public record PendingSummaryView(
        int draftCount,
        int expiringSoonCount,
        int validationFailedCount,
        int conflictCount,
        int riskConfirmPendingCount
) {
}
