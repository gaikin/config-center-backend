package com.configcenter.backend.control.jobscene;

import com.configcenter.backend.common.api.ApiResponse;
import com.configcenter.backend.control.jobscene.dto.JobSceneCloneRequest;
import com.configcenter.backend.control.jobscene.dto.JobSceneShareRequest;
import com.configcenter.backend.control.jobscene.dto.JobSceneStatusUpdateRequest;
import com.configcenter.backend.control.jobscene.dto.JobSceneUpsertRequest;
import com.configcenter.backend.control.jobscene.dto.JobSceneView;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/control/job-scenes")
public class JobSceneController {

    private final JobSceneService jobSceneService;

    public JobSceneController(JobSceneService jobSceneService) {
        this.jobSceneService = jobSceneService;
    }

    @GetMapping
    public ApiResponse<List<JobSceneView>> listJobScenes() {
        return ApiResponse.success(jobSceneService.listScenes());
    }

    @GetMapping("/{sceneId}")
    public ApiResponse<JobSceneView> getJobSceneDetail(@PathVariable Long sceneId) {
        return ApiResponse.success(jobSceneService.getSceneDetail(sceneId));
    }

    @PostMapping
    public ApiResponse<JobSceneView> createJobScene(@RequestBody JobSceneUpsertRequest body) {
        return ApiResponse.success(jobSceneService.createScene(body));
    }

    @PostMapping("/{sceneId}")
    public ApiResponse<JobSceneView> updateJobScene(
            @PathVariable Long sceneId,
            @RequestBody JobSceneUpsertRequest body
    ) {
        return ApiResponse.success(jobSceneService.updateScene(sceneId, body));
    }

    @PostMapping("/{sceneId}/status")
    public ApiResponse<JobSceneView> updateJobSceneStatus(
            @PathVariable Long sceneId,
            @RequestBody JobSceneStatusUpdateRequest body
    ) {
        return ApiResponse.success(jobSceneService.updateSceneStatus(sceneId, body));
    }

    @PostMapping("/{sceneId}/share")
    public ApiResponse<JobSceneView> updateJobSceneShareConfig(
            @PathVariable Long sceneId,
            @RequestBody JobSceneShareRequest body
    ) {
        return ApiResponse.success(jobSceneService.updateSceneShareConfig(sceneId, body));
    }

    @PostMapping("/{sceneId}/clone")
    public ApiResponse<JobSceneView> cloneJobSceneToOrg(
            @PathVariable Long sceneId,
            @RequestBody JobSceneCloneRequest body
    ) {
        return ApiResponse.success(jobSceneService.cloneSceneToOrg(sceneId, body));
    }
}

