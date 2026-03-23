package com.configcenter.backend.control.interfaceapi;

import com.configcenter.backend.common.api.ApiResponse;
import com.configcenter.backend.common.api.PageResponse;
import com.configcenter.backend.control.interfaceapi.dto.InterfaceDefinitionDetailView;
import com.configcenter.backend.control.interfaceapi.dto.InterfaceDefinitionUpsertRequest;
import com.configcenter.backend.control.interfaceapi.dto.InterfaceDefinitionView;
import com.configcenter.backend.control.interfaceapi.dto.InterfaceStatusUpdateRequest;
import com.configcenter.backend.control.interfaceapi.dto.InterfaceVersionUpdateRequest;
import com.configcenter.backend.control.interfaceapi.dto.InterfaceVersionView;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/control/interfaces")
public class InterfaceRegistryController {

    private final InterfaceRegistryService interfaceRegistryService;

    public InterfaceRegistryController(InterfaceRegistryService interfaceRegistryService) {
        this.interfaceRegistryService = interfaceRegistryService;
    }

    @GetMapping
    public ApiResponse<PageResponse<InterfaceDefinitionView>> listInterfaces(
            @RequestParam(defaultValue = "1") Long pageNo,
            @RequestParam(defaultValue = "20") Long pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String ownerOrgId
    ) {
        return ApiResponse.success(interfaceRegistryService.listInterfaces(pageNo, pageSize, keyword, status, ownerOrgId));
    }

    @GetMapping("/{interfaceId}")
    public ApiResponse<InterfaceDefinitionDetailView> getInterfaceDetail(@PathVariable Long interfaceId) {
        return ApiResponse.success(interfaceRegistryService.getInterfaceDetail(interfaceId));
    }

    @PostMapping
    public ApiResponse<InterfaceDefinitionView> saveInterfaceDraft(@RequestBody @Valid InterfaceDefinitionUpsertRequest body) {
        return ApiResponse.success(interfaceRegistryService.saveInterfaceDraft(body));
    }

    @PostMapping("/{interfaceId}/versions")
    public ApiResponse<InterfaceVersionView> createVersion(@PathVariable Long interfaceId) {
        return ApiResponse.success(interfaceRegistryService.createInterfaceVersion(interfaceId));
    }

    @PostMapping("/{interfaceId}/versions/{versionId}")
    public ApiResponse<InterfaceVersionView> updateVersion(
            @PathVariable Long interfaceId,
            @PathVariable Long versionId,
            @RequestBody @Valid InterfaceVersionUpdateRequest body
    ) {
        return ApiResponse.success(interfaceRegistryService.updateInterfaceVersion(interfaceId, versionId, body));
    }

    @PostMapping("/{interfaceId}/status")
    public ApiResponse<InterfaceDefinitionView> updateStatus(
            @PathVariable Long interfaceId,
            @RequestBody @Valid InterfaceStatusUpdateRequest body
    ) {
        return ApiResponse.success(interfaceRegistryService.updateInterfaceStatus(interfaceId, body));
    }
}

