package com.configcenter.backend.runtime.context;

import com.configcenter.backend.common.api.ApiResponse;
import com.configcenter.backend.runtime.context.dto.PageContextResolveRequest;
import com.configcenter.backend.runtime.context.dto.PageContextView;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/runtime/page-context")
public class PageContextController {

    private final PageContextService pageContextService;

    public PageContextController(PageContextService pageContextService) {
        this.pageContextService = pageContextService;
    }

    @PostMapping("/resolve")
    public ApiResponse<PageContextView> resolve(@RequestBody PageContextResolveRequest body) {
        return ApiResponse.success(pageContextService.resolve(body));
    }
}

