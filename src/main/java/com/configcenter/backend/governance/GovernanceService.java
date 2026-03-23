package com.configcenter.backend.governance;

import com.configcenter.backend.governance.dto.GovernanceAuditLogRecordView;
import com.configcenter.backend.governance.dto.GovernanceAuditLogsView;
import com.configcenter.backend.governance.dto.GovernanceExecutionLogRecordView;
import com.configcenter.backend.governance.dto.GovernanceExecutionLogsView;
import com.configcenter.backend.governance.dto.GovernanceMetricsView;
import com.configcenter.backend.governance.dto.GovernanceTriggerLogRecordView;
import com.configcenter.backend.governance.dto.GovernanceTriggerLogsView;
import com.configcenter.backend.governance.dto.PendingSummaryView;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GovernanceService {

    public PendingSummaryView pendingSummary() {
        return new PendingSummaryView(3, 0, 1, 0, 0);
    }

    public GovernanceAuditLogsView auditLogs() {
        return new GovernanceAuditLogsView(1, List.of(
                new GovernanceAuditLogRecordView(1L, "PUBLISH", "PAGE_RESOURCE", "system.demo")
        ));
    }

    public GovernanceTriggerLogsView triggerLogs() {
        return new GovernanceTriggerLogsView(1, List.of(
                new GovernanceTriggerLogRecordView(1L, "trace-demo", "PAGE_RESOLVED")
        ));
    }

    public GovernanceExecutionLogsView executionLogs() {
        return new GovernanceExecutionLogsView(1, List.of(
                new GovernanceExecutionLogRecordView(1L, 9001L, "SUCCEEDED")
        ));
    }

    public GovernanceMetricsView metricsOverview() {
        return new GovernanceMetricsView(1.0d, 0, List.of(), 0, 0);
    }
}

