package com.configcenter.backend.control.interfaceapi.dto;

public record InterfaceVersionView(
        Long id,
        Long interfaceId,
        Integer versionNo,
        String status,
        String contentJson
) {
}
