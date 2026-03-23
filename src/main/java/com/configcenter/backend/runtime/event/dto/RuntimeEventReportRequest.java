package com.configcenter.backend.runtime.event.dto;

import java.util.List;

public record RuntimeEventReportRequest(
        List<RuntimeEventReportItem> events
) {
}

