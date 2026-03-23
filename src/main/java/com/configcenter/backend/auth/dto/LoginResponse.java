package com.configcenter.backend.auth.dto;

import java.util.List;

public record LoginResponse(
        String idToken,
        Long expiresAtEpochSecond,
        String userId,
        String orgId,
        List<String> roleIds
) {
}
