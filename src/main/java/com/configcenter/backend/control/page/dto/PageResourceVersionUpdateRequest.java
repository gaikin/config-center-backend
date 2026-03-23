package com.configcenter.backend.control.page.dto;

import jakarta.validation.constraints.NotBlank;

public record PageResourceVersionUpdateRequest(
        @NotBlank String status,
        @NotBlank String contentJson
) {
}
