package com.configcenter.backend.runtime.sdk;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.configcenter.backend.infrastructure.db.control.jobscene.JobSceneMapper;
import com.configcenter.backend.infrastructure.db.control.jobscene.JobSceneNodeMapper;
import com.configcenter.backend.infrastructure.db.control.jobscene.model.JobSceneDO;
import com.configcenter.backend.infrastructure.db.control.jobscene.model.JobSceneNodeDO;
import com.configcenter.backend.infrastructure.db.control.rule.RuleConditionMapper;
import com.configcenter.backend.infrastructure.db.control.rule.RuleMapper;
import com.configcenter.backend.infrastructure.db.control.rule.model.RuleConditionDO;
import com.configcenter.backend.infrastructure.db.control.rule.model.RuleDefinitionDO;
import com.configcenter.backend.runtime.sdk.dto.RuntimePromptFieldRuleView;
import com.configcenter.backend.runtime.sdk.dto.RuntimeJobNodeView;
import com.configcenter.backend.runtime.sdk.dto.RuntimeJobPackageView;
import com.configcenter.backend.runtime.sdk.dto.RuntimeJobSceneView;
import com.configcenter.backend.runtime.sdk.dto.RuntimePromptBundleView;
import com.configcenter.backend.runtime.sdk.dto.RuntimePromptItemView;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class RuntimeSdkService {

    private static final DateTimeFormatter BUNDLE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final RuleMapper ruleMapper;
    private final RuleConditionMapper ruleConditionMapper;
    private final JobSceneMapper jobSceneMapper;
    private final JobSceneNodeMapper jobSceneNodeMapper;
    private final ObjectMapper objectMapper;

    public RuntimeSdkService(
            RuleMapper ruleMapper,
            RuleConditionMapper ruleConditionMapper,
            JobSceneMapper jobSceneMapper,
            JobSceneNodeMapper jobSceneNodeMapper,
            ObjectMapper objectMapper
    ) {
        this.ruleMapper = ruleMapper;
        this.ruleConditionMapper = ruleConditionMapper;
        this.jobSceneMapper = jobSceneMapper;
        this.jobSceneNodeMapper = jobSceneNodeMapper;
        this.objectMapper = objectMapper;
    }

    public RuntimePromptBundleView getPromptBundle(Long pageId) {
        List<RuntimePromptItemView> prompts = ruleMapper.selectList(
                        new LambdaQueryWrapper<RuleDefinitionDO>()
                                .eq(RuleDefinitionDO::getPageResourceId, pageId)
                                .eq(RuleDefinitionDO::getStatus, "ACTIVE")
                                .orderByDesc(RuleDefinitionDO::getUpdateTime)
                                .orderByDesc(RuleDefinitionDO::getId)
                )
                .stream()
                .map(this::toPromptItem)
                .sorted(Comparator.comparing(RuntimePromptItemView::priority, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
        return new RuntimePromptBundleView(buildBundleVersion("prompt", pageId), String.valueOf(pageId), prompts);
    }

    public RuntimeJobPackageView getJobPackage(Long pageResourceId, Long ruleId, Long sceneId) {
        Long targetSceneId = sceneId != null ? sceneId : resolveRuleSceneId(ruleId);
        List<RuntimeJobSceneView> jobs = jobSceneMapper.selectList(
                        new LambdaQueryWrapper<JobSceneDO>()
                                .eq(JobSceneDO::getPageResourceId, pageResourceId)
                                .eq(JobSceneDO::getStatus, "ACTIVE")
                                .eq(targetSceneId != null, JobSceneDO::getId, targetSceneId)
                                .orderByDesc(JobSceneDO::getUpdateTime)
                                .orderByDesc(JobSceneDO::getId)
                )
                .stream()
                .map(this::toJobSceneView)
                .toList();

        return new RuntimeJobPackageView(buildBundleVersion("job", pageResourceId), String.valueOf(pageResourceId), jobs);
    }

    private RuntimePromptItemView toPromptItem(RuleDefinitionDO row) {
        JsonNode content = parseJsonNode(row.getContentJson());
        JsonNode promptConfig = parseJsonNode(readText(content, "promptContentConfigJson", null));
        String ruleId = String.valueOf(row.getId());
        String ruleName = readText(content, "name", row.getRuleName());
        String titleSuffix = readText(promptConfig, "titleSuffix", null);
        String title = StringUtils.hasText(titleSuffix) ? ruleName + " - " + titleSuffix : ruleName;
        String bodyTemplate = readText(promptConfig, "bodyTemplate", null);
        String sceneId = readLongText(content, "sceneId");
        return new RuntimePromptItemView(
                ruleId,
                ruleName,
                "trace-rule-" + ruleId + "-" + System.currentTimeMillis(),
                normalizePromptMode(readText(content, "promptMode", row.getTriggerMode())),
                title,
                StringUtils.hasText(bodyTemplate) ? bodyTemplate : "触发了智能提示，请执行后续智能作业。",
                readBoolean(content, "hasConfirmButton", false) ? "确认" : null,
                "关闭",
                sceneId,
                readText(content, "sceneName", null),
                String.valueOf(row.getPageResourceId()),
                readText(content, "pageResourceName", null),
                readInteger(content, "priority", 0),
                resolveFieldRules(row.getId())
        );
    }

    private List<RuntimePromptFieldRuleView> resolveFieldRules(Long ruleId) {
        if (ruleId == null) {
            return List.of();
        }
        return ruleConditionMapper.selectList(
                        new LambdaQueryWrapper<RuleConditionDO>()
                                .eq(RuleConditionDO::getRuleId, ruleId)
                                .orderByAsc(RuleConditionDO::getId)
                )
                .stream()
                .map(this::toFieldRule)
                .filter(item -> item != null)
                .toList();
    }

    private RuntimePromptFieldRuleView toFieldRule(RuleConditionDO row) {
        JsonNode left = parseJsonNode(row.getLeftJson());
        JsonNode right = parseJsonNode(row.getRightJson());
        String leftSourceType = readText(left, "sourceType", "");
        if (!"PAGE_FIELD".equalsIgnoreCase(leftSourceType)) {
            return null;
        }
        String fieldKey = firstNonBlank(
                readText(left, "fieldKey", null),
                readText(left, "key", null),
                readText(left, "fieldCode", null)
        );
        if (!StringUtils.hasText(fieldKey)) {
            return null;
        }
        String expectedValue = firstNonBlank(
                readText(right, "constValue", null),
                readText(right, "value", null),
                readText(right, "key", null)
        );
        String operator = normalizeText(row.getOperator()).toUpperCase();
        if (!StringUtils.hasText(operator)) {
            operator = "EQ";
        }
        return new RuntimePromptFieldRuleView(
                String.valueOf(row.getId()),
                fieldKey,
                readText(left, "selector", null),
                operator,
                normalizeText(expectedValue)
        );
    }

    private RuntimeJobSceneView toJobSceneView(JobSceneDO row) {
        List<RuntimeJobNodeView> nodes = jobSceneNodeMapper.selectList(
                        new LambdaQueryWrapper<JobSceneNodeDO>()
                                .eq(JobSceneNodeDO::getSceneId, row.getId())
                                .orderByAsc(JobSceneNodeDO::getOrderNo)
                                .orderByAsc(JobSceneNodeDO::getId)
                )
                .stream()
                .map(node -> new RuntimeJobNodeView(
                        String.valueOf(node.getId()),
                        normalizeText(node.getNodeType()),
                        normalizeText(node.getName()),
                        node.getOrderNo() == null ? 0 : node.getOrderNo(),
                        Boolean.TRUE.equals(node.getEnabled()),
                        normalizeJson(node.getConfigJson())
                ))
                .toList();

        return new RuntimeJobSceneView(
                String.valueOf(row.getId()),
                normalizeText(row.getName()),
                String.valueOf(row.getPageResourceId()),
                normalizeText(row.getPageResourceName()),
                normalizeText(row.getExecutionMode()),
                normalizeText(row.getStatus()),
                nodes
        );
    }

    private Long resolveRuleSceneId(Long ruleId) {
        if (ruleId == null) {
            return null;
        }
        RuleDefinitionDO row = ruleMapper.selectById(ruleId);
        if (row == null) {
            return null;
        }
        JsonNode content = parseJsonNode(row.getContentJson());
        String sceneId = readLongText(content, "sceneId");
        if (!StringUtils.hasText(sceneId)) {
            return null;
        }
        try {
            return Long.parseLong(sceneId);
        } catch (NumberFormatException error) {
            return null;
        }
    }

    private String buildBundleVersion(String prefix, Long resourceId) {
        return prefix + "-" + resourceId + "-" + LocalDateTime.now().format(BUNDLE_FORMATTER);
    }

    private JsonNode parseJsonNode(String rawJson) {
        if (!StringUtils.hasText(rawJson)) {
            return objectMapper.createObjectNode();
        }
        try {
            return objectMapper.readTree(rawJson);
        } catch (Exception error) {
            return objectMapper.createObjectNode();
        }
    }

    private String readText(JsonNode root, String field, String fallback) {
        if (root == null) {
            return normalizeText(fallback);
        }
        JsonNode node = root.get(field);
        if (node == null || node.isNull()) {
            return normalizeText(fallback);
        }
        if (node.isTextual()) {
            return normalizeText(node.asText());
        }
        return normalizeText(node.toString());
    }

    private String readLongText(JsonNode root, String field) {
        if (root == null) {
            return null;
        }
        JsonNode node = root.get(field);
        if (node == null || node.isNull()) {
            return null;
        }
        if (node.isNumber()) {
            return String.valueOf(node.asLong());
        }
        String value = normalizeText(node.asText());
        return StringUtils.hasText(value) ? value : null;
    }

    private boolean readBoolean(JsonNode root, String field, boolean fallback) {
        if (root == null) {
            return fallback;
        }
        JsonNode node = root.get(field);
        if (node == null || node.isNull()) {
            return fallback;
        }
        if (node.isBoolean()) {
            return node.asBoolean();
        }
        String value = normalizeText(node.asText());
        if (!StringUtils.hasText(value)) {
            return fallback;
        }
        return "true".equalsIgnoreCase(value) || "1".equals(value);
    }

    private Integer readInteger(JsonNode root, String field, Integer fallback) {
        if (root == null) {
            return fallback;
        }
        JsonNode node = root.get(field);
        if (node == null || node.isNull()) {
            return fallback;
        }
        if (node.isNumber()) {
            return node.asInt();
        }
        try {
            return Integer.parseInt(normalizeText(node.asText()));
        } catch (Exception error) {
            return fallback;
        }
    }

    private String normalizePromptMode(String value) {
        return "SILENT".equalsIgnoreCase(normalizeText(value)) ? "SILENT" : "FLOATING";
    }

    private String normalizeText(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalizeJson(String value) {
        if (!StringUtils.hasText(value)) {
            return "{}";
        }
        return value.trim();
    }

    private String firstNonBlank(String... candidates) {
        for (String candidate : candidates) {
            if (StringUtils.hasText(candidate)) {
                return candidate.trim();
            }
        }
        return "";
    }
}
