package com.configcenter.backend.control.contextvariable;

import com.configcenter.backend.common.api.ApiResponse;
import com.configcenter.backend.control.contextvariable.dto.ContextVariableStatusUpdateRequest;
import com.configcenter.backend.control.contextvariable.dto.ContextVariableUpsertRequest;
import com.configcenter.backend.control.contextvariable.dto.ContextVariableView;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/control/context-variables")
public class ContextVariableController {

    private final ContextVariableService contextVariableService;

    public ContextVariableController(ContextVariableService contextVariableService) {
        this.contextVariableService = contextVariableService;
    }

    @GetMapping
    public ApiResponse<List<ContextVariableView>> list() {
        return ApiResponse.success(contextVariableService.list());
    }

    @PostMapping
    public ApiResponse<ContextVariableView> upsert(@RequestBody ContextVariableUpsertRequest request) {
        return ApiResponse.success(contextVariableService.upsert(request));
    }

    @PostMapping("/{id}/status")
    public ApiResponse<ContextVariableView> updateStatus(
            @PathVariable Long id,
            @RequestBody ContextVariableStatusUpdateRequest request
    ) {
        return ApiResponse.success(contextVariableService.updateStatus(id, request));
    }
}

