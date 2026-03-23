package com.configcenter.backend.control.interfaceapi.dto;

import java.util.List;

public record InterfaceDefinitionDetailView(
        InterfaceDefinitionView definition,
        List<InterfaceVersionView> versions
) {
}
