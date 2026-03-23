package com.configcenter.backend.governance.dto;

import java.time.LocalDateTime;
import java.util.List;

public record MenuSdkPolicyView(
        Long id,
        String menuCode,
        String menuName,
        boolean promptGrayEnabled,
        String promptGrayVersion,
        List<String> promptGrayOrgIds,
        boolean jobGrayEnabled,
        String jobGrayVersion,
        List<String> jobGrayOrgIds,
        LocalDateTime effectiveStart,
        LocalDateTime effectiveEnd,
        String status,
        LocalDateTime updateTime
) {
}
