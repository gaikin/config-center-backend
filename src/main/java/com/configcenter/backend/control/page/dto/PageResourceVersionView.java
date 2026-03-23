package com.configcenter.backend.control.page.dto;

public record PageResourceVersionView(
        Long id,
        Long pageResourceId,
        Integer versionNo,
        String status,
        String contentJson
) {
}
