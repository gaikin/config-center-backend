package com.configcenter.backend.control.page.dto;

import java.util.List;

public record PageResourceDetailView(
        PageResourceView resource,
        List<PageResourceVersionView> versions
) {
}
