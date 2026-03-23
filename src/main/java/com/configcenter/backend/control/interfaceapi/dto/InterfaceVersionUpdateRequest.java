package com.configcenter.backend.control.interfaceapi.dto;

import jakarta.validation.constraints.NotBlank;

public record InterfaceVersionUpdateRequest(
        @NotBlank String status,
        @NotBlank String contentJson
) {
}
