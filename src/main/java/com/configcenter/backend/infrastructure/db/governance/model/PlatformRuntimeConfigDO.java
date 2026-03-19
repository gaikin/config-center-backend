package com.configcenter.backend.infrastructure.db.governance.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.configcenter.backend.infrastructure.db.model.BaseAuditDO;

@TableName("platform_runtime_config")
public class PlatformRuntimeConfigDO extends BaseAuditDO {

    @TableId(type = IdType.INPUT)
    private Long id;
    private String promptStableVersion;
    private String promptGrayDefaultVersion;
    private String jobStableVersion;
    private String jobGrayDefaultVersion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPromptStableVersion() {
        return promptStableVersion;
    }

    public void setPromptStableVersion(String promptStableVersion) {
        this.promptStableVersion = promptStableVersion;
    }

    public String getPromptGrayDefaultVersion() {
        return promptGrayDefaultVersion;
    }

    public void setPromptGrayDefaultVersion(String promptGrayDefaultVersion) {
        this.promptGrayDefaultVersion = promptGrayDefaultVersion;
    }

    public String getJobStableVersion() {
        return jobStableVersion;
    }

    public void setJobStableVersion(String jobStableVersion) {
        this.jobStableVersion = jobStableVersion;
    }

    public String getJobGrayDefaultVersion() {
        return jobGrayDefaultVersion;
    }

    public void setJobGrayDefaultVersion(String jobGrayDefaultVersion) {
        this.jobGrayDefaultVersion = jobGrayDefaultVersion;
    }
}
