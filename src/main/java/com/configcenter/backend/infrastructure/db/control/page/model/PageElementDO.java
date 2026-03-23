package com.configcenter.backend.infrastructure.db.control.page.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.configcenter.backend.infrastructure.db.model.BaseAuditDO;

@TableName("page_element")
public class PageElementDO extends BaseAuditDO {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long pageResourceId;
    private String logicName;
    private String selector;
    private String selectorType;
    private String frameLocation;

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

    public String getLogicName() {
        return logicName;
    }

    public void setLogicName(String logicName) {
        this.logicName = logicName;
    }

    public String getSelector() {
        return selector;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }

    public String getSelectorType() {
        return selectorType;
    }

    public void setSelectorType(String selectorType) {
        this.selectorType = selectorType;
    }

    public String getFrameLocation() {
        return frameLocation;
    }

    public void setFrameLocation(String frameLocation) {
        this.frameLocation = frameLocation;
    }
}

