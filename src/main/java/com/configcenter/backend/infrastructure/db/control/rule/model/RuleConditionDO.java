package com.configcenter.backend.infrastructure.db.control.rule.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.configcenter.backend.infrastructure.db.model.BaseAuditDO;

@TableName("rule_condition")
public class RuleConditionDO extends BaseAuditDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long ruleId;

    private Long groupId;

    private String leftJson;

    private String operator;

    private String rightJson;

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

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getLeftJson() {
        return leftJson;
    }

    public void setLeftJson(String leftJson) {
        this.leftJson = leftJson;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getRightJson() {
        return rightJson;
    }

    public void setRightJson(String rightJson) {
        this.rightJson = rightJson;
    }
}
