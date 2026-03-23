package com.configcenter.backend.control.jobscene.dto;

import java.util.List;

public class JobSceneView {
    private Long id;
    private String name;
    private String ownerOrgId;
    private String shareMode;
    private List<String> sharedOrgIds;
    private String sharedBy;
    private String sharedAt;
    private Long sourceSceneId;
    private String sourceSceneName;
    private Long pageResourceId;
    private String pageResourceName;
    private String executionMode;
    private Boolean previewBeforeExecute;
    private Boolean floatingButtonEnabled;
    private String floatingButtonLabel;
    private Integer floatingButtonX;
    private Integer floatingButtonY;
    private String status;
    private Integer manualDurationSec;
    private Boolean riskConfirmed;
    private Long nodeCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerOrgId() {
        return ownerOrgId;
    }

    public void setOwnerOrgId(String ownerOrgId) {
        this.ownerOrgId = ownerOrgId;
    }

    public String getShareMode() {
        return shareMode;
    }

    public void setShareMode(String shareMode) {
        this.shareMode = shareMode;
    }

    public List<String> getSharedOrgIds() {
        return sharedOrgIds;
    }

    public void setSharedOrgIds(List<String> sharedOrgIds) {
        this.sharedOrgIds = sharedOrgIds;
    }

    public String getSharedBy() {
        return sharedBy;
    }

    public void setSharedBy(String sharedBy) {
        this.sharedBy = sharedBy;
    }

    public String getSharedAt() {
        return sharedAt;
    }

    public void setSharedAt(String sharedAt) {
        this.sharedAt = sharedAt;
    }

    public Long getSourceSceneId() {
        return sourceSceneId;
    }

    public void setSourceSceneId(Long sourceSceneId) {
        this.sourceSceneId = sourceSceneId;
    }

    public String getSourceSceneName() {
        return sourceSceneName;
    }

    public void setSourceSceneName(String sourceSceneName) {
        this.sourceSceneName = sourceSceneName;
    }

    public Long getPageResourceId() {
        return pageResourceId;
    }

    public void setPageResourceId(Long pageResourceId) {
        this.pageResourceId = pageResourceId;
    }

    public String getPageResourceName() {
        return pageResourceName;
    }

    public void setPageResourceName(String pageResourceName) {
        this.pageResourceName = pageResourceName;
    }

    public String getExecutionMode() {
        return executionMode;
    }

    public void setExecutionMode(String executionMode) {
        this.executionMode = executionMode;
    }

    public Boolean getPreviewBeforeExecute() {
        return previewBeforeExecute;
    }

    public void setPreviewBeforeExecute(Boolean previewBeforeExecute) {
        this.previewBeforeExecute = previewBeforeExecute;
    }

    public Boolean getFloatingButtonEnabled() {
        return floatingButtonEnabled;
    }

    public void setFloatingButtonEnabled(Boolean floatingButtonEnabled) {
        this.floatingButtonEnabled = floatingButtonEnabled;
    }

    public String getFloatingButtonLabel() {
        return floatingButtonLabel;
    }

    public void setFloatingButtonLabel(String floatingButtonLabel) {
        this.floatingButtonLabel = floatingButtonLabel;
    }

    public Integer getFloatingButtonX() {
        return floatingButtonX;
    }

    public void setFloatingButtonX(Integer floatingButtonX) {
        this.floatingButtonX = floatingButtonX;
    }

    public Integer getFloatingButtonY() {
        return floatingButtonY;
    }

    public void setFloatingButtonY(Integer floatingButtonY) {
        this.floatingButtonY = floatingButtonY;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getManualDurationSec() {
        return manualDurationSec;
    }

    public void setManualDurationSec(Integer manualDurationSec) {
        this.manualDurationSec = manualDurationSec;
    }

    public Boolean getRiskConfirmed() {
        return riskConfirmed;
    }

    public void setRiskConfirmed(Boolean riskConfirmed) {
        this.riskConfirmed = riskConfirmed;
    }

    public Long getNodeCount() {
        return nodeCount;
    }

    public void setNodeCount(Long nodeCount) {
        this.nodeCount = nodeCount;
    }
}
