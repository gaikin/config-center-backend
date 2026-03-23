package com.configcenter.backend.infrastructure.db.runtime.record.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.configcenter.backend.infrastructure.db.model.BaseAuditDO;
import java.time.LocalDateTime;

@TableName("prompt_trigger_log")
public class PromptTriggerLogDO extends BaseAuditDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long ruleId;

    private String ruleName;

    private Long pageResourceId;

    private String pageResourceName;

    private String orgId;

    private String orgName;

    private String promptMode;

    private String promptContentSummary;

    private Long sceneId;

    private String sceneName;

    private String triggerResult;

    private String reason;

    private LocalDateTime triggerAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
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

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getPromptMode() {
        return promptMode;
    }

    public void setPromptMode(String promptMode) {
        this.promptMode = promptMode;
    }

    public String getPromptContentSummary() {
        return promptContentSummary;
    }

    public void setPromptContentSummary(String promptContentSummary) {
        this.promptContentSummary = promptContentSummary;
    }

    public Long getSceneId() {
        return sceneId;
    }

    public void setSceneId(Long sceneId) {
        this.sceneId = sceneId;
    }

    public String getSceneName() {
        return sceneName;
    }

    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    public String getTriggerResult() {
        return triggerResult;
    }

    public void setTriggerResult(String triggerResult) {
        this.triggerResult = triggerResult;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getTriggerAt() {
        return triggerAt;
    }

    public void setTriggerAt(LocalDateTime triggerAt) {
        this.triggerAt = triggerAt;
    }
}
