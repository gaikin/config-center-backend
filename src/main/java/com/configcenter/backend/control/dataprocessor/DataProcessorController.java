package com.configcenter.backend.control.dataprocessor;

import com.configcenter.backend.common.api.ApiResponse;
import com.configcenter.backend.control.dataprocessor.dto.DataProcessorStatusUpdateRequest;
import com.configcenter.backend.control.dataprocessor.dto.DataProcessorUpsertRequest;
import com.configcenter.backend.control.dataprocessor.dto.DataProcessorView;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/control/data-processors")
public class DataProcessorController {

    private final DataProcessorService dataProcessorService;

    public DataProcessorController(DataProcessorService dataProcessorService) {
        this.dataProcessorService = dataProcessorService;
    }

    @GetMapping
    public ApiResponse<List<DataProcessorView>> list() {
        return ApiResponse.success(dataProcessorService.list());
    }

    @PostMapping
    public ApiResponse<DataProcessorView> upsert(@RequestBody DataProcessorUpsertRequest request) {
        return ApiResponse.success(dataProcessorService.upsert(request));
    }

    @PostMapping("/{id}/status")
    public ApiResponse<DataProcessorView> updateStatus(
            @PathVariable Long id,
            @RequestBody DataProcessorStatusUpdateRequest request
    ) {
        return ApiResponse.success(dataProcessorService.updateStatus(id, request));
    }
}

