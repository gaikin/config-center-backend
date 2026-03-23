package com.configcenter.backend.control.dataprocessor;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.configcenter.backend.control.dataprocessor.dto.DataProcessorStatusUpdateRequest;
import com.configcenter.backend.control.dataprocessor.dto.DataProcessorUpsertRequest;
import com.configcenter.backend.control.dataprocessor.dto.DataProcessorView;
import com.configcenter.backend.infrastructure.db.control.dataprocessor.DataProcessorMapper;
import com.configcenter.backend.infrastructure.db.control.dataprocessor.model.DataProcessorDO;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class DataProcessorService {

    private static final Pattern TRANSFORM_PATTERN = Pattern.compile("^function\\s+transform\\s*\\(([^)]*)\\)");

    private final DataProcessorMapper dataProcessorMapper;

    public DataProcessorService(DataProcessorMapper dataProcessorMapper) {
        this.dataProcessorMapper = dataProcessorMapper;
    }

    public List<DataProcessorView> list() {
        return dataProcessorMapper.selectList(Wrappers.<DataProcessorDO>lambdaQuery().orderByAsc(DataProcessorDO::getId))
                .stream()
                .map(this::toView)
                .toList();
    }

    public DataProcessorView upsert(DataProcessorUpsertRequest request) {
        DataProcessorDO next = new DataProcessorDO();
        DataProcessorDO existing = request.id() == null ? null : dataProcessorMapper.selectById(request.id());
        next.setId(existing == null ? (request.id() == null ? nextId() : request.id()) : existing.getId());
        next.setName(normalizeRequired(request.name(), "名称"));
        next.setParamCount(validateParamCount(request.paramCount()));
        next.setFunctionCode(normalizeFunctionCode(request.functionCode(), next.getParamCount()));
        next.setStatus(normalizeRequired(request.status(), "状态"));
        next.setUsedByCount(existing == null ? 0 : existing.getUsedByCount());

        if (existing == null) {
            dataProcessorMapper.insert(next);
        } else {
            next.setUsedByCount(existing.getUsedByCount() == null ? 0 : existing.getUsedByCount());
            dataProcessorMapper.updateById(next);
        }
        return toView(dataProcessorMapper.selectById(next.getId()));
    }

    public DataProcessorView updateStatus(Long id, DataProcessorStatusUpdateRequest request) {
        DataProcessorDO existing = dataProcessorMapper.selectById(id);
        if (existing == null) {
            throw new IllegalArgumentException("数据处理函数不存在");
        }
        existing.setStatus(normalizeRequired(request.status(), "状态"));
        dataProcessorMapper.updateById(existing);
        return toView(existing);
    }

    private DataProcessorView toView(DataProcessorDO row) {
        return new DataProcessorView(
                row.getId(),
                row.getName(),
                row.getParamCount(),
                row.getFunctionCode(),
                row.getStatus(),
                row.getUsedByCount() == null ? 0 : row.getUsedByCount()
        );
    }

    private String normalizeRequired(String value, String label) {
        String normalized = value == null ? "" : value.trim();
        if (!StringUtils.hasText(normalized)) {
            throw new IllegalArgumentException("请输入" + label);
        }
        return normalized;
    }

    private Integer validateParamCount(Integer paramCount) {
        if (paramCount == null || paramCount < 1) {
            throw new IllegalArgumentException("参数个数至少为 1");
        }
        return paramCount;
    }

    private String normalizeFunctionCode(String functionCode, Integer paramCount) {
        String normalized = functionCode == null ? "" : functionCode.trim();
        Matcher matcher = TRANSFORM_PATTERN.matcher(normalized);
        if (!matcher.find()) {
            throw new IllegalArgumentException("请使用 function transform(...) 标准函数声明");
        }
        String params = matcher.group(1).trim();
        int actualCount = params.isEmpty() ? 0 : params.split("\\s*,\\s*").length;
        if (actualCount != paramCount) {
            throw new IllegalArgumentException("函数参数个数与配置不一致");
        }
        return normalized;
    }

    private Long nextId() {
        return dataProcessorMapper.selectList(Wrappers.<DataProcessorDO>lambdaQuery())
                .stream()
                .map(DataProcessorDO::getId)
                .filter(id -> id != null)
                .max(Long::compareTo)
                .map(id -> id + 1)
                .orElse(1L);
    }
}
