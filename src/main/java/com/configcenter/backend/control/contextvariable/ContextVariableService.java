package com.configcenter.backend.control.contextvariable;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.configcenter.backend.control.contextvariable.dto.ContextVariableStatusUpdateRequest;
import com.configcenter.backend.control.contextvariable.dto.ContextVariableUpsertRequest;
import com.configcenter.backend.control.contextvariable.dto.ContextVariableView;
import com.configcenter.backend.infrastructure.db.control.contextvariable.ContextVariableMapper;
import com.configcenter.backend.infrastructure.db.control.contextvariable.model.ContextVariableDO;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ContextVariableService {

    private final ContextVariableMapper contextVariableMapper;

    public ContextVariableService(ContextVariableMapper contextVariableMapper) {
        this.contextVariableMapper = contextVariableMapper;
    }

    public List<ContextVariableView> list() {
        return contextVariableMapper.selectList(Wrappers.<ContextVariableDO>lambdaQuery().orderByAsc(ContextVariableDO::getId))
                .stream()
                .map(this::toView)
                .toList();
    }

    public ContextVariableView upsert(ContextVariableUpsertRequest request) {
        String key = normalizeRequired(request.key(), "变量Key");
        String label = normalizeRequired(request.label(), "名称");
        String valueSource = normalizeRequired(request.valueSource(), "取值方式");
        String status = normalizeRequired(request.status(), "状态");
        String ownerOrgId = normalizeRequired(request.ownerOrgId(), "归属机构");
        ContextVariableDO existsById = request.id() == null ? null : contextVariableMapper.selectById(request.id());
        ContextVariableDO duplicated = contextVariableMapper.selectOne(
                Wrappers.<ContextVariableDO>lambdaQuery()
                        .eq(ContextVariableDO::getVariableKey, key)
                        .ne(request.id() != null, ContextVariableDO::getId, request.id())
        );
        if (duplicated != null) {
            throw new IllegalArgumentException("变量Key已存在，请更换");
        }

        ContextVariableDO next = new ContextVariableDO();
        next.setId(existsById == null ? (request.id() == null ? nextId() : request.id()) : existsById.getId());
        next.setVariableKey(key);
        next.setLabel(label);
        next.setValueSource(valueSource);
        next.setStaticValue("SCRIPT".equals(valueSource) ? null : trimToNull(request.staticValue()));
        next.setScriptContent("SCRIPT".equals(valueSource) ? trimToNull(request.scriptContent()) : null);
        next.setStatus(status);
        next.setOwnerOrgId(ownerOrgId);

        if (existsById == null) {
            contextVariableMapper.insert(next);
        } else {
            contextVariableMapper.updateById(next);
        }
        return toView(contextVariableMapper.selectById(next.getId()));
    }

    public ContextVariableView updateStatus(Long id, ContextVariableStatusUpdateRequest request) {
        ContextVariableDO existing = contextVariableMapper.selectById(id);
        if (existing == null) {
            throw new IllegalArgumentException("上下文变量不存在");
        }
        existing.setStatus(normalizeRequired(request.status(), "状态"));
        contextVariableMapper.updateById(existing);
        return toView(existing);
    }

    private ContextVariableView toView(ContextVariableDO row) {
        return new ContextVariableView(
                row.getId(),
                row.getVariableKey(),
                row.getLabel(),
                row.getValueSource(),
                row.getStaticValue(),
                row.getScriptContent(),
                row.getStatus(),
                row.getOwnerOrgId()
        );
    }

    private String normalizeRequired(String value, String label) {
        String normalized = value == null ? "" : value.trim();
        if (!StringUtils.hasText(normalized)) {
            throw new IllegalArgumentException("请输入" + label);
        }
        return normalized;
    }

    private String trimToNull(String value) {
        String normalized = value == null ? "" : value.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private Long nextId() {
        return contextVariableMapper.selectList(Wrappers.<ContextVariableDO>lambdaQuery())
                .stream()
                .map(ContextVariableDO::getId)
                .filter(id -> id != null)
                .max(Long::compareTo)
                .map(id -> id + 1)
                .orElse(1L);
    }
}
