package com.configcenter.backend.control.publish.dto;

import java.util.List;

public record PublishValidationView(
        boolean pass,
        List<PublishValidationItemView> items
) {
}
