package com.configcenter.backend.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class RuntimeCorsConfig implements WebMvcConfigurer {

    private final String allowedOriginPatterns;

    public RuntimeCorsConfig(
            @Value("${runtime.cors.allowed-origin-patterns:http://localhost:5173,http://127.0.0.1:5173,http://localhost:5174,http://127.0.0.1:5174}") String allowedOriginPatterns
    ) {
        this.allowedOriginPatterns = allowedOriginPatterns;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns(parsePatterns(allowedOriginPatterns))
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("X-Trace-Id")
                .maxAge(3600);
    }

    private String[] parsePatterns(String raw) {
        if (raw == null || raw.isBlank()) {
            return new String[]{
                    "http://localhost:5173",
                    "http://127.0.0.1:5173",
                    "http://localhost:5174",
                    "http://127.0.0.1:5174"
            };
        }
        return java.util.Arrays.stream(raw.split(","))
                .map(String::trim)
                .map(item -> item.endsWith("/") ? item.substring(0, item.length() - 1) : item)
                .filter(item -> !item.isEmpty())
                .toArray(String[]::new);
    }
}
