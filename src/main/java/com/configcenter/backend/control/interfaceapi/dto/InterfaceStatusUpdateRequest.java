package com.configcenter.backend.control.interfaceapi.dto;

import jakarta.validation.constraints.NotBlank;

public record InterfaceStatusUpdateRequest(
        @NotBlank String status
) {
}
