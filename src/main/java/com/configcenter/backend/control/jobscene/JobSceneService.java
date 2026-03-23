package com.configcenter.backend.control.jobscene;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.configcenter.backend.control.jobscene.dto.JobSceneCloneRequest;
import com.configcenter.backend.control.jobscene.dto.JobSceneShareRequest;
import com.configcenter.backend.control.jobscene.dto.JobSceneStatusUpdateRequest;
import com.configcenter.backend.control.jobscene.dto.JobSceneUpsertRequest;
import com.configcenter.backend.control.jobscene.dto.JobSceneView;
import com.configcenter.backend.infrastructure.db.control.jobscene.JobSceneMapper;
import com.configcenter.backend.infrastructure.db.control.jobscene.JobSceneNodeMapper;
import com.configcenter.backend.infrastructure.db.control.jobscene.model.JobSceneDO;
import com.configcenter.backend.infrastructure.db.control.jobscene.model.JobSceneNodeDO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JobSceneService {

    private final JobSceneMapper jobSceneMapper;
    private final JobSceneNodeMapper jobSceneNodeMapper;
    private final ObjectMapper objectMapper;

    public JobSceneService(JobSceneMapper jobSceneMapper, JobSceneNodeMapper jobSceneNodeMapper, ObjectMapper objectMapper) {
        this.jobSceneMapper = jobSceneMapper;
        this.jobSceneNodeMapper = jobSceneNodeMapper;
        this.objectMapper = objectMapper;
    }

    public List<JobSceneView> listScenes() {
        return jobSceneMapper.selectList(
                        new LambdaQueryWrapper<JobSceneDO>()
                                .orderByDesc(JobSceneDO::getUpdateTime)
                                .orderByDesc(JobSceneDO::getId)
                )
                .stream()
                .map(this::toView)
                .toList();
    }

    public JobSceneView getSceneDetail(Long sceneId) {
        return toView(requireScene(sceneId));
    }

    @Transactional
    public JobSceneView createScene(JobSceneUpsertRequest request) {
        validate(request);
        JobSceneDO row = toDo(request, null);
        jobSceneMapper.insert(row);
        return toView(row);
    }

    @Transactional
    public JobSceneView updateScene(Long sceneId, JobSceneUpsertRequest request) {
        validate(request);
        JobSceneDO exists = requireScene(sceneId);
        JobSceneDO row = toDo(request, exists);
        row.setId(sceneId);
        jobSceneMapper.updateById(row);
        return toView(row);
    }

    @Transactional
    public JobSceneView updateSceneStatus(Long sceneId, JobSceneStatusUpdateRequest request) {
        JobSceneDO exists = requireScene(sceneId);
        exists.setStatus(normalizeStatus(request.status()));
        jobSceneMapper.updateById(exists);
        return toView(exists);
    }

    @Transactional
    public JobSceneView updateSceneShareConfig(Long sceneId, JobSceneShareRequest request) {
        JobSceneDO exists = requireScene(sceneId);
        exists.setShareMode(normalizeShareMode(request.shareMode()));
        exists.setSharedOrgIdsJson(writeJson(normalizeSharedOrgIds(request.sharedOrgIds())));
        exists.setSharedBy(normalizeString(request.operator()));
        exists.setSharedAt(nowText());
        jobSceneMapper.updateById(exists);
        return toView(exists);
    }

    @Transactional
    public JobSceneView cloneSceneToOrg(Long sceneId, JobSceneCloneRequest request) {
        JobSceneDO source = requireScene(sceneId);
        JobSceneView sourceView = toView(source);
        JobSceneDO row = new JobSceneDO();
        row.setName(sourceView.getName() + "-副本");
        row.setOwnerOrgId(normalizeString(request.targetOrgId()));
        row.setShareMode("PRIVATE");
        row.setSharedOrgIdsJson("[]");
        row.setSharedBy(normalizeString(request.operator()));
        row.setSharedAt(nowText());
        row.setSourceSceneId(sourceView.getId());
        row.setSourceSceneName(sourceView.getName());
        row.setPageResourceId(sourceView.getPageResourceId());
        row.setPageResourceName(sourceView.getPageResourceName());
        row.setExecutionMode(sourceView.getExecutionMode());
        row.setPreviewBeforeExecute(Boolean.TRUE.equals(sourceView.getPreviewBeforeExecute()));
        row.setFloatingButtonEnabled(Boolean.TRUE.equals(sourceView.getFloatingButtonEnabled()));
        row.setFloatingButtonLabel(sourceView.getFloatingButtonLabel());
        row.setFloatingButtonX(sourceView.getFloatingButtonX());
        row.setFloatingButtonY(sourceView.getFloatingButtonY());
        row.setStatus("DRAFT");
        row.setManualDurationSec(sourceView.getManualDurationSec());
        row.setRiskConfirmed(false);
        jobSceneMapper.insert(row);
        return toView(row);
    }

    private JobSceneDO requireScene(Long sceneId) {
        JobSceneDO scene = jobSceneMapper.selectById(sceneId);
        if (scene == null) {
            throw new IllegalArgumentException("场景不存在");
        }
        return scene;
    }

    private void validate(JobSceneUpsertRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("请求为空");
        }
        if (normalizeString(request.name()).isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
        if (request.pageResourceId() == null || request.pageResourceId() <= 0) {
            throw new IllegalArgumentException("pageResourceId is required");
        }
        if (normalizeString(request.pageResourceName()).isBlank()) {
            throw new IllegalArgumentException("pageResourceName is required");
        }
        if (normalizeString(request.executionMode()).isBlank()) {
            throw new IllegalArgumentException("executionMode is required");
        }
        if (request.manualDurationSec() == null || request.manualDurationSec() <= 0) {
            throw new IllegalArgumentException("manualDurationSec is required");
        }
    }

    private JobSceneView toView(JobSceneDO row) {
        JobSceneView view = new JobSceneView();
        view.setId(row.getId());
        view.setName(row.getName());
        view.setOwnerOrgId(row.getOwnerOrgId());
        view.setShareMode(normalizeShareMode(row.getShareMode()));
        view.setSharedOrgIds(readSharedOrgIds(row.getSharedOrgIdsJson()));
        view.setSharedBy(row.getSharedBy());
        view.setSharedAt(row.getSharedAt());
        view.setSourceSceneId(row.getSourceSceneId());
        view.setSourceSceneName(row.getSourceSceneName());
        view.setPageResourceId(row.getPageResourceId());
        view.setPageResourceName(row.getPageResourceName());
        view.setExecutionMode(normalizeString(row.getExecutionMode()));
        view.setPreviewBeforeExecute(Boolean.TRUE.equals(row.getPreviewBeforeExecute()));
        view.setFloatingButtonEnabled(Boolean.TRUE.equals(row.getFloatingButtonEnabled()));
        view.setFloatingButtonLabel(row.getFloatingButtonLabel());
        view.setFloatingButtonX(row.getFloatingButtonX());
        view.setFloatingButtonY(row.getFloatingButtonY());
        view.setStatus(normalizeStatus(row.getStatus()));
        view.setManualDurationSec(row.getManualDurationSec() == null ? 0 : row.getManualDurationSec());
        view.setRiskConfirmed(Boolean.TRUE.equals(row.getRiskConfirmed()));
        view.setNodeCount(jobSceneNodeMapper.selectCount(new LambdaQueryWrapper<JobSceneNodeDO>().eq(JobSceneNodeDO::getSceneId, row.getId())));
        return view;
    }

    private JobSceneDO toDo(JobSceneUpsertRequest request, JobSceneDO fallback) {
        JobSceneDO row = new JobSceneDO();
        row.setId(request.id());
        row.setName(normalizeString(request.name()));
        row.setOwnerOrgId(normalizeString(request.ownerOrgId() == null ? (fallback == null ? "branch-east" : fallback.getOwnerOrgId()) : request.ownerOrgId()));
        row.setShareMode(normalizeShareMode(request.shareMode() == null ? (fallback == null ? "PRIVATE" : fallback.getShareMode()) : request.shareMode()));
        row.setSharedOrgIdsJson(writeJson(normalizeSharedOrgIds(request.sharedOrgIds() == null ? readSharedOrgIds(fallback == null ? "[]" : fallback.getSharedOrgIdsJson()) : request.sharedOrgIds())));
        row.setSharedBy(normalizeString(request.sharedBy() == null ? (fallback == null ? null : fallback.getSharedBy()) : request.sharedBy()));
        row.setSharedAt(normalizeString(request.sharedAt() == null ? (fallback == null ? null : fallback.getSharedAt()) : request.sharedAt()));
        row.setSourceSceneId(request.sourceSceneId() == null ? (fallback == null ? null : fallback.getSourceSceneId()) : request.sourceSceneId());
        row.setSourceSceneName(normalizeString(request.sourceSceneName() == null ? (fallback == null ? null : fallback.getSourceSceneName()) : request.sourceSceneName()));
        row.setPageResourceId(request.pageResourceId());
        row.setPageResourceName(normalizeString(request.pageResourceName()));
        row.setExecutionMode(normalizeString(request.executionMode()));
        row.setPreviewBeforeExecute(Boolean.TRUE.equals(request.previewBeforeExecute() == null ? (fallback != null && Boolean.TRUE.equals(fallback.getPreviewBeforeExecute())) : request.previewBeforeExecute()));
        row.setFloatingButtonEnabled(Boolean.TRUE.equals(request.floatingButtonEnabled() == null ? (fallback != null && Boolean.TRUE.equals(fallback.getFloatingButtonEnabled())) : request.floatingButtonEnabled()));
        row.setFloatingButtonLabel(normalizeString(request.floatingButtonLabel() == null ? (fallback == null ? "" : fallback.getFloatingButtonLabel()) : request.floatingButtonLabel()));
        row.setFloatingButtonX(request.floatingButtonX() == null ? (fallback == null ? 0 : fallback.getFloatingButtonX()) : request.floatingButtonX());
        row.setFloatingButtonY(request.floatingButtonY() == null ? (fallback == null ? 0 : fallback.getFloatingButtonY()) : request.floatingButtonY());
        row.setStatus(normalizeStatus(request.status()));
        row.setManualDurationSec(request.manualDurationSec());
        row.setRiskConfirmed(Boolean.TRUE.equals(request.riskConfirmed() == null ? (fallback != null && Boolean.TRUE.equals(fallback.getRiskConfirmed())) : request.riskConfirmed()));
        return row;
    }

    private List<String> readSharedOrgIds(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }

    private String writeJson(List<String> values) {
        try {
            return objectMapper.writeValueAsString(values == null ? List.of() : values);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("无法序列化共享机构列表", e);
        }
    }

    private String normalizeString(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalizeStatus(String value) {
        String normalized = normalizeString(value);
        return normalized.isBlank() ? "DRAFT" : normalized.toUpperCase();
    }

    private String normalizeShareMode(String value) {
        String normalized = normalizeString(value);
        return "SHARED".equalsIgnoreCase(normalized) ? "SHARED" : "PRIVATE";
    }

    private List<String> normalizeSharedOrgIds(List<String> values) {
        if (values == null) {
            return List.of();
        }
        List<String> normalized = new ArrayList<>();
        for (String value : values) {
            String item = normalizeString(value);
            if (!item.isBlank() && !normalized.contains(item)) {
                normalized.add(item);
            }
        }
        return normalized;
    }

    private String nowText() {
        return LocalDateTime.now().toString();
    }
}
