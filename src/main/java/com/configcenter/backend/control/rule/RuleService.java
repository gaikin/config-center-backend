package com.configcenter.backend.control.rule;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.configcenter.backend.common.api.PageResponse;
import com.configcenter.backend.common.context.RequestContext;
import com.configcenter.backend.common.context.RequestContextHolder;
import com.configcenter.backend.control.rule.dto.RuleCloneRequest;
import com.configcenter.backend.control.rule.dto.RuleDefinitionUpsertRequest;
import com.configcenter.backend.control.rule.dto.RuleDefinitionView;
import com.configcenter.backend.control.rule.dto.RulePreviewRequest;
import com.configcenter.backend.control.rule.dto.RulePreviewTraceView;
import com.configcenter.backend.control.rule.dto.RulePreviewView;
import com.configcenter.backend.control.rule.dto.RuleShareRequest;
import com.configcenter.backend.control.rule.dto.RuleStatusUpdateRequest;
import com.configcenter.backend.infrastructure.db.control.rule.RuleMapper;
import com.configcenter.backend.infrastructure.db.control.rule.model.RuleDefinitionDO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public class RuleService {

    private final RuleMapper ruleMapper;
    private final ObjectMapper objectMapper;

    public RuleService(RuleMapper ruleMapper, ObjectMapper objectMapper) {
        this.ruleMapper = ruleMapper;
        this.objectMapper = objectMapper;
    }

    public PageResponse<RuleDefinitionView> listRules(
            Long pageNo,
            Long pageSize,
            String keyword,
            String status,
            String ownerOrgId
    ) {
        String normalizedKeyword = normalizeString(keyword);
        String normalizedStatus = normalizeString(status);
        String normalizedOwnerOrgId = normalizeString(ownerOrgId);
        Page<RuleDefinitionDO> page = ruleMapper.selectPage(
                new Page<>(pageNo, pageSize),
                new LambdaQueryWrapper<RuleDefinitionDO>()
                        .like(!normalizedKeyword.isBlank(), RuleDefinitionDO::getRuleName, normalizedKeyword)
                        .eq(!normalizedStatus.isBlank(), RuleDefinitionDO::getStatus, normalizedStatus)
                        .eq(!normalizedOwnerOrgId.isBlank(), RuleDefinitionDO::getOwnerOrgId, normalizedOwnerOrgId)
                        .orderByDesc(RuleDefinitionDO::getUpdateTime)
                        .orderByDesc(RuleDefinitionDO::getId)
        );
        List<RuleDefinitionView> rows = page.getRecords().stream().map(this::toView).toList();
        return new PageResponse<>(page.getTotal(), pageNo, pageSize, rows);
    }

    public RuleDefinitionView getRuleDetail(Long ruleId) {
        return toView(requireRule(ruleId));
    }

    public RuleDefinitionView createRule(RuleDefinitionUpsertRequest request) {
        validateRule(request);
        if (existsSameName(request.name(), null)) {
            throw new IllegalArgumentException("Rule name already exists: " + request.name());
        }
        RuleDefinitionView view = normalizeRequest(request, null, true);
        RuleDefinitionDO row = toDo(view);
        ruleMapper.insert(row);
        return toView(row);
    }

    public RuleDefinitionView updateRule(Long ruleId, RuleDefinitionUpsertRequest request) {
        validateRule(request);
        RuleDefinitionDO exists = requireRule(ruleId);
        if (existsSameName(request.name(), ruleId)) {
            throw new IllegalArgumentException("Rule name already exists: " + request.name());
        }

        RuleDefinitionView next = normalizeRequest(request, toView(exists), false);
        if (!"DRAFT".equalsIgnoreCase(Objects.toString(exists.getStatus(), "DRAFT"))) {
            next = next.withId(null);
            next = next.withStatus("DRAFT");
            next = next.withCurrentVersion((next.currentVersion() == null ? 0 : next.currentVersion()) + 1);
        } else {
            next = next.withId(ruleId);
        }

        RuleDefinitionDO row = toDo(next);
        if (exists.getStatus() != null && !"DRAFT".equalsIgnoreCase(exists.getStatus())) {
            ruleMapper.insert(row);
        } else {
            row.setId(ruleId);
            ruleMapper.updateById(row);
        }
        return toView(row);
    }

    public RuleDefinitionView updateRuleStatus(Long ruleId, RuleStatusUpdateRequest request) {
        RuleDefinitionDO exists = requireRule(ruleId);
        RuleDefinitionView view = toView(exists).withStatus(normalizeStatus(request.status()));
        RuleDefinitionDO row = toDo(view);
        row.setId(ruleId);
        ruleMapper.updateById(row);
        return toView(row);
    }

    public RuleDefinitionView updateRuleShareConfig(Long ruleId, RuleShareRequest request) {
        RuleDefinitionDO exists = requireRule(ruleId);
        RuleDefinitionView view = toView(exists).withShareMode(normalizeShareMode(request.shareMode()))
                .withSharedOrgIds(normalizeSharedOrgIds(request.sharedOrgIds()))
                .withSharedBy(normalizeString(request.operator()))
                .withSharedAt(nowText());
        RuleDefinitionDO row = toDo(view);
        row.setId(ruleId);
        ruleMapper.updateById(row);
        return toView(row);
    }

    public RuleDefinitionView cloneRuleToOrg(Long ruleId, RuleCloneRequest request) {
        RuleDefinitionDO source = requireRule(ruleId);
        RuleDefinitionView view = toView(source);
        RuleDefinitionView cloned = view.withId(null)
                .withName(view.name() + "-副本")
                .withOwnerOrgId(normalizeString(request.targetOrgId()))
                .withShareMode("PRIVATE")
                .withSharedOrgIds(List.of())
                .withSharedBy(normalizeString(request.operator()))
                .withSharedAt(nowText())
                .withSourceRuleId(view.id())
                .withSourceRuleName(view.name())
                .withStatus("DRAFT")
                .withCurrentVersion(1);
        RuleDefinitionDO row = toDo(cloned);
        ruleMapper.insert(row);
        return toView(row);
    }

    public RulePreviewView previewRule(Long ruleId, RulePreviewRequest body) {
        Map<String, String> fieldValues = body == null ? Map.of() : body.pageFields();
        String amountValue = fieldValues.getOrDefault("loanAmount", "0");
        long amount;
        try {
            amount = Long.parseLong(String.valueOf(amountValue));
        } catch (NumberFormatException error) {
            amount = 0L;
        }
        boolean matched = amount > 500000;
        String detail = matched
                ? "规则命中"
                : "规则未命中";
        return new RulePreviewView(
                ruleId,
                matched,
                detail,
                List.of(new RulePreviewTraceView(1L, "loanAmount > 500000", amountValue, "500000", matched,
                        matched ? "匹配" : "未匹配"))
        );
    }

    private RuleDefinitionDO requireRule(Long ruleId) {
        RuleDefinitionDO rule = ruleMapper.selectById(ruleId);
        if (rule == null) {
            throw new IllegalArgumentException("规则不存在");
        }
        return rule;
    }

    private boolean existsSameName(String name, Long currentId) {
        return ruleMapper.selectList(
                new LambdaQueryWrapper<RuleDefinitionDO>()
                        .eq(RuleDefinitionDO::getRuleName, normalizeString(name))
        ).stream().anyMatch(item -> currentId == null || !Objects.equals(item.getId(), currentId));
    }

    private void validateRule(RuleDefinitionUpsertRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("请求为空");
        }
        if (normalizeString(request.name()).isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
        if (normalizeString(request.promptMode()).isBlank()) {
            throw new IllegalArgumentException("promptMode is required");
        }
        if ("TIMER_THEN_MANUAL".equalsIgnoreCase(normalizeString(request.closeMode()))
                && (request.closeTimeoutSec() == null || request.closeTimeoutSec() <= 0)) {
            throw new IllegalArgumentException("closeTimeoutSec is required when closeMode is TIMER_THEN_MANUAL");
        }
    }

    private RuleDefinitionView normalizeRequest(RuleDefinitionUpsertRequest request, RuleDefinitionView fallback, boolean isCreate) {
        String status = normalizeStatus(request.status() == null ? (isCreate ? "DRAFT" : fallback == null ? "DRAFT" : fallback.status()) : request.status());
        Integer currentVersion = request.currentVersion() != null
                ? request.currentVersion()
                : fallback == null || fallback.currentVersion() == null ? 1 : fallback.currentVersion();
        String ownerOrgFallback = fallback == null ? resolveDefaultOwnerOrgId() : fallback.ownerOrgId();
        return new RuleDefinitionView(
                request.id(),
                normalizeString(request.name()),
                normalizeRuleScope(request.ruleScope(), fallback),
                normalizeString(request.ruleSetCode()),
                request.pageResourceId() == null ? fallbackPageResourceId(fallback) : request.pageResourceId(),
                normalizeStringOrNull(request.pageResourceName(), fallback == null ? null : fallback.pageResourceName()),
                request.sourceRuleId() == null ? (fallback == null ? null : fallback.sourceRuleId()) : request.sourceRuleId(),
                normalizeStringOrNull(request.sourceRuleName(), fallback == null ? null : fallback.sourceRuleName()),
                normalizeShareMode(request.shareMode() == null ? (fallback == null ? "PRIVATE" : fallback.shareMode()) : request.shareMode()),
                normalizeSharedOrgIds(request.sharedOrgIds() == null ? (fallback == null ? List.of() : fallback.sharedOrgIds()) : request.sharedOrgIds()),
                normalizeStringOrNull(request.sharedBy(), fallback == null ? null : fallback.sharedBy()),
                normalizeStringOrNull(request.sharedAt(), fallback == null ? null : fallback.sharedAt()),
                request.priority() == null ? (fallback == null ? 0 : fallback.priority()) : request.priority(),
                normalizeStringOrNull(request.promptMode(), fallback == null ? null : fallback.promptMode()),
                normalizeStringOrNull(request.closeMode(), fallback == null ? null : fallback.closeMode()),
                normalizeStringOrNull(request.promptContentConfigJson(), fallback == null ? null : fallback.promptContentConfigJson()),
                request.closeTimeoutSec() == null ? (fallback == null ? null : fallback.closeTimeoutSec()) : request.closeTimeoutSec(),
                request.hasConfirmButton() == null ? (fallback != null && Boolean.TRUE.equals(fallback.hasConfirmButton())) : request.hasConfirmButton(),
                request.sceneId() == null ? (fallback == null ? null : fallback.sceneId()) : request.sceneId(),
                normalizeStringOrNull(request.sceneName(), fallback == null ? null : fallback.sceneName()),
                normalizeStringOrNull(request.effectiveStartAt(), fallback == null ? null : fallback.effectiveStartAt()),
                normalizeStringOrNull(request.effectiveEndAt(), fallback == null ? null : fallback.effectiveEndAt()),
                status,
                currentVersion,
                normalizeString(request.ownerOrgId() == null ? ownerOrgFallback : request.ownerOrgId()),
                request.listLookupConditions() == null ? (fallback == null ? objectMapper.createArrayNode() : fallback.listLookupConditions()) : request.listLookupConditions(),
                nowText()
        );
    }

    private String resolveDefaultOwnerOrgId() {
        RequestContext context = RequestContextHolder.get();
        if (context == null) {
            return "100001";
        }
        String orgId = normalizeString(context.orgId());
        return orgId.isBlank() ? "100001" : orgId;
    }

    private RuleDefinitionDO toDo(RuleDefinitionView view) {
        RuleDefinitionDO row = new RuleDefinitionDO();
        row.setId(view.id());
        row.setRuleName(view.name());
        row.setPageResourceId(view.pageResourceId() == null ? 0L : view.pageResourceId());
        row.setOwnerOrgId(view.ownerOrgId());
        row.setTriggerMode(normalizeString(view.promptMode()));
        row.setStatus(view.status());
        row.setCurrentVersionId(view.currentVersion() == null ? null : view.currentVersion().longValue());
        row.setContentJson(writeJson(view));
        return row;
    }

    private RuleDefinitionView toView(RuleDefinitionDO row) {
        RuleDefinitionView parsed = readView(row.getContentJson());
        if (parsed != null) {
            return parsed.withId(row.getId())
                    .withName(normalizeStringOrNull(parsed.name(), row.getRuleName()))
                    .withPageResourceId(parsed.pageResourceId() == null || parsed.pageResourceId() == 0 ? null : parsed.pageResourceId())
                    .withOwnerOrgId(normalizeStringOrNull(parsed.ownerOrgId(), row.getOwnerOrgId()))
                    .withStatus(normalizeStringOrNull(parsed.status(), row.getStatus()))
                    .withCurrentVersion(parsed.currentVersion() == null ? (row.getCurrentVersionId() == null ? 1 : row.getCurrentVersionId().intValue()) : parsed.currentVersion())
                    .withUpdatedAt(row.getUpdateTime() == null ? parsed.updatedAt() : row.getUpdateTime().toString());
        }
        return new RuleDefinitionView(
                row.getId(),
                row.getRuleName(),
                "PAGE_RESOURCE",
                row.getRuleName() == null ? "" : row.getRuleName().toLowerCase(),
                row.getPageResourceId() == null || row.getPageResourceId() == 0 ? null : row.getPageResourceId(),
                null,
                null,
                null,
                "PRIVATE",
                List.of(),
                null,
                null,
                0,
                normalizeString(row.getTriggerMode()),
                "MANUAL_CLOSE",
                "{}",
                null,
                false,
                null,
                null,
                null,
                null,
                normalizeString(row.getStatus()),
                row.getCurrentVersionId() == null ? 1 : row.getCurrentVersionId().intValue(),
                row.getOwnerOrgId(),
                objectMapper.createArrayNode(),
                row.getUpdateTime() == null ? "" : row.getUpdateTime().toString()
        );
    }

    private RuleDefinitionView readView(String contentJson) {
        if (contentJson == null || contentJson.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(contentJson, RuleDefinitionView.class);
        } catch (Exception error) {
            return null;
        }
    }

    private String writeJson(RuleDefinitionView view) {
        try {
            return objectMapper.writeValueAsString(view);
        } catch (JsonProcessingException error) {
            throw new IllegalArgumentException("规则内容序列化失败", error);
        }
    }

    private String normalizeString(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalizeStringOrNull(String value, String fallback) {
        String normalized = normalizeString(value);
        if (!normalized.isBlank()) {
            return normalized;
        }
        return fallback;
    }

    private String normalizeStatus(String value) {
        String normalized = normalizeString(value).toUpperCase();
        return normalized.isBlank() ? "DRAFT" : normalized;
    }

    private String normalizeShareMode(String value) {
        String normalized = normalizeString(value).toUpperCase();
        return "SHARED".equals(normalized) ? "SHARED" : "PRIVATE";
    }

    private List<String> normalizeSharedOrgIds(List<String> values) {
        if (values == null) {
            return List.of();
        }
        return values.stream()
                .map(this::normalizeString)
                .filter(item -> !item.isBlank())
                .distinct()
                .toList();
    }

    private String normalizeRuleScope(String value, RuleDefinitionView fallback) {
        String normalized = normalizeString(value).toUpperCase();
        if ("SHARED".equals(normalized) || "PAGE_RESOURCE".equals(normalized)) {
            return normalized;
        }
        return fallback == null ? "PAGE_RESOURCE" : fallback.ruleScope();
    }

    private Long fallbackPageResourceId(RuleDefinitionView fallback) {
        return fallback == null ? null : fallback.pageResourceId();
    }

    private String nowText() {
        return LocalDateTime.now().toString();
    }
}
