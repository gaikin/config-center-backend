package com.configcenter.backend.runtime.event;

import com.configcenter.backend.common.context.RequestContextHolder;
import com.configcenter.backend.runtime.event.dto.RuntimeEventReportItem;
import com.configcenter.backend.runtime.event.dto.RuntimeEventReportRequest;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RuntimeEventService {

    private static final Logger log = LoggerFactory.getLogger(RuntimeEventService.class);

    public Long reportEvents(RuntimeEventReportRequest request) {
        List<RuntimeEventReportItem> events = request == null || request.events() == null
                ? List.of()
                : request.events().stream().filter(item -> item != null).toList();

        String operator = RequestContextHolder.currentUserId();
        for (RuntimeEventReportItem event : events) {
            log.info(
                    "runtime_event type={} createdAt={} traceId={} sdkVersion={} bundleVersion={} pageResourceId={} ruleId={} "
                            + "reason={} latencyMs={} userId={}",
                    safe(event.type()),
                    safe(event.createdAt()),
                    safe(event.traceId()),
                    safe(event.sdkVersion()),
                    safe(event.bundleVersion()),
                    safe(event.pageResourceId()),
                    safe(event.ruleId()),
                    safe(event.reason()),
                    event.latencyMs(),
                    operator
            );
        }
        return (long) events.size();
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
