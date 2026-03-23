package com.configcenter.backend.runtime.record;

import com.configcenter.backend.common.api.ApiResponse;
import com.configcenter.backend.runtime.record.dto.JobExecutionRecordQuery;
import com.configcenter.backend.runtime.record.dto.JobExecutionRecordUpsertRequest;
import com.configcenter.backend.runtime.record.dto.JobExecutionRecordView;
import com.configcenter.backend.runtime.record.dto.PromptTriggerLogQuery;
import com.configcenter.backend.runtime.record.dto.PromptTriggerLogUpsertRequest;
import com.configcenter.backend.runtime.record.dto.PromptTriggerLogView;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/runtime")
public class RuntimeRecordController {

    private final RuntimeRecordService runtimeRecordService;

    public RuntimeRecordController(RuntimeRecordService runtimeRecordService) {
        this.runtimeRecordService = runtimeRecordService;
    }

    @GetMapping("/prompt-trigger-logs")
    public ApiResponse<List<PromptTriggerLogView>> listPromptTriggerLogs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long pageResourceId,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String startAt,
            @RequestParam(required = false) String endAt,
            @RequestParam(required = false) Long ruleId
    ) {
        return ApiResponse.success(runtimeRecordService.listPromptTriggerLogs(
                new PromptTriggerLogQuery(keyword, pageResourceId, orgId, startAt, endAt, ruleId)
        ));
    }

    @PostMapping("/prompt-trigger-logs")
    public ApiResponse<Long> createPromptTriggerLog(@RequestBody PromptTriggerLogUpsertRequest body) {
        return ApiResponse.success(runtimeRecordService.createPromptTriggerLog(body));
    }

    @GetMapping("/job-execution-records")
    public ApiResponse<List<JobExecutionRecordView>> listJobExecutionRecords(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String result,
            @RequestParam(required = false) Long pageResourceId,
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String startAt,
            @RequestParam(required = false) String endAt,
            @RequestParam(required = false) Long sceneId,
            @RequestParam(required = false) String sceneName
    ) {
        return ApiResponse.success(runtimeRecordService.listJobExecutionRecords(
                new JobExecutionRecordQuery(keyword, result, pageResourceId, orgId, startAt, endAt, sceneId, sceneName)
        ));
    }

    @PostMapping("/job-execution-records")
    public ApiResponse<Long> createJobExecutionRecord(@RequestBody JobExecutionRecordUpsertRequest body) {
        return ApiResponse.success(runtimeRecordService.createJobExecutionRecord(body));
    }
}
