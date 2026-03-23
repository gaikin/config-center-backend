package com.configcenter.backend.governance.dto;

import java.util.List;

public record GovernanceTriggerLogsView(
        int total,
        List<GovernanceTriggerLogRecordView> records
) {
}
