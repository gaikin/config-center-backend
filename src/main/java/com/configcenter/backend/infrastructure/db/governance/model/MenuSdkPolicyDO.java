package com.configcenter.backend.infrastructure.db.governance.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.configcenter.backend.infrastructure.db.model.BaseAuditDO;
import java.time.LocalDateTime;

@TableName("menu_sdk_policy")
public class MenuSdkPolicyDO extends BaseAuditDO {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String menuCode;
    private String menuName;
    private Boolean promptGrayEnabled;
    private String promptGrayVersion;
    private String promptGrayOrgIdsJson;
    private Boolean jobGrayEnabled;
    private String jobGrayVersion;
    private String jobGrayOrgIdsJson;
    private LocalDateTime effectiveStart;
    private LocalDateTime effectiveEnd;
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMenuCode() {
        return menuCode;
    }

    public void setMenuCode(String menuCode) {
        this.menuCode = menuCode;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public Boolean getPromptGrayEnabled() {
        return promptGrayEnabled;
    }

    public void setPromptGrayEnabled(Boolean promptGrayEnabled) {
        this.promptGrayEnabled = promptGrayEnabled;
    }

    public String getPromptGrayVersion() {
        return promptGrayVersion;
    }

    public void setPromptGrayVersion(String promptGrayVersion) {
        this.promptGrayVersion = promptGrayVersion;
    }

    public String getPromptGrayOrgIdsJson() {
        return promptGrayOrgIdsJson;
    }

    public void setPromptGrayOrgIdsJson(String promptGrayOrgIdsJson) {
        this.promptGrayOrgIdsJson = promptGrayOrgIdsJson;
    }

    public Boolean getJobGrayEnabled() {
        return jobGrayEnabled;
    }

    public void setJobGrayEnabled(Boolean jobGrayEnabled) {
        this.jobGrayEnabled = jobGrayEnabled;
    }

    public String getJobGrayVersion() {
        return jobGrayVersion;
    }

    public void setJobGrayVersion(String jobGrayVersion) {
        this.jobGrayVersion = jobGrayVersion;
    }

    public String getJobGrayOrgIdsJson() {
        return jobGrayOrgIdsJson;
    }

    public void setJobGrayOrgIdsJson(String jobGrayOrgIdsJson) {
        this.jobGrayOrgIdsJson = jobGrayOrgIdsJson;
    }

    public LocalDateTime getEffectiveStart() {
        return effectiveStart;
    }

    public void setEffectiveStart(LocalDateTime effectiveStart) {
        this.effectiveStart = effectiveStart;
    }

    public LocalDateTime getEffectiveEnd() {
        return effectiveEnd;
    }

    public void setEffectiveEnd(LocalDateTime effectiveEnd) {
        this.effectiveEnd = effectiveEnd;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
