package com.configcenter.backend.control.publish.dto;

public record PublishValidationItemView(
        String type,
        String target,
        String reason
) {
}
