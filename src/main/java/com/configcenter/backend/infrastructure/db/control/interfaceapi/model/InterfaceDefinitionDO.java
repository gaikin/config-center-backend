package com.configcenter.backend.infrastructure.db.control.interfaceapi.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.configcenter.backend.infrastructure.db.model.BaseAuditDO;

@TableName("interface_definition")
public class InterfaceDefinitionDO extends BaseAuditDO {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String description;
    private String method;
    private String testPath;
    private String prodPath;
    private String path;
    private String url;
    private String ownerOrgId;
    private String status;
    private Long currentVersionId;
    private Integer timeoutMs;
    private Integer retryTimes;
    private String bodyTemplateJson;
    private String inputConfigJson;
    private String outputConfigJson;
    private String paramSourceSummary;
    private String responsePath;
    private Boolean maskSensitive;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getTestPath() {
        return testPath;
    }

    public void setTestPath(String testPath) {
        this.testPath = testPath;
    }

    public String getProdPath() {
        return prodPath;
    }

    public void setProdPath(String prodPath) {
        this.prodPath = prodPath;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOwnerOrgId() {
        return ownerOrgId;
    }

    public void setOwnerOrgId(String ownerOrgId) {
        this.ownerOrgId = ownerOrgId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCurrentVersionId() {
        return currentVersionId;
    }

    public void setCurrentVersionId(Long currentVersionId) {
        this.currentVersionId = currentVersionId;
    }

    public Integer getTimeoutMs() {
        return timeoutMs;
    }

    public void setTimeoutMs(Integer timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    public Integer getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(Integer retryTimes) {
        this.retryTimes = retryTimes;
    }

    public String getBodyTemplateJson() {
        return bodyTemplateJson;
    }

    public void setBodyTemplateJson(String bodyTemplateJson) {
        this.bodyTemplateJson = bodyTemplateJson;
    }

    public String getInputConfigJson() {
        return inputConfigJson;
    }

    public void setInputConfigJson(String inputConfigJson) {
        this.inputConfigJson = inputConfigJson;
    }

    public String getOutputConfigJson() {
        return outputConfigJson;
    }

    public void setOutputConfigJson(String outputConfigJson) {
        this.outputConfigJson = outputConfigJson;
    }

    public String getParamSourceSummary() {
        return paramSourceSummary;
    }

    public void setParamSourceSummary(String paramSourceSummary) {
        this.paramSourceSummary = paramSourceSummary;
    }

    public String getResponsePath() {
        return responsePath;
    }

    public void setResponsePath(String responsePath) {
        this.responsePath = responsePath;
    }

    public Boolean getMaskSensitive() {
        return maskSensitive;
    }

    public void setMaskSensitive(Boolean maskSensitive) {
        this.maskSensitive = maskSensitive;
    }
}
