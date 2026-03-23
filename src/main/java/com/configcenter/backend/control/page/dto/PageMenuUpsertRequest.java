package com.configcenter.backend.control.page.dto;

import jakarta.validation.constraints.NotBlank;

public record PageMenuUpsertRequest(
        @NotBlank String regionId,
        @NotBlank String menuCode,
        @NotBlank String menuName,
        @NotBlank String urlPattern,
        @NotBlank String status
) {
}
