package com.configcenter.backend.infrastructure.db.control.dataprocessor.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.configcenter.backend.infrastructure.db.model.BaseAuditDO;

@TableName("data_processor")
public class DataProcessorDO extends BaseAuditDO {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Integer paramCount;
    private String functionCode;
    private String status;
    private Integer usedByCount;

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

    public Integer getParamCount() {
        return paramCount;
    }

    public void setParamCount(Integer paramCount) {
        this.paramCount = paramCount;
    }

    public String getFunctionCode() {
        return functionCode;
    }

    public void setFunctionCode(String functionCode) {
        this.functionCode = functionCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getUsedByCount() {
        return usedByCount;
    }

    public void setUsedByCount(Integer usedByCount) {
        this.usedByCount = usedByCount;
    }
}
