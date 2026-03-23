package com.configcenter.backend.infrastructure.db.control.publish.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

@TableName("publish_operation_log")
public class PublishOperationLogDO {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String action;
    private String resourceType;
    private Long resourceId;
    private String resourceName;
    private String operator;
    private String effectiveScopeType;
    private String effectiveOrgIdsJson;
    private String effectiveScopeSummary;
    private String effectiveStartAt;
    private String effectiveEndAt;
    private String approvalTicketId;
    private String approvalSource;
    private String approvalStatus;
    private LocalDateTime createTime;
    private String createdBy;
    private LocalDateTime updateTime;
    private String updatedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getEffectiveScopeType() {
        return effectiveScopeType;
    }

    public void setEffectiveScopeType(String effectiveScopeType) {
        this.effectiveScopeType = effectiveScopeType;
    }

    public String getEffectiveOrgIdsJson() {
        return effectiveOrgIdsJson;
    }

    public void setEffectiveOrgIdsJson(String effectiveOrgIdsJson) {
        this.effectiveOrgIdsJson = effectiveOrgIdsJson;
    }

    public String getEffectiveScopeSummary() {
        return effectiveScopeSummary;
    }

    public void setEffectiveScopeSummary(String effectiveScopeSummary) {
        this.effectiveScopeSummary = effectiveScopeSummary;
    }

    public String getEffectiveStartAt() {
        return effectiveStartAt;
    }

    public void setEffectiveStartAt(String effectiveStartAt) {
        this.effectiveStartAt = effectiveStartAt;
    }

    public String getEffectiveEndAt() {
        return effectiveEndAt;
    }

    public void setEffectiveEndAt(String effectiveEndAt) {
        this.effectiveEndAt = effectiveEndAt;
    }

    public String getApprovalTicketId() {
        return approvalTicketId;
    }

    public void setApprovalTicketId(String approvalTicketId) {
        this.approvalTicketId = approvalTicketId;
    }

    public String getApprovalSource() {
        return approvalSource;
    }

    public void setApprovalSource(String approvalSource) {
        this.approvalSource = approvalSource;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
