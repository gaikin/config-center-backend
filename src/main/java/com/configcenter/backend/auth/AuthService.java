package com.configcenter.backend.auth;

import com.configcenter.backend.auth.dto.IdTokenClaims;
import com.configcenter.backend.auth.dto.LoginRequest;
import com.configcenter.backend.auth.dto.LoginResponse;
import com.configcenter.backend.common.exception.BizException;
import java.util.List;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final JdbcTemplate jdbcTemplate;
    private final IdTokenService idTokenService;

    public AuthService(JdbcTemplate jdbcTemplate, IdTokenService idTokenService) {
        this.jdbcTemplate = jdbcTemplate;
        this.idTokenService = idTokenService;
    }

    public LoginResponse login(LoginRequest request) {
        String userId = normalize(request.userId());
        String password = request.password() == null ? "" : request.password();
        String orgId = resolveOrgIdByCredentials(userId, password);

        List<String> roleIds = jdbcTemplate.queryForList(
                """
                        SELECT DISTINCT r.role_type
                        FROM user_role_binding b
                        JOIN cc_role r ON r.id = b.role_id
                        WHERE b.user_id = ?
                          AND UPPER(COALESCE(b.status, '')) = 'ACTIVE'
                          AND UPPER(COALESCE(r.status, '')) = 'ACTIVE'
                        """,
                String.class,
                userId
        ).stream()
                .map(this::normalize)
                .filter(item -> !item.isEmpty())
                .distinct()
                .toList();

        List<String> effectiveRoleIds = roleIds.isEmpty() ? List.of("CONFIG_OPERATOR") : roleIds;
        IdTokenClaims claims = idTokenService.buildClaims(userId, orgId, effectiveRoleIds);
        String idToken = idTokenService.issueToken(claims);
        return new LoginResponse(
                idToken,
                claims.expiresAtEpochSecond(),
                claims.userId(),
                claims.orgId(),
                claims.roleIds()
        );
    }

    private String resolveOrgIdByCredentials(String userId, String password) {
        try {
            String orgId = jdbcTemplate.queryForObject(
                    """
                            SELECT u.dpt_id
                            FROM user_inf u
                            JOIN dpt_inf d ON d.dpt_id = u.dpt_id
                            WHERE u.user_id = ?
                              AND COALESCE(u.pwd, '') = ?
                              AND UPPER(COALESCE(u.status, '')) IN ('ACTIVE', 'Y', '0', '1')
                              AND UPPER(COALESCE(d.status, '')) IN ('ACTIVE', 'Y', '0', '1')
                            ORDER BY u.id DESC
                            LIMIT 1
                            """,
                    String.class,
                    userId,
                    password
            );
            String normalizedOrgId = normalize(orgId);
            if (normalizedOrgId.isEmpty()) {
                throw new BizException("UNAUTHORIZED", "账号缺少机构信息，请联系管理员", 401);
            }
            return normalizedOrgId;
        } catch (EmptyResultDataAccessException exception) {
            throw new BizException("UNAUTHORIZED", "账号或密码错误，请确认后重试", 401);
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
