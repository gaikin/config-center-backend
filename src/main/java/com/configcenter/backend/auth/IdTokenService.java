package com.configcenter.backend.auth;

import com.configcenter.backend.auth.dto.IdTokenClaims;
import com.configcenter.backend.common.exception.BizException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class IdTokenService {

    private static final String TOKEN_VERSION = "v1";
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final ObjectMapper objectMapper;
    private final String issuer;
    private final String secret;
    private final long expireSeconds;

    public IdTokenService(
            ObjectMapper objectMapper,
            @Value("${auth.id-token.issuer:config-center-backend}") String issuer,
            @Value("${auth.id-token.secret:config-center-id-token-secret}") String secret,
            @Value("${auth.id-token.expire-seconds:7200}") long expireSeconds
    ) {
        this.objectMapper = objectMapper;
        this.issuer = issuer;
        this.secret = secret;
        this.expireSeconds = Math.max(60L, expireSeconds);
    }

    public IdTokenClaims buildClaims(String userId, String orgId, List<String> roleIds) {
        long issuedAt = Instant.now().getEpochSecond();
        long expiresAt = issuedAt + expireSeconds;
        return new IdTokenClaims(
                issuer,
                normalize(userId),
                normalize(orgId),
                roleIds == null ? List.of() : roleIds.stream().map(this::normalize).filter(item -> !item.isEmpty()).distinct().toList(),
                issuedAt,
                expiresAt
        );
    }

    public String issueToken(IdTokenClaims claims) {
        try {
            byte[] payloadBytes = objectMapper.writeValueAsBytes(claims);
            String payloadSegment = base64UrlEncode(payloadBytes);
            String signingInput = TOKEN_VERSION + "." + payloadSegment;
            String signatureSegment = sign(signingInput);
            return signingInput + "." + signatureSegment;
        } catch (Exception exception) {
            throw new BizException("TOKEN_ISSUE_FAILED", "idToken签发失败", 500);
        }
    }

    public IdTokenClaims validateAndParse(String idToken) {
        if (idToken == null || idToken.isBlank()) {
            throw new BizException("UNAUTHORIZED", "idToken缺失，请先登录", 401);
        }
        String[] parts = idToken.split("\\.");
        if (parts.length != 3 || !TOKEN_VERSION.equals(parts[0])) {
            throw new BizException("UNAUTHORIZED", "idToken格式非法", 401);
        }
        String signingInput = parts[0] + "." + parts[1];
        String expectedSignature = sign(signingInput);
        if (!MessageDigest.isEqual(
                expectedSignature.getBytes(StandardCharsets.UTF_8),
                parts[2].getBytes(StandardCharsets.UTF_8)
        )) {
            throw new BizException("UNAUTHORIZED", "idToken签名非法", 401);
        }
        try {
            byte[] payloadBytes = base64UrlDecode(parts[1]);
            IdTokenClaims claims = objectMapper.readValue(payloadBytes, IdTokenClaims.class);
            validateClaims(claims);
            return new IdTokenClaims(
                    issuer,
                    normalize(claims.userId()),
                    normalize(claims.orgId()),
                    claims.roleIds() == null ? List.of() : claims.roleIds().stream()
                            .map(this::normalize)
                            .filter(item -> !item.isEmpty())
                            .distinct()
                            .toList(),
                    claims.issuedAtEpochSecond(),
                    claims.expiresAtEpochSecond()
            );
        } catch (BizException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new BizException("UNAUTHORIZED", "idToken解析失败", 401);
        }
    }

    private void validateClaims(IdTokenClaims claims) {
        if (claims == null) {
            throw new BizException("UNAUTHORIZED", "idToken内容为空", 401);
        }
        if (!issuer.equals(normalize(claims.issuer()))) {
            throw new BizException("UNAUTHORIZED", "idToken发行方不匹配", 401);
        }
        if (normalize(claims.userId()).isEmpty() || normalize(claims.orgId()).isEmpty()) {
            throw new BizException("UNAUTHORIZED", "idToken身份信息缺失", 401);
        }
        long now = Instant.now().getEpochSecond();
        long expiresAt = claims.expiresAtEpochSecond() == null ? 0L : claims.expiresAtEpochSecond();
        if (expiresAt < now) {
            throw new BizException("UNAUTHORIZED", "idToken已过期，请重新登录", 401);
        }
    }

    private String sign(String signingInput) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            byte[] digest = mac.doFinal(signingInput.getBytes(StandardCharsets.UTF_8));
            return base64UrlEncode(digest);
        } catch (Exception exception) {
            throw new BizException("TOKEN_SIGN_FAILED", "idToken签名失败", 500);
        }
    }

    private String base64UrlEncode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private byte[] base64UrlDecode(String text) {
        return Base64.getUrlDecoder().decode(text);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
