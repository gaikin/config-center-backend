package com.configcenter.backend.control.publish;

import com.configcenter.backend.common.api.ApiResponse;
import com.configcenter.backend.control.publish.dto.PublishAuditLogRecordRequest;
import com.configcenter.backend.control.publish.dto.PublishAuditLogView;
import com.configcenter.backend.control.publish.dto.PublishValidationRequest;
import com.configcenter.backend.control.publish.dto.PublishValidationView;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/control/publish")
public class PublishController {

    private final PublishService publishService;

    public PublishController(PublishService publishService) {
        this.publishService = publishService;
    }

    @PostMapping("/validate")
    public ApiResponse<PublishValidationView> validate(@RequestBody PublishValidationRequest body) {
        return ApiResponse.success(publishService.validate(body));
    }

    @GetMapping("/logs")
    public ApiResponse<List<PublishAuditLogView>> listLogs() {
        return ApiResponse.success(publishService.listLogs());
    }

    @PostMapping("/logs")
    public ApiResponse<PublishAuditLogView> recordLog(@RequestBody PublishAuditLogRecordRequest request) {
        return ApiResponse.success(publishService.recordLog(request));
    }
}
