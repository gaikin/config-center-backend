package com.configcenter.backend.infrastructure.db.permission.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.configcenter.backend.infrastructure.db.model.BaseAuditDO;

@TableName("cc_role")
public class RoleDO extends BaseAuditDO {

    @TableId(type = IdType.INPUT)
    private Long id;
    private String name;
    private String roleType;
    private String status;
    private String orgScopeId;

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

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrgScopeId() {
        return orgScopeId;
    }

    public void setOrgScopeId(String orgScopeId) {
        this.orgScopeId = orgScopeId;
    }
}
