package com.configcenter.backend.governance.dto;

import java.util.List;

public record GovernanceExecutionLogsView(
        int total,
        List<GovernanceExecutionLogRecordView> records
) {
}
