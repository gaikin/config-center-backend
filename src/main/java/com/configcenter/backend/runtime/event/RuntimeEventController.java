package com.configcenter.backend.runtime.event;

import com.configcenter.backend.common.api.ApiResponse;
import com.configcenter.backend.runtime.event.dto.RuntimeEventReportRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/runtime")
public class RuntimeEventController {

    private final RuntimeEventService runtimeEventService;

    public RuntimeEventController(RuntimeEventService runtimeEventService) {
        this.runtimeEventService = runtimeEventService;
    }

    @PostMapping("/events")
    public ApiResponse<Long> reportEvents(@RequestBody(required = false) RuntimeEventReportRequest body) {
        return ApiResponse.success(runtimeEventService.reportEvents(body));
    }
}
