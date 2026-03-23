package com.configcenter.backend.control.interfaceapi;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.configcenter.backend.common.api.PageResponse;
import com.configcenter.backend.control.interfaceapi.dto.InterfaceDefinitionDetailView;
import com.configcenter.backend.control.interfaceapi.dto.InterfaceDefinitionUpsertRequest;
import com.configcenter.backend.control.interfaceapi.dto.InterfaceDefinitionView;
import com.configcenter.backend.control.interfaceapi.dto.InterfaceStatusUpdateRequest;
import com.configcenter.backend.control.interfaceapi.dto.InterfaceVersionUpdateRequest;
import com.configcenter.backend.control.interfaceapi.dto.InterfaceVersionContentView;
import com.configcenter.backend.control.interfaceapi.dto.InterfaceVersionView;
import com.configcenter.backend.infrastructure.db.control.interfaceapi.InterfaceRegistryMapper;
import com.configcenter.backend.infrastructure.db.control.interfaceapi.model.InterfaceDefinitionDO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InterfaceRegistryService {

    private static final int DEFAULT_TIMEOUT_MS = 3000;

    private final InterfaceRegistryMapper interfaceRegistryMapper;
    private final ObjectMapper objectMapper;

    public InterfaceRegistryService(
            InterfaceRegistryMapper interfaceRegistryMapper,
            ObjectMapper objectMapper
    ) {
        this.interfaceRegistryMapper = interfaceRegistryMapper;
        this.objectMapper = objectMapper;
    }

    public PageResponse<InterfaceDefinitionView> listInterfaces(
            Long pageNo,
            Long pageSize,
            String keyword,
            String status,
            String ownerOrgId
    ) {
        long currentPage = Math.max(1L, pageNo);
        long currentPageSize = Math.max(1L, pageSize);
        String normalizedKeyword = normalize(keyword);
        String normalizedStatus = normalize(status);
        String normalizedOwnerOrgId = normalize(ownerOrgId);
        LambdaQueryWrapper<InterfaceDefinitionDO> wrapper = new LambdaQueryWrapper<InterfaceDefinitionDO>()
                .and(!normalizedKeyword.isEmpty(), inner -> inner
                        .like(InterfaceDefinitionDO::getName, normalizedKeyword)
                        .or()
                        .like(InterfaceDefinitionDO::getDescription, normalizedKeyword)
                        .or()
                        .like(InterfaceDefinitionDO::getProdPath, normalizedKeyword)
                        .or()
                        .like(InterfaceDefinitionDO::getTestPath, normalizedKeyword)
                        .or()
                        .like(InterfaceDefinitionDO::getUrl, normalizedKeyword))
                .eq(!normalizedStatus.isEmpty(), InterfaceDefinitionDO::getStatus, normalizedStatus)
                .eq(!normalizedOwnerOrgId.isEmpty(), InterfaceDefinitionDO::getOwnerOrgId, normalizedOwnerOrgId)
                .orderByDesc(InterfaceDefinitionDO::getId);
        Page<InterfaceDefinitionDO> page = interfaceRegistryMapper.selectPage(new Page<>(currentPage, currentPageSize), wrapper);
        List<InterfaceDefinitionView> records = page.getRecords().stream()
                .map(this::toView)
                .toList();
        return new PageResponse<>(page.getTotal(), currentPage, currentPageSize, records);
    }

    public InterfaceDefinitionDetailView getInterfaceDetail(Long interfaceId) {
        InterfaceDefinitionDO definition = interfaceRegistryMapper.selectById(interfaceId);
        if (definition == null) {
            throw new IllegalArgumentException("接口定义不存在");
        }
        Integer currentVersionNo = resolveCurrentVersion(definition, null);
        InterfaceVersionView currentVersion = new InterfaceVersionView(
                currentVersionNo.longValue(),
                interfaceId,
                currentVersionNo,
                definition.getStatus(),
                toVersionContentJson(definition, currentVersionNo)
        );
        return new InterfaceDefinitionDetailView(toView(definition), List.of(currentVersion));
    }

    @Transactional
    public InterfaceDefinitionView saveInterfaceDraft(InterfaceDefinitionUpsertRequest body) {
        InterfaceDefinitionDO existing = Optional.ofNullable(body.id())
                .map(interfaceRegistryMapper::selectById)
                .orElse(null);
        boolean cloneFromActive = existing != null && "ACTIVE".equalsIgnoreCase(existing.getStatus());
        Integer nextVersionNo = cloneFromActive
                ? resolveCloneVersion(existing, body.currentVersion())
                : resolveCurrentVersion(existing, body.currentVersion());
        InterfaceDefinitionDO next = cloneFromActive ? new InterfaceDefinitionDO() : new InterfaceDefinitionDO();
        next.setId(cloneFromActive ? null : body.id());
        String normalizedProdPath = normalizeRequiredPath(body.prodPath());
        next.setName(normalize(body.name()));
        next.setDescription(normalize(body.description()));
        next.setMethod(normalizeHttpMethod(body.method()));
        next.setTestPath(normalizeOptional(body.testPath(), normalizedProdPath));
        next.setProdPath(normalizedProdPath);
        next.setPath(normalizedProdPath);
        next.setUrl(normalizeOptional(body.url(), normalizedProdPath));
        next.setOwnerOrgId(normalize(body.ownerOrgId()));
        next.setStatus(cloneFromActive ? "DRAFT" : normalizeStatus(existing, "DRAFT"));
        next.setCurrentVersionId(nextVersionNo.longValue());
        next.setTimeoutMs(body.timeoutMs() != null ? body.timeoutMs() : resolveInteger(existing == null ? null : existing.getTimeoutMs(), DEFAULT_TIMEOUT_MS));
        next.setRetryTimes(body.retryTimes() != null ? body.retryTimes() : resolveInteger(existing == null ? null : existing.getRetryTimes(), 0));
        next.setBodyTemplateJson(body.bodyTemplateJson() == null ? "" : body.bodyTemplateJson().trim());
        next.setInputConfigJson(normalize(body.inputConfigJson()));
        next.setOutputConfigJson(normalize(body.outputConfigJson()));
        next.setParamSourceSummary(normalize(body.paramSourceSummary()));
        next.setResponsePath(normalize(body.responsePath()));
        next.setMaskSensitive(body.maskSensitive() != null ? body.maskSensitive() : resolveBoolean(existing == null ? null : existing.getMaskSensitive(), Boolean.TRUE));
        ensurePathForPersistence(next);

        if (cloneFromActive) {
            interfaceRegistryMapper.insert(next);
        } else if (existing == null) {
            interfaceRegistryMapper.insert(next);
        } else {
            next.setId(existing.getId());
            interfaceRegistryMapper.updateById(next);
        }

        return toView(next);
    }

    @Transactional
    public InterfaceVersionView createInterfaceVersion(Long interfaceId) {
        InterfaceDefinitionDO definition = interfaceRegistryMapper.selectById(interfaceId);
        if (definition == null) {
            throw new IllegalArgumentException("接口定义不存在");
        }
        Integer nextVersionNo = resolveCurrentVersion(definition, null) + 1;
        definition.setCurrentVersionId(nextVersionNo.longValue());
        ensurePathForPersistence(definition);
        interfaceRegistryMapper.updateById(definition);
        return new InterfaceVersionView(
                nextVersionNo.longValue(),
                interfaceId,
                nextVersionNo,
                definition.getStatus(),
                toVersionContentJson(definition, nextVersionNo)
        );
    }

    @Transactional
    public InterfaceVersionView updateInterfaceVersion(Long interfaceId, Long versionId, InterfaceVersionUpdateRequest body) {
        InterfaceDefinitionDO definition = interfaceRegistryMapper.selectById(interfaceId);
        if (definition == null) {
            throw new IllegalArgumentException("接口定义不存在");
        }
        Integer targetVersionNo = resolveCurrentVersion(definition, versionId == null ? null : versionId.intValue());
        applyContentToDefinition(definition, body.contentJson());
        definition.setStatus(normalize(body.status()));
        definition.setCurrentVersionId(targetVersionNo.longValue());
        ensurePathForPersistence(definition);
        interfaceRegistryMapper.updateById(definition);
        return new InterfaceVersionView(
                targetVersionNo.longValue(),
                interfaceId,
                targetVersionNo,
                definition.getStatus(),
                normalize(body.contentJson())
        );
    }

    @Transactional
    public InterfaceDefinitionView updateInterfaceStatus(Long interfaceId, InterfaceStatusUpdateRequest body) {
        InterfaceDefinitionDO definition = interfaceRegistryMapper.selectById(interfaceId);
        if (definition == null) {
            throw new IllegalArgumentException("接口定义不存在");
        }
        definition.setStatus(normalize(body.status()));
        ensurePathForPersistence(definition);
        interfaceRegistryMapper.updateById(definition);
        return toView(definition);
    }

    private void applyContentToDefinition(InterfaceDefinitionDO definition, String contentJson) {
        if (contentJson == null || contentJson.isBlank()) {
            return;
        }
        try {
            JsonNode node = objectMapper.readTree(contentJson);
            definition.setName(readText(node, "name", definition.getName()));
            definition.setDescription(readText(node, "description", definition.getDescription()));
            definition.setMethod(normalizeHttpMethod(readText(node, "method", definition.getMethod())));
            definition.setTestPath(readText(node, "testPath", definition.getTestPath()));
            definition.setProdPath(readText(node, "prodPath", definition.getProdPath()));
            definition.setPath(readText(node, "path", definition.getPath()));
            definition.setUrl(readText(node, "url", definition.getUrl()));
            definition.setOwnerOrgId(readText(node, "ownerOrgId", definition.getOwnerOrgId()));
            definition.setTimeoutMs(readInteger(node, "timeoutMs", definition.getTimeoutMs()));
            definition.setRetryTimes(readInteger(node, "retryTimes", definition.getRetryTimes()));
            definition.setBodyTemplateJson(readText(node, "bodyTemplateJson", definition.getBodyTemplateJson()));
            definition.setInputConfigJson(readText(node, "inputConfigJson", definition.getInputConfigJson()));
            definition.setOutputConfigJson(readText(node, "outputConfigJson", definition.getOutputConfigJson()));
            definition.setParamSourceSummary(readText(node, "paramSourceSummary", definition.getParamSourceSummary()));
            definition.setResponsePath(readText(node, "responsePath", definition.getResponsePath()));
            definition.setMaskSensitive(readBoolean(node, "maskSensitive", definition.getMaskSensitive()));
            ensurePathForPersistence(definition);
        } catch (Exception ex) {
            throw new IllegalStateException("接口版本内容解析失败", ex);
        }
    }

    private String toVersionContentJson(InterfaceDefinitionDO definition, Integer versionNo) {
        InterfaceVersionContentView content = new InterfaceVersionContentView(
                definition.getName(),
                definition.getDescription(),
                normalizeHttpMethodOrDefault(definition.getMethod()),
                definition.getTestPath(),
                definition.getProdPath(),
                definition.getUrl(),
                definition.getStatus(),
                definition.getOwnerOrgId(),
                versionNo,
                definition.getTimeoutMs(),
                definition.getRetryTimes(),
                definition.getBodyTemplateJson(),
                definition.getInputConfigJson(),
                definition.getOutputConfigJson(),
                definition.getParamSourceSummary(),
                definition.getResponsePath(),
                definition.getMaskSensitive()
        );
        try {
            return objectMapper.writeValueAsString(content);
        } catch (Exception ex) {
            throw new IllegalStateException("接口版本内容序列化失败", ex);
        }
    }

    private InterfaceDefinitionView toView(InterfaceDefinitionDO definition) {
        Integer currentVersionNo = resolveCurrentVersion(definition, null);
        return new InterfaceDefinitionView(
                definition.getId(),
                definition.getName(),
                definition.getDescription(),
                normalizeHttpMethodOrDefault(definition.getMethod()),
                definition.getTestPath(),
                definition.getProdPath(),
                definition.getUrl(),
                definition.getStatus(),
                definition.getOwnerOrgId(),
                currentVersionNo,
                definition.getTimeoutMs(),
                definition.getRetryTimes(),
                definition.getBodyTemplateJson(),
                definition.getInputConfigJson(),
                definition.getOutputConfigJson(),
                definition.getParamSourceSummary(),
                definition.getResponsePath(),
                definition.getMaskSensitive(),
                definition.getUpdateTime() == null ? "" : definition.getUpdateTime().toString()
        );
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalizeOptional(String value, String fallback) {
        String normalized = normalize(value);
        return normalized.isEmpty() ? fallback : normalized;
    }

    private String normalizeRequiredPath(String value) {
        String normalized = normalize(value);
        if (!normalized.isEmpty()) {
            return normalized;
        }
        throw new IllegalArgumentException("prodPath不能为空");
    }

    private void ensurePathForPersistence(InterfaceDefinitionDO definition) {
        String resolvedPath = firstNonBlank(
                definition.getProdPath(),
                definition.getPath(),
                definition.getUrl(),
                definition.getTestPath()
        );
        if (resolvedPath.isEmpty()) {
            throw new IllegalArgumentException("接口path不能为空");
        }
        definition.setProdPath(normalizeOptional(definition.getProdPath(), resolvedPath));
        definition.setPath(resolvedPath);
    }

    private String firstNonBlank(String... values) {
        if (values == null) {
            return "";
        }
        for (String value : values) {
            String normalized = normalize(value);
            if (!normalized.isEmpty()) {
                return normalized;
            }
        }
        return "";
    }

    private String normalizeHttpMethod(String value) {
        String normalized = normalize(value).toUpperCase();
        if ("GET".equals(normalized) || "POST".equals(normalized)) {
            return normalized;
        }
        throw new IllegalArgumentException("method only supports GET or POST");
    }

    private String normalizeHttpMethodOrDefault(String value) {
        String normalized = normalize(value).toUpperCase();
        if ("GET".equals(normalized) || "POST".equals(normalized)) {
            return normalized;
        }
        return "POST";
    }

    private String normalizeStatus(InterfaceDefinitionDO existing, String fallback) {
        if (existing == null) {
            return fallback;
        }
        String normalized = normalize(existing.getStatus());
        return normalized.isEmpty() ? fallback : normalized;
    }

    private Integer resolveCurrentVersion(InterfaceDefinitionDO existing, Integer requestedVersion) {
        if (requestedVersion != null && requestedVersion > 0) {
            return requestedVersion;
        }
        if (existing == null || existing.getCurrentVersionId() == null || existing.getCurrentVersionId() < 1) {
            return 1;
        }
        if (existing.getCurrentVersionId() > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return existing.getCurrentVersionId().intValue();
    }

    private Integer resolveCloneVersion(InterfaceDefinitionDO existing, Integer requestedVersion) {
        if (requestedVersion != null && requestedVersion > 0) {
            return requestedVersion;
        }
        return resolveCurrentVersion(existing, null) + 1;
    }

    private Integer resolveInteger(Integer value, Integer fallback) {
        return value == null ? fallback : value;
    }

    private Boolean resolveBoolean(Boolean value, Boolean fallback) {
        return value == null ? fallback : value;
    }

    private String readText(JsonNode node, String field, String fallback) {
        JsonNode child = node.get(field);
        if (child == null || child.isNull()) {
            return fallback;
        }
        String value = child.asText("");
        return value.isEmpty() ? fallback : value;
    }

    private Integer readInteger(JsonNode node, String field, Integer fallback) {
        JsonNode child = node.get(field);
        if (child == null || child.isNull()) {
            return fallback;
        }
        if (child.canConvertToInt()) {
            return child.asInt();
        }
        return fallback;
    }

    private Boolean readBoolean(JsonNode node, String field, Boolean fallback) {
        JsonNode child = node.get(field);
        if (child == null || child.isNull()) {
            return fallback;
        }
        return child.asBoolean(fallback);
    }
}
