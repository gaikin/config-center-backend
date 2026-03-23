package com.configcenter.backend.runtime.snapshot.dto;

import java.util.List;

public class RuntimeBundleView {

    private RuntimeBundleManifestView manifest;
    private RuntimePageConfigView pageConfig;
    private List<RuntimeRuleConfigView> ruleConfigs;
    private List<RuntimeInterfaceConfigView> interfaceConfigs;

    public RuntimeBundleManifestView getManifest() {
        return manifest;
    }

    public void setManifest(RuntimeBundleManifestView manifest) {
        this.manifest = manifest;
    }

    public RuntimePageConfigView getPageConfig() {
        return pageConfig;
    }

    public void setPageConfig(RuntimePageConfigView pageConfig) {
        this.pageConfig = pageConfig;
    }

    public List<RuntimeRuleConfigView> getRuleConfigs() {
        return ruleConfigs;
    }

    public void setRuleConfigs(List<RuntimeRuleConfigView> ruleConfigs) {
        this.ruleConfigs = ruleConfigs;
    }

    public List<RuntimeInterfaceConfigView> getInterfaceConfigs() {
        return interfaceConfigs;
    }

    public void setInterfaceConfigs(List<RuntimeInterfaceConfigView> interfaceConfigs) {
        this.interfaceConfigs = interfaceConfigs;
    }
}
