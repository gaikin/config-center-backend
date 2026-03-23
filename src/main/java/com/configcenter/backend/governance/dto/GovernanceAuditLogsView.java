package com.configcenter.backend.governance.dto;

import java.util.List;

public record GovernanceAuditLogsView(
        int total,
        List<GovernanceAuditLogRecordView> records
) {
}
