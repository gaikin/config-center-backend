package com.configcenter.backend.common.filter;

import com.configcenter.backend.auth.IdTokenService;
import com.configcenter.backend.auth.dto.IdTokenClaims;
import com.configcenter.backend.common.api.ApiResponse;
import com.configcenter.backend.common.context.RequestContext;
import com.configcenter.backend.common.context.RequestContextHolder;
import com.configcenter.backend.common.exception.BizException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component("traceContextFilter")
public class RequestContextFilter extends OncePerRequestFilter {

    private final IdTokenService idTokenService;
    private final ObjectMapper objectMapper;

    @Value("${auth.id-token.enforce:true}")
    private boolean enforceIdToken;

    public RequestContextFilter(IdTokenService idTokenService, ObjectMapper objectMapper) {
        this.idTokenService = idTokenService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String traceId = readHeader(request, "X-Trace-Id", "trace-" + UUID.randomUUID());
        response.setHeader("X-Trace-Id", traceId);

        if (HttpMethod.OPTIONS.matches(request.getMethod()) || isPublicEndpoint(request.getRequestURI())) {
            applyLegacyContext(request, traceId);
            try {
                filterChain.doFilter(request, response);
            } finally {
                RequestContextHolder.clear();
            }
            return;
        }

        if (!enforceIdToken) {
            applyLegacyContext(request, traceId);
            try {
                filterChain.doFilter(request, response);
            } finally {
                RequestContextHolder.clear();
            }
            return;
        }

        try {
            IdTokenClaims claims = idTokenService.validateAndParse(extractIdToken(request));
            RequestContextHolder.set(new RequestContext(
                    request.getHeader("Authorization"),
                    traceId,
                    claims.userId(),
                    claims.orgId(),
                    claims.roleIds() == null ? List.of() : claims.roleIds()
            ));
        } catch (BizException exception) {
            writeUnauthorized(response, exception.getCode(), exception.getMessage());
            return;
        } catch (Exception exception) {
            writeUnauthorized(response, "UNAUTHORIZED", "idToken校验失败，请重新登录");
            return;
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            RequestContextHolder.clear();
        }
    }

    private void applyLegacyContext(HttpServletRequest request, String traceId) {
        String userId = readHeader(request, "X-User-Id", "system.demo");
        String orgId = readHeader(request, "X-Org-Id", "org.demo");
        List<String> roleIds = Arrays.stream(readHeader(request, "X-Role-Ids", "business-config").split(","))
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .collect(Collectors.toList());
        RequestContextHolder.set(new RequestContext(
                request.getHeader("Authorization"),
                traceId,
                userId,
                orgId,
                roleIds
        ));
    }

    private String extractIdToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring("Bearer ".length()).trim();
        }
        String fallback = request.getHeader("X-Id-Token");
        return fallback == null ? "" : fallback.trim();
    }

    private boolean isPublicEndpoint(String uri) {
        if (uri == null || uri.isBlank()) {
            return false;
        }
        return "/healthz".equals(uri)
                || "/api/auth/login".equals(uri)
                || uri.startsWith("/h2-console")
                || uri.startsWith("/error");
    }

    private void writeUnauthorized(HttpServletResponse response, String code, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(response.getWriter(), ApiResponse.failure(code, message, null));
    }

    private String readHeader(HttpServletRequest request, String name, String defaultValue) {
        String value = request.getHeader(name);
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
