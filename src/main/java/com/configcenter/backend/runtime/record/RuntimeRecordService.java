package com.configcenter.backend.runtime.record;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.configcenter.backend.common.context.RequestContextHolder;
import com.configcenter.backend.infrastructure.db.control.jobscene.JobExecutionMapper;
import com.configcenter.backend.infrastructure.db.control.jobscene.model.JobExecutionDO;
import com.configcenter.backend.infrastructure.db.control.jobscene.JobSceneMapper;
import com.configcenter.backend.infrastructure.db.control.jobscene.model.JobSceneDO;
import com.configcenter.backend.infrastructure.db.runtime.record.PromptTriggerLogMapper;
import com.configcenter.backend.infrastructure.db.runtime.record.model.PromptTriggerLogDO;
import com.configcenter.backend.runtime.record.dto.JobExecutionRecordUpsertRequest;
import com.configcenter.backend.runtime.record.dto.JobExecutionRecordQuery;
import com.configcenter.backend.runtime.record.dto.JobExecutionRecordView;
import com.configcenter.backend.runtime.record.dto.PromptTriggerLogQuery;
import com.configcenter.backend.runtime.record.dto.PromptTriggerLogUpsertRequest;
import com.configcenter.backend.runtime.record.dto.PromptTriggerLogView;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class RuntimeRecordService {

    private final PromptTriggerLogMapper promptTriggerLogMapper;
    private final JobSceneMapper jobSceneMapper;
    private final JobExecutionMapper jobExecutionMapper;

    public RuntimeRecordService(
            PromptTriggerLogMapper promptTriggerLogMapper,
            JobSceneMapper jobSceneMapper,
            JobExecutionMapper jobExecutionMapper
    ) {
        this.promptTriggerLogMapper = promptTriggerLogMapper;
        this.jobSceneMapper = jobSceneMapper;
        this.jobExecutionMapper = jobExecutionMapper;
    }

    public List<PromptTriggerLogView> listPromptTriggerLogs(PromptTriggerLogQuery query) {
        String keyword = normalize(query.keyword());
        LocalDateTime startAt = parseStartAt(query.startAt());
        LocalDateTime endAt = parseEndAt(query.endAt());
        return promptTriggerLogMapper.selectList(
                        new LambdaQueryWrapper<PromptTriggerLogDO>()
                                .orderByDesc(PromptTriggerLogDO::getTriggerAt)
                                .orderByDesc(PromptTriggerLogDO::getId)
                )
                .stream()
                .filter(row -> query.ruleId() == null || query.ruleId().equals(row.getRuleId()))
                .filter(row -> query.pageResourceId() == null || query.pageResourceId().equals(row.getPageResourceId()))
                .filter(row -> query.orgId() == null || query.orgId().isBlank() || query.orgId().trim().equals(row.getOrgId()))
                .filter(row -> startAt == null || (row.getTriggerAt() != null && !row.getTriggerAt().isBefore(startAt)))
                .filter(row -> endAt == null || (row.getTriggerAt() != null && !row.getTriggerAt().isAfter(endAt)))
                .filter(row -> keyword.isBlank() || matchesPromptKeyword(row, keyword))
                .map(this::toPromptView)
                .toList();
    }

    public Long createPromptTriggerLog(PromptTriggerLogUpsertRequest body) {
        PromptTriggerLogDO row = new PromptTriggerLogDO();
        row.setRuleId(body.ruleId());
        row.setRuleName(defaultText(body.ruleName(), "unknown-rule"));
        row.setPageResourceId(body.pageResourceId());
        row.setPageResourceName(defaultText(body.pageResourceName(), ""));
        row.setOrgId(defaultText(body.orgId(), RequestContextHolder.get() == null ? "org.demo" : RequestContextHolder.get().orgId()));
        row.setOrgName(defaultText(body.orgName(), resolveOrgName(row.getOrgId(), null)));
        row.setPromptMode(defaultText(body.promptMode(), "FLOATING"));
        row.setPromptContentSummary(defaultText(body.promptContentSummary(), ""));
        row.setSceneId(body.sceneId());
        row.setSceneName(defaultText(body.sceneName(), ""));
        row.setTriggerResult(defaultText(body.triggerResult(), "HIT"));
        row.setReason(defaultText(body.reason(), ""));
        row.setTriggerAt(parseDateTime(body.triggerAt(), LocalDateTime.now()));
        promptTriggerLogMapper.insert(row);
        return row.getId();
    }

    public List<JobExecutionRecordView> listJobExecutionRecords(JobExecutionRecordQuery query) {
        String keyword = normalize(query.keyword());
        String result = normalize(query.result());
        String sceneName = normalize(query.sceneName());
        String orgId = normalize(query.orgId());
        LocalDateTime startAt = parseStartAt(query.startAt());
        LocalDateTime endAt = parseEndAt(query.endAt());

        List<JobSceneDO> scenes = jobSceneMapper.selectList(
                        new LambdaQueryWrapper<JobSceneDO>()
                                .eq(query.sceneId() != null, JobSceneDO::getId, query.sceneId())
                                .eq(query.pageResourceId() != null, JobSceneDO::getPageResourceId, query.pageResourceId())
                                .eq(!orgId.isBlank(), JobSceneDO::getOwnerOrgId, orgId)
                                .like(!sceneName.isBlank(), JobSceneDO::getName, sceneName)
                                .orderByAsc(JobSceneDO::getId)
                )
                .stream()
                .toList();
        Map<Long, JobSceneDO> sceneById = scenes.stream()
                .collect(Collectors.toMap(JobSceneDO::getId, Function.identity(), (left, right) -> left, LinkedHashMap::new));
        if (sceneById.isEmpty()) {
            return List.of();
        }
        List<Long> sceneIds = sceneById.keySet().stream().toList();
        return jobExecutionMapper.selectList(
                        new LambdaQueryWrapper<JobExecutionDO>()
                                .in(JobExecutionDO::getSceneId, sceneIds)
                                .orderByDesc(JobExecutionDO::getStartedAt)
                                .orderByDesc(JobExecutionDO::getId)
                )
                .stream()
                .filter(row -> result.isBlank() || result.equalsIgnoreCase(normalize(row.getResult())))
                .filter(row -> startAt == null || (row.getStartedAt() != null && !row.getStartedAt().isBefore(startAt)))
                .filter(row -> endAt == null || (row.getStartedAt() != null && !row.getStartedAt().isAfter(endAt)))
                .filter(row -> keyword.isBlank() || matchesJobKeyword(row, sceneById.get(row.getSceneId()), keyword))
                .map(row -> toJobExecutionView(row, sceneById.get(row.getSceneId())))
                .toList();
    }

    public Long createJobExecutionRecord(JobExecutionRecordUpsertRequest body) {
        JobExecutionDO row = new JobExecutionDO();
        row.setSceneId(body.sceneId());
        row.setSceneName(defaultText(body.sceneName(), "unknown-scene"));
        row.setTriggerSource(defaultText(body.triggerSource(), "PROMPT_TRIGGER"));
        row.setResult(defaultText(body.result(), "SUCCESS").toUpperCase());
        row.setFallbackToManual(Boolean.TRUE.equals(body.fallbackToManual()));
        row.setDetail(defaultText(body.detail(), ""));
        row.setStartedAt(parseDateTime(body.startedAt(), LocalDateTime.now()));
        row.setFinishedAt(parseDateTime(body.finishedAt(), row.getStartedAt()));
        jobExecutionMapper.insert(row);
        return row.getId();
    }

    private PromptTriggerLogView toPromptView(PromptTriggerLogDO row) {
        return new PromptTriggerLogView(
                row.getId(),
                row.getRuleId(),
                row.getRuleName(),
                row.getPageResourceId(),
                row.getPageResourceName(),
                row.getOrgId(),
                resolveOrgName(row.getOrgId(), row.getOrgName()),
                row.getPromptMode(),
                row.getPromptContentSummary(),
                row.getSceneId(),
                row.getSceneName(),
                row.getTriggerAt() == null ? "" : row.getTriggerAt().toString(),
                row.getTriggerResult(),
                row.getReason()
        );
    }

    private JobExecutionRecordView toJobExecutionView(JobExecutionDO row, JobSceneDO scene) {
        if (scene == null) {
            throw new IllegalStateException("作业场景不存在");
        }
        return new JobExecutionRecordView(
                row.getId(),
                row.getSceneId(),
                row.getSceneName(),
                scene.getPageResourceId(),
                scene.getPageResourceName(),
                scene.getOwnerOrgId(),
                resolveOrgName(scene.getOwnerOrgId(), null),
                row.getTriggerSource(),
                row.getResult(),
                normalize(row.getDetail()),
                row.getStartedAt() == null ? "" : row.getStartedAt().toString(),
                row.getFinishedAt() == null ? "" : row.getFinishedAt().toString()
        );
    }

    private boolean matchesPromptKeyword(PromptTriggerLogDO row, String keyword) {
        return contains(row.getRuleName(), keyword)
                || contains(row.getPageResourceName(), keyword)
                || contains(row.getPromptContentSummary(), keyword)
                || contains(row.getSceneName(), keyword)
                || contains(row.getOrgName(), keyword)
                || contains(row.getReason(), keyword);
    }

    private boolean matchesJobKeyword(JobExecutionDO row, JobSceneDO scene, String keyword) {
        return contains(row.getSceneName(), keyword)
                || contains(row.getDetail(), keyword)
                || contains(scene == null ? null : scene.getPageResourceName(), keyword)
                || contains(scene == null ? null : scene.getOwnerOrgId(), keyword);
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword.toLowerCase());
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private String defaultText(String value, String fallback) {
        String normalized = normalize(value);
        return normalized.isBlank() ? fallback : normalized;
    }

    private LocalDateTime parseStartAt(String value) {
        String normalized = normalize(value);
        if (normalized.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(normalized).atStartOfDay();
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private LocalDateTime parseEndAt(String value) {
        String normalized = normalize(value);
        if (normalized.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(normalized).atTime(LocalTime.MAX);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private LocalDateTime parseDateTime(String value, LocalDateTime fallback) {
        String normalized = normalize(value);
        if (normalized.isBlank()) {
            return fallback;
        }
        try {
            return LocalDateTime.parse(normalized);
        } catch (DateTimeParseException ignore) {
            // fallback to offset format
        }
        try {
            return OffsetDateTime.parse(normalized).toLocalDateTime();
        } catch (DateTimeParseException ignore) {
            return fallback;
        }
    }

    private String resolveOrgName(String orgId, String fallback) {
        if (fallback != null && !fallback.isBlank()) {
            return fallback;
        }
        return switch (normalize(orgId)) {
            case "head-office" -> "总行";
            case "branch-east" -> "东分行";
            case "branch-south" -> "南分行";
            case "org.demo" -> "演示机构";
            case "100001" -> "演示机构";
            default -> orgId == null ? "" : orgId;
        };
    }
}
