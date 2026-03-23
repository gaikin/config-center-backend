package com.configcenter.backend.control.page;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.configcenter.backend.common.api.PageResponse;
import com.configcenter.backend.control.page.dto.PageMenuView;
import com.configcenter.backend.control.page.dto.PageMenuUpsertRequest;
import com.configcenter.backend.control.page.dto.PageResourceDetailView;
import com.configcenter.backend.control.page.dto.PageResourceUpsertRequest;
import com.configcenter.backend.control.page.dto.PageResourceVersionContentView;
import com.configcenter.backend.control.page.dto.PageResourceVersionUpdateRequest;
import com.configcenter.backend.control.page.dto.PageResourceVersionView;
import com.configcenter.backend.control.page.dto.PageResourceView;
import com.configcenter.backend.infrastructure.db.control.page.PageMenuMapper;
import com.configcenter.backend.infrastructure.db.control.page.PageResourceMapper;
import com.configcenter.backend.infrastructure.db.control.page.model.PageMenuDO;
import com.configcenter.backend.infrastructure.db.control.page.model.PageResourceDO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PageResourceService {

    private final PageMenuMapper pageMenuMapper;
    private final PageResourceMapper pageResourceMapper;
    private final ObjectMapper objectMapper;

    public PageResourceService(
            PageMenuMapper pageMenuMapper,
            PageResourceMapper pageResourceMapper,
            ObjectMapper objectMapper
    ) {
        this.pageMenuMapper = pageMenuMapper;
        this.pageResourceMapper = pageResourceMapper;
        this.objectMapper = objectMapper;
    }

    public List<PageMenuView> listMenus() {
        return pageMenuMapper.selectList(new LambdaQueryWrapper<PageMenuDO>().orderByAsc(PageMenuDO::getId))
                .stream()
                .map(this::toMenuView)
                .toList();
    }

    @Transactional
    public PageMenuView createMenu(PageMenuUpsertRequest body) {
        String regionId = normalizeRequired(body.regionId(), "请选择专区");
        String menuCode = normalizeRequired(body.menuCode(), "menuCode is required");
        String menuName = normalizeRequired(body.menuName(), "menuName is required");
        String urlPattern = normalizeRequired(body.urlPattern(), "urlPattern is required");
        String status = normalizeRequired(body.status(), "status is required");
        PageMenuDO exists = pageMenuMapper.selectOne(
                new LambdaQueryWrapper<PageMenuDO>().eq(PageMenuDO::getMenuCode, menuCode)
        );
        if (exists != null) {
            throw new IllegalArgumentException("菜单编码已存在");
        }
        PageMenuDO row = new PageMenuDO();
        row.setRegionId(regionId);
        row.setMenuCode(menuCode);
        row.setMenuName(menuName);
        row.setUrlPattern(urlPattern);
        row.setStatus(status);
        pageMenuMapper.insert(row);
        return toMenuView(row);
    }

    public PageResponse<PageResourceView> listPageResources(
            Long pageNo,
            Long pageSize,
            String keyword,
            String status,
            String ownerOrgId
    ) {
        long currentPage = Math.max(1L, pageNo);
        long currentPageSize = Math.max(1L, pageSize);
        String normalizedStatus = status == null ? "" : status.trim();
        String normalizedOwnerOrgId = ownerOrgId == null ? "" : ownerOrgId.trim();
        String normalizedKeyword = keyword == null ? "" : keyword.trim();
        LambdaQueryWrapper<PageResourceDO> wrapper = new LambdaQueryWrapper<PageResourceDO>()
                .and(!normalizedKeyword.isEmpty(), inner -> inner
                        .like(PageResourceDO::getPageName, normalizedKeyword)
                        .or()
                        .like(PageResourceDO::getPageCode, normalizedKeyword))
                .eq(!normalizedStatus.isEmpty(), PageResourceDO::getStatus, normalizedStatus)
                .eq(!normalizedOwnerOrgId.isEmpty(), PageResourceDO::getOwnerOrgId, normalizedOwnerOrgId)
                .orderByAsc(PageResourceDO::getId);
        Page<PageResourceDO> page = pageResourceMapper.selectPage(new Page<>(currentPage, currentPageSize), wrapper);
        List<PageResourceView> records = page.getRecords().stream()
                .map(this::toResourceView)
                .toList();
        return new PageResponse<>(page.getTotal(), currentPage, currentPageSize, records);
    }

    public PageResourceDetailView getPageResourceDetail(Long pageId) {
        PageResourceDO resource = pageResourceMapper.selectById(pageId);
        if (resource == null) {
            throw new IllegalArgumentException("页面资源不存在");
        }
        Integer currentVersionNo = resolveCurrentVersion(resource, null);
        PageResourceVersionView currentVersion = new PageResourceVersionView(
                currentVersionNo.longValue(),
                pageId,
                currentVersionNo,
                resource.getStatus(),
                toContentJson(resource, "URL兜底")
        );
        return new PageResourceDetailView(toResourceView(resource), List.of(currentVersion));
    }

    @Transactional
    public PageResourceView createPageResource(PageResourceUpsertRequest body) {
        String menuCode = normalizeOptional(body.menuCode());
        if (menuCode.isEmpty()) {
            throw new IllegalArgumentException("请选择所属菜单");
        }
        PageMenuDO menu = pageMenuMapper.selectOne(
                new LambdaQueryWrapper<PageMenuDO>().eq(PageMenuDO::getMenuCode, menuCode)
        );
        if (menu == null) {
            throw new IllegalArgumentException("请选择有效菜单");
        }
        PageResourceDO resource = Optional.ofNullable(body.id())
                .map(pageResourceMapper::selectById)
                .orElse(null);
        PageResourceDO next = new PageResourceDO();
        next.setId(resource != null ? resource.getId() : null);
        next.setMenuCode(menuCode);
        next.setPageCode(body.pageCode().trim());
        next.setPageName(body.name().trim());
        next.setOwnerOrgId(body.ownerOrgId().trim());
        next.setStatus(body.status().trim());
        next.setCurrentVersionId(resolveCurrentVersion(resource, null).longValue());
        if (resource == null) {
            pageResourceMapper.insert(next);
        } else {
            pageResourceMapper.updateById(next);
        }
        return toResourceView(next);
    }

    @Transactional
    public PageResourceVersionView createPageResourceVersion(Long pageId) {
        PageResourceDO resource = pageResourceMapper.selectById(pageId);
        if (resource == null) {
            throw new IllegalArgumentException("页面资源不存在");
        }
        Integer nextVersionNo = resolveCurrentVersion(resource, null) + 1;
        resource.setCurrentVersionId(nextVersionNo.longValue());
        pageResourceMapper.updateById(resource);
        return new PageResourceVersionView(
                nextVersionNo.longValue(),
                pageId,
                nextVersionNo,
                resource.getStatus(),
                toContentJson(resource, "URL兜底")
        );
    }

    @Transactional
    public PageResourceVersionView updatePageResourceVersion(Long pageId, Long versionId, PageResourceVersionUpdateRequest body) {
        PageResourceDO resource = pageResourceMapper.selectById(pageId);
        if (resource == null) {
            throw new IllegalArgumentException("页面资源不存在");
        }
        Integer targetVersionNo = resolveCurrentVersion(resource, versionId == null ? null : versionId.intValue());
        applyContentToResource(resource, body.contentJson());
        resource.setStatus(body.status().trim());
        resource.setCurrentVersionId(targetVersionNo.longValue());
        pageResourceMapper.updateById(resource);
        return new PageResourceVersionView(
                targetVersionNo.longValue(),
                pageId,
                targetVersionNo,
                resource.getStatus(),
                body.contentJson().trim()
        );
    }

    private PageMenuView toMenuView(PageMenuDO menu) {
        return new PageMenuView(
                menu.getId(),
                menu.getRegionId(),
                menu.getMenuCode(),
                menu.getMenuName(),
                menu.getUrlPattern(),
                menu.getStatus(),
                "head-office"
        );
    }

    private String normalizeRequired(String value, String message) {
        String normalized = value == null ? "" : value.trim();
        if (normalized.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return normalized;
    }

    private String normalizeOptional(String value) {
        return value == null ? "" : value.trim();
    }

    private PageResourceView toResourceView(PageResourceDO resource) {
        Integer currentVersionNo = resolveCurrentVersion(resource, null);
        return new PageResourceView(
                resource.getId(),
                resource.getMenuCode(),
                resource.getPageCode(),
                null,
                resource.getPageName(),
                resource.getStatus(),
                resource.getOwnerOrgId(),
                currentVersionNo,
                0,
                "URL兜底",
                resource.getUpdateTime() == null ? "" : resource.getUpdateTime().toString()
        );
    }

    private String toContentJson(PageResourceDO resource, String detectRulesSummary) {
        PageResourceVersionContentView content = new PageResourceVersionContentView(
                resource.getPageName(),
                resource.getPageCode(),
                null,
                detectRulesSummary,
                resource.getOwnerOrgId()
        );
        try {
            return objectMapper.writeValueAsString(content);
        } catch (Exception ex) {
            throw new IllegalStateException("页面版本内容序列化失败", ex);
        }
    }

    private void applyContentToResource(PageResourceDO resource, String contentJson) {
        if (contentJson == null || contentJson.isBlank()) {
            return;
        }
        try {
            JsonNode node = objectMapper.readTree(contentJson);
            JsonNode pageName = node.get("pageName");
            if (pageName != null && !pageName.isNull()) {
                String value = pageName.asText("");
                if (!value.isBlank()) {
                    resource.setPageName(value.trim());
                }
            }
            JsonNode pageCode = node.get("pageCode");
            if (pageCode != null && !pageCode.isNull()) {
                String value = pageCode.asText("");
                if (!value.isBlank()) {
                    resource.setPageCode(value.trim());
                }
            }
            JsonNode ownerOrgId = node.get("ownerOrgId");
            if (ownerOrgId != null && !ownerOrgId.isNull()) {
                String value = ownerOrgId.asText("");
                if (!value.isBlank()) {
                    resource.setOwnerOrgId(value.trim());
                }
            }
        } catch (Exception ex) {
            throw new IllegalStateException("页面版本内容解析失败", ex);
        }
    }

    private Integer resolveCurrentVersion(PageResourceDO existing, Integer requestedVersion) {
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
}
