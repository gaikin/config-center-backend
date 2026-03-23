package com.configcenter.backend.infrastructure.db.control.page.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.configcenter.backend.infrastructure.db.model.BaseAuditDO;

@TableName("page_field_binding")
public class PageFieldBindingDO extends BaseAuditDO {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long pageResourceId;
    private String businessFieldCode;
    private Long pageElementId;
    private Boolean required;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPageResourceId() {
        return pageResourceId;
    }

    public void setPageResourceId(Long pageResourceId) {
        this.pageResourceId = pageResourceId;
    }

    public String getBusinessFieldCode() {
        return businessFieldCode;
    }

    public void setBusinessFieldCode(String businessFieldCode) {
        this.businessFieldCode = businessFieldCode;
    }

    public Long getPageElementId() {
        return pageElementId;
    }

    public void setPageElementId(Long pageElementId) {
        this.pageElementId = pageElementId;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }
}

