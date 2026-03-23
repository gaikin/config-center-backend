package com.configcenter.backend.runtime.sdk.dto;

public record RuntimeJobNodeView(
        String nodeId,
        String nodeType,
        String nodeName,
        Integer orderNo,
        boolean enabled,
        String configJson
) {
}

