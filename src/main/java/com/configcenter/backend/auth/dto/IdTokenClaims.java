package com.configcenter.backend.auth.dto;

import java.util.List;

public record IdTokenClaims(
        String issuer,
        String userId,
        String orgId,
        List<String> roleIds,
        Long issuedAtEpochSecond,
        Long expiresAtEpochSecond
) {
}
