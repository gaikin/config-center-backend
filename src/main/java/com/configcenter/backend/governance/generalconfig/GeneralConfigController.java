package com.configcenter.backend.governance.generalconfig;

import com.configcenter.backend.common.api.ApiResponse;
import com.configcenter.backend.governance.generalconfig.dto.GeneralConfigItemStatusUpdateRequest;
import com.configcenter.backend.governance.generalconfig.dto.GeneralConfigItemUpsertRequest;
import com.configcenter.backend.governance.generalconfig.dto.GeneralConfigItemView;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/governance/general-config-items")
public class GeneralConfigController {

    private final GeneralConfigService generalConfigService;

    public GeneralConfigController(GeneralConfigService generalConfigService) {
        this.generalConfigService = generalConfigService;
    }

    @GetMapping
    public ApiResponse<List<GeneralConfigItemView>> list(@RequestParam(required = false) String groupKey) {
        return ApiResponse.success(generalConfigService.listGeneralConfigItems(groupKey));
    }

    @PostMapping
    public ApiResponse<GeneralConfigItemView> create(@RequestBody GeneralConfigItemUpsertRequest request) {
        return ApiResponse.success(generalConfigService.upsertGeneralConfigItem(request));
    }

    @PostMapping("/{id}")
    public ApiResponse<GeneralConfigItemView> update(@PathVariable Long id, @RequestBody GeneralConfigItemUpsertRequest request) {
        return ApiResponse.success(generalConfigService.updateGeneralConfigItem(id, request));
    }

    @PostMapping("/{id}/status")
    public ApiResponse<GeneralConfigItemView> updateStatus(
            @PathVariable Long id,
            @RequestBody GeneralConfigItemStatusUpdateRequest request
    ) {
        return ApiResponse.success(generalConfigService.updateGeneralConfigItemStatus(id, request));
    }
}

