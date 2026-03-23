package com.configcenter.backend.control.page;

import com.configcenter.backend.common.api.ApiResponse;
import com.configcenter.backend.common.api.PageResponse;
import com.configcenter.backend.control.page.dto.PageMenuView;
import com.configcenter.backend.control.page.dto.PageMenuUpsertRequest;
import com.configcenter.backend.control.page.dto.PageResourceDetailView;
import com.configcenter.backend.control.page.dto.PageResourceUpsertRequest;
import com.configcenter.backend.control.page.dto.PageResourceVersionUpdateRequest;
import com.configcenter.backend.control.page.dto.PageResourceVersionView;
import com.configcenter.backend.control.page.dto.PageResourceView;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/control")
public class PageResourceController {

    private final PageResourceService pageResourceService;

    public PageResourceController(PageResourceService pageResourceService) {
        this.pageResourceService = pageResourceService;
    }

    @GetMapping("/page-menus")
    public ApiResponse<List<PageMenuView>> listMenus() {
        return ApiResponse.success(pageResourceService.listMenus());
    }

    @PostMapping("/page-menus")
    public ApiResponse<PageMenuView> createMenu(@Valid @RequestBody PageMenuUpsertRequest body) {
        return ApiResponse.success(pageResourceService.createMenu(body));
    }

    @GetMapping("/page-resources")
    public ApiResponse<PageResponse<PageResourceView>> listPageResources(
            @RequestParam(defaultValue = "1") Long pageNo,
            @RequestParam(defaultValue = "20") Long pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String ownerOrgId
    ) {
        return ApiResponse.success(pageResourceService.listPageResources(pageNo, pageSize, keyword, status, ownerOrgId));
    }

    @GetMapping("/page-resources/{pageId}")
    public ApiResponse<PageResourceDetailView> getPageResourceDetail(@PathVariable Long pageId) {
        return ApiResponse.success(pageResourceService.getPageResourceDetail(pageId));
    }

    @PostMapping("/page-resources")
    public ApiResponse<PageResourceView> createPageResource(@Valid @RequestBody PageResourceUpsertRequest body) {
        return ApiResponse.success(pageResourceService.createPageResource(body));
    }

    @PostMapping("/page-resources/{pageId}/versions")
    public ApiResponse<PageResourceVersionView> createPageResourceVersion(@PathVariable Long pageId) {
        return ApiResponse.success(pageResourceService.createPageResourceVersion(pageId));
    }

    @PostMapping("/page-resources/{pageId}/versions/{versionId}")
    public ApiResponse<PageResourceVersionView> updatePageResourceVersion(
            @PathVariable Long pageId,
            @PathVariable Long versionId,
            @Valid @RequestBody PageResourceVersionUpdateRequest body
    ) {
        return ApiResponse.success(pageResourceService.updatePageResourceVersion(pageId, versionId, body));
    }
}

