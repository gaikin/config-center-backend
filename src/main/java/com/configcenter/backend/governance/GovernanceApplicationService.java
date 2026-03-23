package com.configcenter.backend.governance;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.configcenter.backend.governance.dto.MenuSdkPolicyUpsertRequest;
import com.configcenter.backend.governance.dto.MenuSdkPolicyView;
import com.configcenter.backend.infrastructure.db.governance.mapper.MenuSdkPolicyMapper;
import com.configcenter.backend.infrastructure.db.governance.model.MenuSdkPolicyDO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class GovernanceApplicationService {

    private static final TypeReference<List<String>> STRING_LIST_TYPE = new TypeReference<>() {};

    private final MenuSdkPolicyMapper menuSdkPolicyMapper;
    private final ObjectMapper objectMapper;

    public GovernanceApplicationService(
            MenuSdkPolicyMapper menuSdkPolicyMapper,
            ObjectMapper objectMapper
    ) {
        this.menuSdkPolicyMapper = menuSdkPolicyMapper;
        this.objectMapper = objectMapper;
    }

    public List<MenuSdkPolicyView> listMenuSdkPolicies() {
        return menuSdkPolicyMapper.selectList(new LambdaQueryWrapper<MenuSdkPolicyDO>()
                        .orderByDesc(MenuSdkPolicyDO::getId))
                .stream()
                .map(this::toPolicyView)
                .toList();
    }

    public MenuSdkPolicyView createMenuSdkPolicy(MenuSdkPolicyUpsertRequest payload) {
        MenuSdkPolicyDO policy = new MenuSdkPolicyDO();
        applyPolicyPayload(policy, payload);
        menuSdkPolicyMapper.insert(policy);
        return toPolicyView(menuSdkPolicyMapper.selectById(policy.getId()));
    }

    public MenuSdkPolicyView updateMenuSdkPolicy(Long id, MenuSdkPolicyUpsertRequest payload) {
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
        return toPolicyView(menuSdkPolicyMapper.selectById(policy.getId()));
    }

    private void applyPolicyPayload(MenuSdkPolicyDO policy, MenuSdkPolicyUpsertRequest payload) {
        policy.setMenuCode(payload.menuCode());
        policy.setMenuName(payload.menuName());
        policy.setPromptGrayEnabled(payload.promptGrayEnabled());
        policy.setPromptGrayVersion(payload.promptGrayVersion());
        policy.setPromptGrayOrgIdsJson(writeJson(payload.promptGrayOrgIds()));
        policy.setJobGrayEnabled(payload.jobGrayEnabled());
        policy.setJobGrayVersion(payload.jobGrayVersion());
        policy.setJobGrayOrgIdsJson(writeJson(payload.jobGrayOrgIds()));
        policy.setEffectiveStart(payload.effectiveStart());
        policy.setEffectiveEnd(payload.effectiveEnd());
        policy.setStatus(defaultText(payload.status(), "ACTIVE"));
    }

    private MenuSdkPolicyView toPolicyView(MenuSdkPolicyDO item) {
        return new MenuSdkPolicyView(
                item.getId(),
                item.getMenuCode(),
                item.getMenuName(),
                Boolean.TRUE.equals(item.getPromptGrayEnabled()),
                item.getPromptGrayVersion(),
                parseJsonList(item.getPromptGrayOrgIdsJson()),
                Boolean.TRUE.equals(item.getJobGrayEnabled()),
                item.getJobGrayVersion(),
                parseJsonList(item.getJobGrayOrgIdsJson()),
                item.getEffectiveStart(),
                item.getEffectiveEnd(),
                item.getStatus(),
                item.getUpdateTime()
        );
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

    private String defaultText(String value, String defaultValue) {
        return StringUtils.hasText(value) ? value : defaultValue;
    }
}
