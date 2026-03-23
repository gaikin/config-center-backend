package com.configcenter.backend.control.page;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.configcenter.backend.control.page.dto.BusinessFieldUpsertRequest;
import com.configcenter.backend.control.page.dto.BusinessFieldView;
import com.configcenter.backend.control.page.dto.PageElementUpsertRequest;
import com.configcenter.backend.control.page.dto.PageElementView;
import com.configcenter.backend.control.page.dto.PageFieldBindingUpsertRequest;
import com.configcenter.backend.control.page.dto.PageFieldBindingView;
import com.configcenter.backend.infrastructure.db.control.page.BusinessFieldMapper;
import com.configcenter.backend.infrastructure.db.control.page.PageElementMapper;
import com.configcenter.backend.infrastructure.db.control.page.PageFieldBindingMapper;
import com.configcenter.backend.infrastructure.db.control.page.PageResourceMapper;
import com.configcenter.backend.infrastructure.db.control.page.model.BusinessFieldDO;
import com.configcenter.backend.infrastructure.db.control.page.model.PageElementDO;
import com.configcenter.backend.infrastructure.db.control.page.model.PageFieldBindingDO;
import com.configcenter.backend.infrastructure.db.control.page.model.PageResourceDO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class PageMappingService {

    private final PageElementMapper pageElementMapper;
    private final BusinessFieldMapper businessFieldMapper;
    private final PageFieldBindingMapper pageFieldBindingMapper;
    private final PageResourceMapper pageResourceMapper;
    private final ObjectMapper objectMapper;

    public PageMappingService(
            PageElementMapper pageElementMapper,
            BusinessFieldMapper businessFieldMapper,
            PageFieldBindingMapper pageFieldBindingMapper,
            PageResourceMapper pageResourceMapper,
            ObjectMapper objectMapper
    ) {
        this.pageElementMapper = pageElementMapper;
        this.businessFieldMapper = businessFieldMapper;
        this.pageFieldBindingMapper = pageFieldBindingMapper;
        this.pageResourceMapper = pageResourceMapper;
        this.objectMapper = objectMapper;
    }

    public List<PageElementView> listPageElements(Long pageResourceId) {
        requirePageResource(pageResourceId);
        return pageElementMapper.selectList(
                        new LambdaQueryWrapper<PageElementDO>()
                                .eq(PageElementDO::getPageResourceId, pageResourceId)
                                .orderByDesc(PageElementDO::getUpdateTime)
                                .orderByDesc(PageElementDO::getId)
                )
                .stream()
                .map(this::toPageElementView)
                .toList();
    }

    @Transactional
    public PageElementView upsertPageElement(PageElementUpsertRequest request) {
        Long pageResourceId = requirePositive(request.pageResourceId(), "pageResourceId is required");
        requirePageResource(pageResourceId);
        PageElementDO exists = request.id() == null ? null : pageElementMapper.selectById(request.id());
        PageElementDO row = new PageElementDO();
        if (request.id() != null) {
            row.setId(request.id());
        } else if (exists != null) {
            row.setId(exists.getId());
        }
        row.setPageResourceId(pageResourceId);
        row.setLogicName(normalizeRequired(request.logicName(), "logicName is required"));
        row.setSelector(normalizeRequired(request.selector(), "selector is required"));
        row.setSelectorType(normalizeSelectorType(request.selectorType()));
        row.setFrameLocation(normalizeString(request.frameLocation()));

        if (exists == null) {
            pageElementMapper.insert(row);
            return toPageElementView(requirePageElement(row.getId()));
        }
        pageElementMapper.updateById(row);
        return toPageElementView(requirePageElement(row.getId()));
    }

    @Transactional
    public void deletePageElement(Long id) {
        pageElementMapper.deleteById(id);
    }

    public List<BusinessFieldView> listBusinessFields(Long pageResourceId) {
        LambdaQueryWrapper<BusinessFieldDO> wrapper = new LambdaQueryWrapper<>();
        if (pageResourceId != null) {
            wrapper.and(inner -> inner.eq(BusinessFieldDO::getScope, "GLOBAL")
                    .or()
                    .eq(BusinessFieldDO::getPageResourceId, pageResourceId));
        }
        wrapper.orderByDesc(BusinessFieldDO::getUpdateTime).orderByDesc(BusinessFieldDO::getId);
        return businessFieldMapper.selectList(wrapper)
                .stream()
                .map(this::toBusinessFieldView)
                .toList();
    }

    @Transactional
    public BusinessFieldView upsertBusinessField(BusinessFieldUpsertRequest request) {
        BusinessFieldDO exists = request.id() == null ? null : businessFieldMapper.selectById(request.id());
        String scope = normalizeScope(request.scope());
        Long pageResourceId = scope.equals("GLOBAL") ? null : requirePositive(request.pageResourceId(), "pageResourceId is required");
        if (pageResourceId != null) {
            requirePageResource(pageResourceId);
        }
        String code = normalizeRequired(request.code(), "code is required");
        BusinessFieldDO duplicated = businessFieldMapper.selectOne(
                new LambdaQueryWrapper<BusinessFieldDO>()
                        .eq(BusinessFieldDO::getCode, code)
                        .ne(request.id() != null, BusinessFieldDO::getId, request.id())
        );
        if (duplicated != null) {
            throw new IllegalArgumentException("字段编码已存在");
        }

        BusinessFieldDO row = new BusinessFieldDO();
        if (request.id() != null) {
            row.setId(request.id());
        } else if (exists != null) {
            row.setId(exists.getId());
        }
        row.setCode(code);
        row.setName(normalizeRequired(request.name(), "name is required"));
        row.setScope(scope);
        row.setPageResourceId(pageResourceId);
        row.setValueType(normalizeRequired(request.valueType(), "valueType is required"));
        row.setRequired(Boolean.TRUE.equals(request.required()));
        row.setDescription(normalizeString(request.description()));
        row.setOwnerOrgId(normalizeRequired(request.ownerOrgId(), "ownerOrgId is required"));
        row.setStatus(normalizeStatus(request.status()));
        row.setCurrentVersion(request.currentVersion() == null || request.currentVersion() < 1 ? 1 : request.currentVersion());
        row.setAliasesJson(writeAliases(normalizeAliases(request.aliases())));

        if (exists == null) {
            businessFieldMapper.insert(row);
            return toBusinessFieldView(requireBusinessField(row.getId()));
        }
        businessFieldMapper.updateById(row);
        return toBusinessFieldView(requireBusinessField(row.getId()));
    }

    public List<PageFieldBindingView> listPageFieldBindings(Long pageResourceId) {
        requirePageResource(pageResourceId);
        return pageFieldBindingMapper.selectList(
                        new LambdaQueryWrapper<PageFieldBindingDO>()
                                .eq(PageFieldBindingDO::getPageResourceId, pageResourceId)
                                .orderByDesc(PageFieldBindingDO::getUpdateTime)
                                .orderByDesc(PageFieldBindingDO::getId)
                )
                .stream()
                .map(this::toPageFieldBindingView)
                .toList();
    }

    @Transactional
    public PageFieldBindingView upsertPageFieldBinding(PageFieldBindingUpsertRequest request) {
        Long pageResourceId = requirePositive(request.pageResourceId(), "pageResourceId is required");
        requirePageResource(pageResourceId);
        String businessFieldCode = normalizeRequired(request.businessFieldCode(), "businessFieldCode is required");
        Long pageElementId = requirePositive(request.pageElementId(), "pageElementId is required");
        BusinessFieldDO field = businessFieldMapper.selectOne(
                new LambdaQueryWrapper<BusinessFieldDO>().eq(BusinessFieldDO::getCode, businessFieldCode)
        );
        if (field == null) {
            throw new IllegalArgumentException("业务字段不存在");
        }
        if ("PAGE_RESOURCE".equalsIgnoreCase(field.getScope())
                && (field.getPageResourceId() == null || !field.getPageResourceId().equals(pageResourceId))) {
            throw new IllegalArgumentException("页面字段归属不匹配");
        }
        PageElementDO element = requirePageElement(pageElementId);
        if (!pageResourceId.equals(element.getPageResourceId())) {
            throw new IllegalArgumentException("元素不属于当前页面");
        }

        PageFieldBindingDO exists = request.id() == null ? null : pageFieldBindingMapper.selectById(request.id());
        PageFieldBindingDO row = new PageFieldBindingDO();
        if (request.id() != null) {
            row.setId(request.id());
        } else if (exists != null) {
            row.setId(exists.getId());
        }
        row.setPageResourceId(pageResourceId);
        row.setBusinessFieldCode(businessFieldCode);
        row.setPageElementId(pageElementId);
        row.setRequired(Boolean.TRUE.equals(request.required()));

        if (exists == null) {
            pageFieldBindingMapper.insert(row);
            return toPageFieldBindingView(requirePageFieldBinding(row.getId()));
        }
        pageFieldBindingMapper.updateById(row);
        return toPageFieldBindingView(requirePageFieldBinding(row.getId()));
    }

    @Transactional
    public void deletePageFieldBinding(Long id) {
        pageFieldBindingMapper.deleteById(id);
    }

    private PageResourceDO requirePageResource(Long pageResourceId) {
        if (pageResourceId == null || pageResourceId <= 0) {
            throw new IllegalArgumentException("pageResourceId is required");
        }
        PageResourceDO row = pageResourceMapper.selectById(pageResourceId);
        if (row == null) {
            throw new IllegalArgumentException("页面资源不存在");
        }
        return row;
    }

    private PageElementDO requirePageElement(Long id) {
        PageElementDO row = pageElementMapper.selectById(id);
        if (row == null) {
            throw new IllegalArgumentException("页面元素不存在");
        }
        return row;
    }

    private BusinessFieldDO requireBusinessField(Long id) {
        BusinessFieldDO row = businessFieldMapper.selectById(id);
        if (row == null) {
            throw new IllegalArgumentException("业务字段不存在");
        }
        return row;
    }

    private PageFieldBindingDO requirePageFieldBinding(Long id) {
        PageFieldBindingDO row = pageFieldBindingMapper.selectById(id);
        if (row == null) {
            throw new IllegalArgumentException("字段绑定不存在");
        }
        return row;
    }

    private PageElementView toPageElementView(PageElementDO row) {
        return new PageElementView(
                row.getId(),
                row.getPageResourceId(),
                row.getLogicName(),
                row.getSelector(),
                row.getSelectorType(),
                row.getFrameLocation() == null ? "" : row.getFrameLocation(),
                row.getUpdateTime() == null ? "" : row.getUpdateTime().toString()
        );
    }

    private BusinessFieldView toBusinessFieldView(BusinessFieldDO row) {
        return new BusinessFieldView(
                row.getId(),
                row.getCode(),
                row.getName(),
                normalizeScope(row.getScope()),
                row.getPageResourceId(),
                row.getValueType(),
                Boolean.TRUE.equals(row.getRequired()),
                row.getDescription() == null ? "" : row.getDescription(),
                row.getOwnerOrgId(),
                normalizeStatus(row.getStatus()),
                row.getCurrentVersion() == null || row.getCurrentVersion() < 1 ? 1 : row.getCurrentVersion(),
                readAliases(row.getAliasesJson()),
                row.getUpdateTime() == null ? "" : row.getUpdateTime().toString()
        );
    }

    private PageFieldBindingView toPageFieldBindingView(PageFieldBindingDO row) {
        return new PageFieldBindingView(
                row.getId(),
                row.getPageResourceId(),
                row.getBusinessFieldCode(),
                row.getPageElementId(),
                Boolean.TRUE.equals(row.getRequired()),
                row.getUpdateTime() == null ? "" : row.getUpdateTime().toString()
        );
    }

    private String normalizeRequired(String value, String message) {
        String normalized = normalizeString(value);
        if (!StringUtils.hasText(normalized)) {
            throw new IllegalArgumentException(message);
        }
        return normalized;
    }

    private String normalizeString(String value) {
        return value == null ? "" : value.trim();
    }

    private Long requirePositive(Long value, String message) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    private String normalizeSelectorType(String selectorType) {
        String normalized = normalizeString(selectorType).toUpperCase();
        if (!"CSS".equals(normalized) && !"XPATH".equals(normalized)) {
            throw new IllegalArgumentException("selectorType is invalid");
        }
        return normalized;
    }

    private String normalizeScope(String scope) {
        String normalized = normalizeString(scope).toUpperCase();
        return "PAGE_RESOURCE".equals(normalized) ? "PAGE_RESOURCE" : "GLOBAL";
    }

    private String normalizeStatus(String status) {
        String normalized = normalizeString(status).toUpperCase();
        return normalized.isBlank() ? "DRAFT" : normalized;
    }

    private List<String> normalizeAliases(List<String> aliases) {
        if (aliases == null) {
            return List.of();
        }
        List<String> normalized = new ArrayList<>();
        for (String alias : aliases) {
            String next = normalizeString(alias);
            if (!next.isBlank() && !normalized.contains(next)) {
                normalized.add(next);
            }
        }
        return normalized;
    }

    private List<String> readAliases(String aliasesJson) {
        if (!StringUtils.hasText(aliasesJson)) {
            return List.of();
        }
        try {
            return objectMapper.readValue(aliasesJson, new TypeReference<List<String>>() {});
        } catch (Exception ignored) {
            return List.of();
        }
    }

    private String writeAliases(List<String> aliases) {
        try {
            return objectMapper.writeValueAsString(aliases == null ? List.of() : aliases);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("字段别名序列化失败", exception);
        }
    }
}

