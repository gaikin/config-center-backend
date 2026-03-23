package com.configcenter.backend.control.jobscene;

import com.configcenter.backend.common.api.ApiResponse;
import com.configcenter.backend.control.jobscene.dto.JobNodeUpsertRequest;
import com.configcenter.backend.control.jobscene.dto.JobNodeView;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/control")
public class JobSceneWorkflowController {

    private final JobSceneWorkflowService jobSceneWorkflowService;

    public JobSceneWorkflowController(JobSceneWorkflowService jobSceneWorkflowService) {
        this.jobSceneWorkflowService = jobSceneWorkflowService;
    }

    @GetMapping("/job-scenes/{sceneId}/nodes")
    public ApiResponse<List<JobNodeView>> listJobNodes(@PathVariable Long sceneId) {
        return ApiResponse.success(jobSceneWorkflowService.listJobNodes(sceneId));
    }

    @PostMapping("/job-scenes/{sceneId}/nodes")
    public ApiResponse<JobNodeView> upsertJobNode(
            @PathVariable Long sceneId,
            @RequestBody JobNodeUpsertRequest body
    ) {
        return ApiResponse.success(jobSceneWorkflowService.upsertJobNode(sceneId, body));
    }

    @PostMapping("/job-nodes/{nodeId}")
    public ApiResponse<Void> deleteJobNode(@PathVariable Long nodeId) {
        jobSceneWorkflowService.deleteJobNode(nodeId);
        return ApiResponse.success(null);
    }
}

