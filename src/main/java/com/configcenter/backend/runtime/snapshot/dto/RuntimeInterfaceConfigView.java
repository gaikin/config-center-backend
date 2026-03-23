package com.configcenter.backend.runtime.snapshot.dto;

public class RuntimeInterfaceConfigView {

    private Long interfaceId;
    private String name;

    public Long getInterfaceId() {
        return interfaceId;
    }

    public void setInterfaceId(Long interfaceId) {
        this.interfaceId = interfaceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
