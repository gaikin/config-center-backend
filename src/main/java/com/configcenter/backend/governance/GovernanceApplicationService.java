package com.configcenter.backend.governance;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.configcenter.backend.infrastructure.db.governance.mapper.MenuSdkPolicyMapper;
import com.configcenter.backend.infrastructure.db.governance.mapper.PlatformRuntimeConfigMapper;
import com.configcenter.backend.infrastructure.db.governance.model.MenuSdkPolicyDO;
import com.configcenter.backend.infrastructure.db.governance.model.PlatformRuntimeConfigDO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class GovernanceApplicationService {

    private static final TypeReference<List<String>> STRING_LIST_TYPE = new TypeReference<>() {};

    private final PlatformRuntimeConfigMapper platformRuntimeConfigMapper;
    private final MenuSdkPolicyMapper menuSdkPolicyMapper;
    private final ObjectMapper objectMapper;

    public GovernanceApplicationService(
            PlatformRuntimeConfigMapper platformRuntimeConfigMapper,
            MenuSdkPolicyMapper menuSdkPolicyMapper,
            ObjectMapper objectMapper
    ) {
        this.platformRuntimeConfigMapper = platformRuntimeConfigMapper;
        this.menuSdkPolicyMapper = menuSdkPolicyMapper;
        this.objectMapper = objectMapper;
    }

    public Map<String, Object> getPlatformRuntimeConfig() {
        PlatformRuntimeConfigDO config = platformRuntimeConfigMapper.selectById(1L);
        if (config == null) {
            PlatformRuntimeConfigDO fallback = new PlatformRuntimeConfigDO();
            fallback.setId(1L);
            fallback.setPromptStableVersion("");
            fallback.setJobStableVersion("");
            return toPlatformMap(fallback);
        }
        return toPlatformMap(config);
    }

    public Map<String, Object> upsertPlatformRuntimeConfig(Map<String, Object> payload) {
        PlatformRuntimeConfigDO config = platformRuntimeConfigMapper.selectById(1L);
        if (config == null) {
            config = new PlatformRuntimeConfigDO();
            config.setId(1L);
        }

        config.setPromptStableVersion(readText(payload, "promptStableVersion"));
        config.setPromptGrayDefaultVersion(readText(payload, "promptGrayDefaultVersion"));
        config.setJobStableVersion(readText(payload, "jobStableVersion"));
        config.setJobGrayDefaultVersion(readText(payload, "jobGrayDefaultVersion"));

        if (platformRuntimeConfigMapper.selectById(1L) == null) {
            platformRuntimeConfigMapper.insert(config);
        } else {
            platformRuntimeConfigMapper.updateById(config);
        }

        return toPlatformMap(platformRuntimeConfigMapper.selectById(1L));
    }

    public List<Map<String, Object>> listMenuSdkPolicies() {
        return menuSdkPolicyMapper.selectList(new LambdaQueryWrapper<MenuSdkPolicyDO>()
                        .eq(MenuSdkPolicyDO::getIsDeleted, 0)
                        .orderByDesc(MenuSdkPolicyDO::getId))
                .stream()
                .map(this::toPolicyMap)
                .toList();
    }

    public Map<String, Object> createMenuSdkPolicy(Map<String, Object> payload) {
        MenuSdkPolicyDO policy = new MenuSdkPolicyDO();
        applyPolicyPayload(policy, payload);
        menuSdkPolicyMapper.insert(policy);
        return toPolicyMap(menuSdkPolicyMapper.selectById(policy.getId()));
    }

    public Map<String, Object> updateMenuSdkPolicy(Long id, Map<String, Object> payload) {
        MenuSdkPolicyDO policy = menuSdkPolicyMapper.selectById(id);
        if (policy == null) {
            policy = new MenuSdkPolicyDO();
            policy.setId(id);
        }
        applyPolicyPayload(policy, payload);
        if (menuSdkPolicyMapper.selectById(id) == null) {
            menuSdkPolicyMapper.insert(policy);
        } else {
            menuSdkPolicyMapper.updateById(policy);
        }
        return toPolicyMap(menuSdkPolicyMapper.selectById(policy.getId()));
    }

    private void applyPolicyPayload(MenuSdkPolicyDO policy, Map<String, Object> payload) {
        policy.setMenuCode(readText(payload, "menuCode"));
        policy.setMenuName(readText(payload, "menuName"));
        policy.setPromptGrayEnabled(readBoolean(payload, "promptGrayEnabled"));
        policy.setPromptGrayVersion(readText(payload, "promptGrayVersion"));
        policy.setPromptGrayOrgIdsJson(writeJson(readStringList(payload, "promptGrayOrgIds")));
        policy.setJobGrayEnabled(readBoolean(payload, "jobGrayEnabled"));
        policy.setJobGrayVersion(readText(payload, "jobGrayVersion"));
        policy.setJobGrayOrgIdsJson(writeJson(readStringList(payload, "jobGrayOrgIds")));
        policy.setEffectiveStart(readDateTime(payload, "effectiveStart"));
        policy.setEffectiveEnd(readDateTime(payload, "effectiveEnd"));
        policy.setStatus(defaultText(readText(payload, "status"), "ACTIVE"));
    }

    private Map<String, Object> toPlatformMap(PlatformRuntimeConfigDO item) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("promptStableVersion", item.getPromptStableVersion());
        row.put("promptGrayDefaultVersion", item.getPromptGrayDefaultVersion());
        row.put("jobStableVersion", item.getJobStableVersion());
        row.put("jobGrayDefaultVersion", item.getJobGrayDefaultVersion());
        row.put("updatedAt", item.getUpdatedAt());
        return row;
    }

    private Map<String, Object> toPolicyMap(MenuSdkPolicyDO item) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", item.getId());
        row.put("menuCode", item.getMenuCode());
        row.put("menuName", item.getMenuName());
        row.put("promptGrayEnabled", Boolean.TRUE.equals(item.getPromptGrayEnabled()));
        row.put("promptGrayVersion", item.getPromptGrayVersion());
        row.put("promptGrayOrgIds", parseJsonList(item.getPromptGrayOrgIdsJson()));
        row.put("jobGrayEnabled", Boolean.TRUE.equals(item.getJobGrayEnabled()));
        row.put("jobGrayVersion", item.getJobGrayVersion());
        row.put("jobGrayOrgIds", parseJsonList(item.getJobGrayOrgIdsJson()));
        row.put("effectiveStart", item.getEffectiveStart());
        row.put("effectiveEnd", item.getEffectiveEnd());
        row.put("status", item.getStatus());
        row.put("updatedAt", item.getUpdatedAt());
        return row;
    }

    private String readText(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        return value == null ? null : String.valueOf(value);
    }

    private boolean readBoolean(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value instanceof Boolean bool) {
            return bool;
        }
        if (value instanceof Number number) {
            return number.intValue() != 0;
        }
        return Boolean.parseBoolean(String.valueOf(value));
    }

    private LocalDateTime readDateTime(Map<String, Object> payload, String key) {
        String raw = readText(payload, key);
        if (!StringUtils.hasText(raw)) {
            return LocalDateTime.now();
        }
        return LocalDateTime.parse(raw);
    }

    private String writeJson(List<String> values) {
        try {
            return objectMapper.writeValueAsString(values);
        } catch (Exception ex) {
            return "[]";
        }
    }

    private List<String> parseJsonList(String raw) {
        if (!StringUtils.hasText(raw)) {
            return List.of();
        }
        try {
            return objectMapper.readValue(raw, STRING_LIST_TYPE);
        } catch (Exception ex) {
            return List.of();
        }
    }

    private List<String> readStringList(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value instanceof List<?> list) {
            return list.stream().map(String::valueOf).toList();
        }
        return List.of();
    }

    private String defaultText(String value, String defaultValue) {
        return StringUtils.hasText(value) ? value : defaultValue;
    }
}
