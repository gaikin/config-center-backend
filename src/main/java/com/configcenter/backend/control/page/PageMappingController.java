package com.configcenter.backend.control.page;

import com.configcenter.backend.common.api.ApiResponse;
import com.configcenter.backend.control.page.dto.BusinessFieldUpsertRequest;
import com.configcenter.backend.control.page.dto.BusinessFieldView;
import com.configcenter.backend.control.page.dto.PageElementUpsertRequest;
import com.configcenter.backend.control.page.dto.PageElementView;
import com.configcenter.backend.control.page.dto.PageFieldBindingUpsertRequest;
import com.configcenter.backend.control.page.dto.PageFieldBindingView;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/control")
public class PageMappingController {

    private final PageMappingService pageMappingService;

    public PageMappingController(PageMappingService pageMappingService) {
        this.pageMappingService = pageMappingService;
    }

    @GetMapping("/page-resources/{pageResourceId}/elements")
    public ApiResponse<List<PageElementView>> listPageElements(@PathVariable Long pageResourceId) {
        return ApiResponse.success(pageMappingService.listPageElements(pageResourceId));
    }

    @PostMapping("/page-elements")
    public ApiResponse<PageElementView> upsertPageElement(@RequestBody PageElementUpsertRequest request) {
        return ApiResponse.success(pageMappingService.upsertPageElement(request));
    }

    @PostMapping("/page-elements/{id}")
    public ApiResponse<Boolean> deletePageElement(@PathVariable Long id) {
        pageMappingService.deletePageElement(id);
        return ApiResponse.success(Boolean.TRUE);
    }

    @GetMapping("/business-fields")
    public ApiResponse<List<BusinessFieldView>> listBusinessFields(@RequestParam(required = false) Long pageResourceId) {
        return ApiResponse.success(pageMappingService.listBusinessFields(pageResourceId));
    }

    @PostMapping("/business-fields")
    public ApiResponse<BusinessFieldView> upsertBusinessField(@RequestBody BusinessFieldUpsertRequest request) {
        return ApiResponse.success(pageMappingService.upsertBusinessField(request));
    }

    @GetMapping("/page-resources/{pageResourceId}/field-bindings")
    public ApiResponse<List<PageFieldBindingView>> listPageFieldBindings(@PathVariable Long pageResourceId) {
        return ApiResponse.success(pageMappingService.listPageFieldBindings(pageResourceId));
    }

    @PostMapping("/page-field-bindings")
    public ApiResponse<PageFieldBindingView> upsertPageFieldBinding(@RequestBody PageFieldBindingUpsertRequest request) {
        return ApiResponse.success(pageMappingService.upsertPageFieldBinding(request));
    }

    @PostMapping("/page-field-bindings/{id}")
    public ApiResponse<Boolean> deletePageFieldBinding(@PathVariable Long id) {
        pageMappingService.deletePageFieldBinding(id);
        return ApiResponse.success(Boolean.TRUE);
    }
}


